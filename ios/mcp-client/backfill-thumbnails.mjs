#!/usr/bin/env node
// One-time/resumable migration: create 250x250 previews from existing Storage originals.
// Uses the local virtual-closet Keychain login; never stores an ID token or image in the repo.
import os from 'node:os';
import path from 'node:path';
import { spawnSync } from 'node:child_process';
import { pathToFileURL } from 'node:url';

const { idToken } = await import(pathToFileURL(path.join(os.homedir(), '.pi/agent/servers/virtual-closet/bridge.mjs')).href);
const project = 'callum-virtual-closet-2026';
const bucket = `${project}.firebasestorage.app`;
const token = await idToken();
const uid = JSON.parse(Buffer.from(token.split('.')[1], 'base64url').toString()).user_id;
if (!uid) throw Error('Not signed in to the closet');
const firestore = `https://firestore.googleapis.com/v1/projects/${project}/databases/(default)/documents/users/${uid}`;
const storage = `https://firebasestorage.googleapis.com/v0/b/${bucket}/o`;
const limit = process.env.VC_THUMBNAIL_LIMIT ? Number(process.env.VC_THUMBNAIL_LIMIT) : Infinity;
const force = process.env.VC_THUMBNAIL_FORCE === '1';
let created = 0;

async function request(url, options = {}) {
  const response = await fetch(url, { ...options, signal: AbortSignal.timeout(30000),
    headers: { ...(options.headers || {}), Authorization: `Bearer ${await idToken()}` } });
  if (!response.ok) throw Error(`${options.method || 'GET'} ${url.split('?')[0]}: HTTP ${response.status}: ${(await response.text()).slice(0, 300)}`);
  return response;
}
async function documents(collection) {
  let pageToken = '', result = [];
  do {
    const params = new URLSearchParams({ pageSize: '100' });
    if (pageToken) params.set('pageToken', pageToken);
    const page = await (await request(`${firestore}/${collection}?${params}`)).json();
    result.push(...(page.documents || []));
    pageToken = page.nextPageToken || '';
  } while (pageToken);
  return result;
}
function thumbnailName(original) {
  if (!/^(items|wishlist)\/[a-zA-Z0-9_.-]+\.[a-zA-Z0-9]+$/.test(original)) throw Error(`Unexpected photo path: ${original}`);
  return original.replace(/\.[^.]+$/, '-thumb.jpg');
}
async function makeThumbnail(original) {
  const name = thumbnailName(original);
  const key = `users/${uid}/${original}`;
  const full = Buffer.from(await (await request(`${storage}/${encodeURIComponent(key)}?alt=media`)).arrayBuffer());
  const converted = spawnSync('magick', ['-', '-auto-orient', '-resize', '250x250', '-gravity', 'center',
    '-background', 'white', '-alpha', 'remove', '-alpha', 'off', '-extent', '250x250',
    '-strip', '-quality', '82', 'jpeg:-'], { input: full, maxBuffer: 10 * 1024 * 1024 });
  if (converted.status !== 0) throw Error(`Image conversion failed for ${original}: ${converted.stderr.toString().slice(0, 200)}`);
  await request(`${storage}?name=${encodeURIComponent(`users/${uid}/${name}`)}`,
    { method: 'POST', headers: { 'Content-Type': 'image/jpeg' }, body: converted.stdout });
  created++;
  console.log(`Stored 250x250 preview: ${name}`);
  return name;
}
async function patch(doc, fields) {
  const params = new URLSearchParams({ 'currentDocument.updateTime': doc.updateTime });
  for (const field of Object.keys(fields)) params.append('updateMask.fieldPaths', field);
  await request(`https://firestore.googleapis.com/v1/${doc.name}?${params}`, {
    method: 'PATCH', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ fields })
  });
}
console.log('Checking clothing items…');
for (const doc of process.env.VC_THUMBNAIL_SKIP_ITEMS === '1' ? [] : await documents('items')) {
  if (created >= limit) break;
  const fields = doc.fields || {};
  const primary = fields.photoPath?.stringValue || '';
  const originals = fields.photoPaths?.arrayValue?.values?.map(v => v.stringValue).filter(Boolean) || (primary ? [primary] : []);
  if (!originals.length) continue;
  const existing = fields.thumbnailPaths?.arrayValue?.values?.map(v => v.stringValue) || [];
  const thumbnails = [];
  for (let index = 0; index < originals.length; index++) {
    thumbnails.push(!force && existing[index] || await makeThumbnail(originals[index]));
  }
  const main = originals.indexOf(primary);
  const thumbnailPath = thumbnails[main < 0 ? 0 : main];
  if (existing.length !== thumbnails.length || existing.some((value, index) => value !== thumbnails[index]) ||
      fields.thumbnailPath?.stringValue !== thumbnailPath) {
    await patch(doc, {
      thumbnailPath: { stringValue: thumbnailPath },
      thumbnailPaths: { arrayValue: { values: thumbnails.map(value => ({ stringValue: value })) } }
    });
    console.log(`Updated item ${doc.name.split('/').at(-1)} (${thumbnails.length} previews)`);
  }
}
console.log('Checking wishlist…');
for (const doc of await documents('wishlist')) {
  if (process.env.VC_THUMBNAIL_START_AFTER_WISH &&
      doc.name.split('/').at(-1) <= process.env.VC_THUMBNAIL_START_AFTER_WISH) continue;
  if (created >= limit) break;
  const original = doc.fields?.photoPath?.stringValue;
  if (!original || (!force && doc.fields?.thumbnailPath?.stringValue)) continue;
  const thumbnail = await makeThumbnail(original);
  await patch(doc, { thumbnailPath: { stringValue: thumbnail } });
  console.log(`Updated wish ${doc.name.split('/').at(-1)}`);
}
console.log(`Done: created ${created} 250x250 thumbnails.`);
