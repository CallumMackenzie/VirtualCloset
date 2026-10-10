#!/usr/bin/env node
import readline from 'node:readline/promises';
import { spawnSync } from 'node:child_process';
import { firebaseRequest, credential, idToken } from './bridge.mjs';

if (!process.stdin.isTTY) { console.error('Run this in an interactive terminal; never pass your password on the command line.'); process.exit(1); }
const rl = readline.createInterface({ input: process.stdin, output: process.stderr });
try {
  const email = (await rl.question('Virtual Closet email: ')).trim();
  rl.close();
  process.stderr.write('Password (hidden): ');
  spawnSync('stty', ['-echo'], { stdio: 'inherit' });
  let password;
  const hidden = readline.createInterface({ input: process.stdin, terminal: false });
  try { password = await hidden.question(''); }
  finally { hidden.close(); spawnSync('stty', ['echo'], { stdio: 'inherit' }); process.stderr.write('\n'); }
  const creating = process.argv.includes('--register');
  const result = await firebaseRequest(`https://identitytoolkit.googleapis.com/v1/accounts:${creating ? 'signUp' : 'signInWithPassword'}`,
    { email, password, returnSecureToken: true });
  password = undefined;
  credential('set', result.refreshToken);
  await idToken();
  console.error(`Signed in as ${result.email}. Refresh credential saved in macOS Keychain. Run pi mcp list to verify.`);
} catch (error) {
  console.error(`Sign-in failed: ${error.message}`); process.exitCode = 1;
} finally { rl.close(); }
