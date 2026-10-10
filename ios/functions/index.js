import { onRequest } from 'firebase-functions/v2/https';
import { initializeApp } from 'firebase-admin/app';
import { getAuth } from 'firebase-admin/auth';
import { getFirestore } from 'firebase-admin/firestore';
import { McpServer } from '@modelcontextprotocol/sdk/server/mcp.js';
import { StreamableHTTPServerTransport } from '@modelcontextprotocol/sdk/server/streamableHttp.js';
import { registerClosetTools } from './closet-tools.js';

initializeApp();

// Stateless requests are important for serverless instances: no session crosses user boundaries.
export const mcp = onRequest({ region: 'us-central1', maxInstances: 4, timeoutSeconds: 60, cors: false }, async (req, res) => {
  const match = /^Bearer ([^\s]+)$/.exec(req.headers.authorization || '');
  if (!match) {
    res.set('WWW-Authenticate', 'Bearer realm="virtual-closet"').status(401).json({ error: 'Firebase ID token required' });
    return;
  }
  let user;
  try { user = await getAuth().verifyIdToken(match[1], true); }
  catch { res.status(401).json({ error: 'Invalid or expired Firebase ID token' }); return; }
  if (req.method !== 'POST') { res.set('Allow', 'POST').status(405).end(); return; }

  const server = new McpServer({ name: 'virtual-closet', version: '0.1.0' });
  registerClosetTools(server, getFirestore(), user.uid);
  const transport = new StreamableHTTPServerTransport({ sessionIdGenerator: undefined, enableJsonResponse: true });
  try {
    await server.connect(transport);
    res.once('close', () => { transport.close(); server.close(); });
    await transport.handleRequest(req, res, req.body);
  } catch (error) {
    console.error('MCP request failed', error);
    await transport.close(); await server.close();
    if (!res.headersSent) res.status(500).json({ error: 'MCP request failed' });
  }
});
