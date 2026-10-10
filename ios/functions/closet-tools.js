import { z } from 'zod';
import { Timestamp } from 'firebase-admin/firestore';

// iOS generates UUID strings; Firestore generates 20-character alphanumeric document IDs.
const id = z.string().regex(/^[A-Za-z0-9_-]{1,128}$/);
const title = z.string().trim().min(1).max(120);
const tag = z.string().trim().min(1).max(60);
const tags = z.array(tag).max(20).default([]);
const category = z.enum(['Tops', 'Bottoms', 'One-pieces', 'Outerwear', 'Shoes', 'Accessories', 'Other']);
const size = z.enum(['Unknown', 'XXS', 'XS', 'S', 'M', 'L', 'XL', 'XXL']);
const fit = z.enum(['Unknown', 'Fitted', 'Slim', 'Regular', 'Relaxed', 'Oversized', 'Baggy']);
const length = z.enum(['Unknown', 'Cropped', 'Standard', 'Longline']);
const firstType = { Tops: 'T-shirt', Bottoms: 'Jeans', 'One-pieces': 'Dress', Outerwear: 'Jacket', Shoes: 'Sneakers', Accessories: 'Hat', Other: 'Other' };
const garmentFields = z.object({
  name: title, closetId: id, category, type: title.optional(), customType: z.string().trim().max(120).optional(),
  colors: tags.optional(), styles: tags.optional(), brand: z.string().trim().max(120).optional(),
  material: z.string().trim().max(120).optional(), size: size.optional(), fit: fit.optional(),
  length: length.optional(), isDirty: z.boolean().optional()
}).strict();
const wishFields = z.object({
  name: title, brand: z.string().trim().max(120).optional(), url: z.string().trim().max(2000).optional(),
  price: z.string().trim().max(30).optional(), currency: z.string().regex(/^[A-Z]{3}$/).optional(),
  notes: z.string().max(5000).optional()
}).strict();
const result = data => ({ content: [{ type: 'text', text: JSON.stringify(data) }] });
const fail = message => ({ isError: true, content: [{ type: 'text', text: message }] });
const clean = snap => ({ id: snap.id, ...snap.data() });
const ref = (root, collection, key) => root.collection(collection).doc(key);
const all = async (root, name) => (await root.collection(name).get()).docs.map(clean);
const exists = async doc => (await doc.get()).exists;

export function registerClosetTools(server, db, uid) {
  // Every reference is constructed from the verified token's UID; no caller-supplied path or UID.
  const root = db.collection('users').doc(uid);
  const add = (name, description, shape, handler, readOnly = false) => server.registerTool(name, {
    description, inputSchema: shape, annotations: { readOnlyHint: readOnly, destructiveHint: name.startsWith('delete_') }
  }, async args => {
    try { return result(await handler(args)); }
    catch (error) { console.error('MCP tool failed', name, error); return fail(error.message || 'Operation failed'); }
  });
  add('list_closets', 'List your closets with their IDs.', {}, async () => all(root, 'closets'), true);
  add('create_closet', 'Create a named closet. Returns its ID.', { name: title }, async ({ name }) => {
    const names = await all(root, 'closets');
    if (names.some(c => c.name.trim().toLowerCase() === name.toLowerCase())) throw Error('Closet name already exists');
    const doc = root.collection('closets').doc(); await doc.set({ name }); return { id: doc.id, name };
  });
  add('rename_closet', 'Rename a closet.', { closetId: id, name: title }, async ({ closetId, name }) => {
    const doc = ref(root, 'closets', closetId);
    if (!await exists(doc)) throw Error('Closet not found');
    if ((await all(root, 'closets')).some(c => c.id !== closetId && c.name.trim().toLowerCase() === name.toLowerCase())) throw Error('Closet name already exists');
    await doc.update({ name }); return { id: closetId, name };
  });
  add('list_items', 'List clothing and accessories. Optionally filter by closet, category, dirty status, or text. Maximum 200 results.', {
    closetId: id.optional(), category: category.optional(), isDirty: z.boolean().optional(), query: z.string().max(120).optional()
  }, async ({ closetId, category, isDirty, query }) => {
    let items = await all(root, 'items');
    if (closetId) items = items.filter(i => i.closetId === closetId);
    if (category) items = items.filter(i => i.category === category);
    if (isDirty !== undefined) items = items.filter(i => i.isDirty === isDirty);
    if (query?.trim()) {
      const words = query.toLowerCase().trim().split(/\s+/);
      items = items.filter(i => words.every(word => [i.name, i.type, i.customType, i.brand, i.material, i.fit, ...(i.colors || []), ...(i.styles || [])].some(s => String(s || '').toLowerCase().includes(word))));
    }
    return items.slice(0, 200);
  }, true);
  async function verifyCloset(closetId) { if (!await exists(ref(root, 'closets', closetId))) throw Error('Closet not found'); }
  async function prepareItem(input, original = {}) {
    await verifyCloset(input.closetId);
    const type = input.type || firstType[input.category];
    if (type === 'Other' && !input.customType?.trim()) throw Error('Custom type required for Other');
    return {
      closetId: input.closetId, name: input.name, category: input.category, type,
      customType: input.customType || '', colors: input.colors || [], styles: input.styles || [],
      brand: input.brand || '', material: input.material || '', size: input.size || 'Unknown',
      fit: input.fit || 'Unknown', length: input.length || 'Unknown', isDirty: input.isDirty ?? false,
      photoPath: original.photoPath || '', photoPaths: original.photoPaths || (original.photoPath ? [original.photoPath] : []),
      createdAt: original.createdAt || Timestamp.now(), updatedAt: Timestamp.now()
    };
  }
  add('create_items', 'Bulk-add up to 50 clothing items/accessories. Supply a closet ID; photos can be added in the iOS app later. Returns created IDs.', {
    items: z.array(garmentFields).min(1).max(50)
  }, async ({ items }) => {
    const prepared = [];
    for (const item of items) prepared.push(await prepareItem(item));
    const batch = db.batch(); const created = [];
    for (const data of prepared) { const doc = root.collection('items').doc(); batch.set(doc, data); created.push({ id: doc.id, name: data.name }); }
    await batch.commit(); return created;
  });
  add('update_item', 'Edit item fields; unspecified fields and its photo remain unchanged.', {
    itemId: id, changes: garmentFields.partial().refine(obj => Object.keys(obj).length > 0, 'Provide changes')
  }, async ({ itemId, changes }) => {
    const doc = ref(root, 'items', itemId), snapshot = await doc.get();
    if (!snapshot.exists) throw Error('Item not found');
    const current = snapshot.data();
    const merged = { ...current, ...changes };
    if (changes.category && changes.category !== current.category && !changes.type) {
      merged.type = firstType[changes.category]; merged.customType = '';
    }
    const updated = await prepareItem(merged, current);
    await doc.set(updated); return { id: itemId, ...updated };
  });
  add('delete_item', 'Delete an item and remove it from outfits. Photo deletion is not supported by this tool: delete photos from the app first.', { itemId: id }, async ({ itemId }) => {
    const doc = ref(root, 'items', itemId), snap = await doc.get();
    if (!snap.exists) throw Error('Item not found');
    if (snap.data().photoPath || snap.data().photoPaths?.length) throw Error('Item has photos; remove it in the iOS app instead');
    const outfits = (await root.collection('outfits').get()).docs.filter(o => (o.data().itemIds || []).includes(itemId));
    if (outfits.length >= 499) throw Error('Too many referencing outfits to delete atomically');
    const batch = db.batch(); batch.delete(doc);
    for (const o of outfits) batch.update(o.ref, { itemIds: o.data().itemIds.filter(x => x !== itemId), updatedAt: Timestamp.now() });
    await batch.commit(); return { deleted: itemId, updatedOutfits: outfits.length };
  });
  add('list_outfits', 'List saved outfits and their item IDs.', {}, async () => all(root, 'outfits'), true);
  add('save_outfit', 'Create or update an outfit from existing item IDs across your closets.', {
    outfitId: id.optional(), name: title, itemIds: z.array(id).max(100)
  }, async ({ outfitId, name, itemIds }) => {
    if (new Set(itemIds).size !== itemIds.length) throw Error('Duplicate item IDs');
    for (const itemId of itemIds) if (!await exists(ref(root, 'items', itemId))) throw Error(`Unknown item: ${itemId}`);
    const doc = outfitId ? ref(root, 'outfits', outfitId) : root.collection('outfits').doc();
    if (outfitId && !await exists(doc)) throw Error('Outfit not found');
    await doc.set({ name, itemIds, updatedAt: Timestamp.now() }); return { id: doc.id, name, itemIds };
  });
  add('delete_outfit', 'Delete an outfit without deleting its items.', { outfitId: id }, async ({ outfitId }) => {
    const doc = ref(root, 'outfits', outfitId);
    if (!await exists(doc)) throw Error('Outfit not found');
    await doc.delete(); return { deleted: outfitId };
  });
  add('list_wishlist', 'List your future fashion purchases.', {}, async () => all(root, 'wishlist'), true);
  add('create_wishes', 'Bulk-add up to 50 wishlist ideas. Price is stored as text, not floating point.', {
    wishes: z.array(wishFields).min(1).max(50)
  }, async ({ wishes }) => {
    const batch = db.batch(), created = [];
    for (const wish of wishes) {
      const doc = root.collection('wishlist').doc(); batch.set(doc, {
        name: wish.name, brand: wish.brand || '', url: wish.url || '', price: wish.price || '',
        currency: wish.currency || 'USD', notes: wish.notes || '', photoPath: '', createdAt: Timestamp.now()
      }); created.push({ id: doc.id, name: wish.name });
    }
    await batch.commit(); return created;
  });
  add('update_wish', 'Update a wishlist entry without changing its photo.', {
    wishId: id, changes: wishFields.partial().refine(obj => Object.keys(obj).length > 0, 'Provide changes')
  }, async ({ wishId, changes }) => {
    const doc = ref(root, 'wishlist', wishId);
    if (!await exists(doc)) throw Error('Wishlist entry not found');
    await doc.update(changes); return { id: wishId, ...changes };
  });
  add('delete_wish', 'Delete a wishlist entry without a photo. Use the app for photographed entries.', { wishId: id }, async ({ wishId }) => {
    const doc = ref(root, 'wishlist', wishId), snap = await doc.get();
    if (!snap.exists) throw Error('Wishlist entry not found');
    if (snap.data().photoPath) throw Error('Entry has a photo; delete it in the iOS app instead');
    await doc.delete(); return { deleted: wishId };
  });
  add('mark_purchased', 'Convert a wishlist entry to a clothing item in a closet (retaining any photo).', {
    wishId: id, closetId: id, category, type: title.optional()
  }, async ({ wishId, closetId, category, type }) => {
    await verifyCloset(closetId);
    const wishDoc = ref(root, 'wishlist', wishId), wish = await wishDoc.get();
    if (!wish.exists) throw Error('Wishlist entry not found');
    const w = wish.data();
    const item = await prepareItem({ name: w.name, brand: w.brand, closetId, category, type });
    item.photoPath = w.photoPath || '';
    const doc = root.collection('items').doc(), batch = db.batch();
    batch.set(doc, item); batch.delete(wishDoc); await batch.commit();
    return { itemId: doc.id, wishId };
  });
}
