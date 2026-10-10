import test from 'node:test';
import assert from 'node:assert/strict';
import { registerClosetTools } from './closet-tools.js';
import { McpServer } from '@modelcontextprotocol/sdk/server/mcp.js';
import { Client } from '@modelcontextprotocol/sdk/client/index.js';
import { InMemoryTransport } from '@modelcontextprotocol/sdk/inMemory.js';

function fakeStore() {
  const records = new Map(), calls = new Map();
  let counter = 0;
  const db = {
    collection(name) {
      assert.equal(name, 'users');
      return { doc(uid) {
        assert.equal(uid, 'signed-in-user');
        return { collection(bucket) {
          const prefix = `users/${uid}/${bucket}/`;
          return {
            doc(key = `generated${++counter}`) {
              const path = prefix + key;
              return { id: key, get: async () => ({ id: key, exists: records.has(path), data: () => records.get(path) }),
                set: async data => { records.set(path, data); }, update: async data => { records.set(path, { ...records.get(path), ...data }); },
                delete: async () => { records.delete(path); } };
            },
            get: async () => ({ docs: [...records].filter(([key]) => key.startsWith(prefix)).map(([key, value]) => ({ id: key.slice(prefix.length), data: () => value })) })
          };
        } };
      } };
    },
    batch() { const changes = []; return { set(doc, data) { changes.push(() => doc.set(data)); }, delete(doc) { changes.push(() => doc.delete()); },
      update(doc, data) { changes.push(() => doc.update(data)); }, async commit() { for (const change of changes) await change(); } }; }
  };
  const server = { registerTool(name, config, callback) { calls.set(name, { config, callback }); } };
  registerClosetTools(server, db, 'signed-in-user');
  async function invoke(name, input) { const result = await calls.get(name).callback(input); return result.isError ? result : JSON.parse(result.content[0].text); }
  return { invoke, calls, records, db };
}

test('MCP protocol lists tools and calls list_closets', async () => {
  const { db } = fakeStore();
  const server = new McpServer({ name: 'test-closet', version: '0.1.0' });
  registerClosetTools(server, db, 'signed-in-user');
  const client = new Client({ name: 'test-client', version: '0.1.0' });
  const [serverTransport, clientTransport] = InMemoryTransport.createLinkedPair();
  try {
    await Promise.all([server.connect(serverTransport), client.connect(clientTransport)]);
    const tools = await client.listTools();
    assert.ok(tools.tools.some(t => t.name === 'create_items'));
    assert.ok(tools.tools.some(t => t.name === 'list_closets'));
    const response = await client.callTool({ name: 'list_closets', arguments: {} });
    assert.deepEqual(JSON.parse(response.content[0].text), []);
  } finally { await client.close(); await server.close(); }
});

test('bulk item entry uses iOS fields and returns generated IDs', async () => {
  const { invoke } = fakeStore();
  const closet = await invoke('create_closet', { name: 'Bedroom' });
  const created = await invoke('create_items', { items: [{ name: 'Blue shirt', closetId: closet.id, category: 'Tops', type: 'Shirt', colors: ['Blue'] },
    { name: 'Wool scarf', closetId: closet.id, category: 'Accessories', type: 'Scarf' }] });
  assert.equal(created.length, 2);
  const items = await invoke('list_items', {});
  assert.equal(items[0].category, 'Tops');
  assert.equal(items[0].fit, 'Unknown');
  assert.equal(items[1].category, 'Accessories');
  assert.equal((await invoke('list_items', { query: 'scarf' })).length, 1);
  assert.equal((await invoke('list_items', { closetId: 'nonexistent' })).length, 0);
});

test('outfit refuses foreign/missing item IDs; empty wishlist can be bulk created', async () => {
  const { invoke } = fakeStore();
  const failure = await invoke('save_outfit', { name: 'Look', itemIds: ['other-users-item'] });
  assert.equal(failure.isError, true);
  const wishes = await invoke('create_wishes', { wishes: [{ name: 'Black boots' }, { name: 'Belt', currency: 'CAD', price: '29.99' }] });
  assert.equal(wishes.length, 2);
  assert.equal((await invoke('list_wishlist', {})).length, 2);
});

test('write tools marked writable; delete tools marked destructive', () => {
  const { calls } = fakeStore();
  assert.equal(calls.get('list_items').config.annotations.readOnlyHint, true);
  assert.equal(calls.get('create_items').config.annotations.readOnlyHint, false);
  assert.equal(calls.get('delete_item').config.annotations.destructiveHint, true);
  // Both UUIDs (iOS) and generated Firestore IDs are accepted.
  const schema = calls.get('save_outfit').config.inputSchema.outfitId;
  assert.equal(schema.safeParse('31de7239-3b1c-4257-9611-74008e1ec9e2').success, true);
  assert.equal(schema.safeParse('generatedXYZ123').success, true);
  assert.equal(schema.safeParse('../other').success, false);
});
