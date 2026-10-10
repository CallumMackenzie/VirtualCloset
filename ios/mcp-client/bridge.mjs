#!/usr/bin/env node
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { spawnSync } from 'node:child_process';
import readline from 'node:readline';

const directory = path.dirname(fileURLToPath(import.meta.url));
const config = JSON.parse(fs.readFileSync(path.join(directory, 'config.json'), 'utf8'));
const keychain = path.join(directory, 'keychain');
let cached = null;

function credential(mode, value) {
  const result = spawnSync(keychain, [mode], { input: value, encoding: 'utf8', timeout: 12000 });
  if (result.status !== 0) throw Error(result.stderr?.trim() || 'Sign in with: node login.mjs');
  return result.stdout.trim();
}
async function firebaseRequest(endpoint, data, form = false) {
  const response = await fetch(`${endpoint}?key=${encodeURIComponent(config.apiKey)}`, {
    method: 'POST', headers: { 'Content-Type': form ? 'application/x-www-form-urlencoded' : 'application/json' },
    body: form ? new URLSearchParams(data) : JSON.stringify(data), signal: AbortSignal.timeout(15000)
  });
  const result = await response.json();
  if (!response.ok) throw Error(result.error?.message || `Firebase sign-in failed (${response.status})`);
  return result;
}
export async function idToken(force = false) {
  if (!force && cached && cached.until > Date.now() + 5 * 60_000) return cached.token;
  const refresh = credential('get');
  const response = await firebaseRequest('https://securetoken.googleapis.com/v1/token',
    { grant_type: 'refresh_token', refresh_token: refresh }, true);
  if (response.refresh_token && response.refresh_token !== refresh) credential('set', response.refresh_token);
  cached = { token: response.id_token, until: Date.now() + Number(response.expires_in) * 1000 };
  return cached.token;
}
async function remote(request, retry = true) {
  const token = await idToken();
  const response = await fetch(config.url, {
    method: 'POST', headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json', Accept: 'application/json, text/event-stream' },
    body: JSON.stringify(request), signal: AbortSignal.timeout(60000)
  });
  if (response.status === 401 && retry) { cached = null; await idToken(true); return remote(request, false); }
  if (!response.ok) throw Error(`Remote MCP returned HTTP ${response.status}: ${(await response.text()).slice(0, 180)}`);
  const payload = await response.json();
  if (payload.error) throw Error(payload.error.message || 'Remote MCP error');
  return payload;
}
const replies = { initialize: async req => ({ protocolVersion: req.params?.protocolVersion || '2025-03-26', capabilities: { tools: {} }, serverInfo: { name: 'virtual-closet-keychain-bridge', version: '0.1.0' } }),
  ping: async () => ({}),
  'tools/list': async req => (await remote({ jsonrpc: '2.0', id: req.id, method: 'tools/list', params: req.params || {} })).result,
  'tools/call': async req => (await remote({ jsonrpc: '2.0', id: req.id, method: 'tools/call', params: req.params })).result };

if (process.argv[1] && path.resolve(process.argv[1]) === fileURLToPath(import.meta.url)) {
  const lines = readline.createInterface({ input: process.stdin, crlfDelay: Infinity });
  for await (const line of lines) {
    let req;
    try { req = JSON.parse(line); } catch { continue; }
    if (req.id === undefined) continue; // MCP notifications do not receive replies.
    try {
      const handler = replies[req.method];
      if (!handler) throw Error(`Unsupported MCP method: ${req.method}`);
      const result = await handler(req);
      process.stdout.write(JSON.stringify({ jsonrpc: '2.0', id: req.id, result }) + '\n');
    } catch (error) {
      process.stdout.write(JSON.stringify({ jsonrpc: '2.0', id: req.id, error: { code: -32603, message: error.message } }) + '\n');
    }
  }
}
export { firebaseRequest, credential };
