# Virtual Closet 2 (iOS)

SwiftUI + Firebase MVP: email/password accounts; private closets; clothing, shoes and accessories with optional photos; outfit catalogue; searchable/filterable items; wishlist and purchase conversion. Requires iOS 26.5 as configured by the original Xcode template. Open `VirtualCloset2.xcodeproj` in Xcode 26.6 or newer.

## Firebase setup status

The existing deployment is Firebase project **`callum-virtual-closet-2026`** with iOS bundle ID `com.camackenzie.VirtualCloset2`. The Firebase config plist is **not included in this repository**. Obtain your own access to the Firebase project and download its iOS `GoogleService-Info.plist` from the Firebase console, or run:

```bash
firebase apps:sdkconfig IOS 1:56905891657:ios:45b1a5c30e8d02db00e498 \
  --project callum-virtual-closet-2026 --out VirtualCloset2/GoogleService-Info.plist
```

Place the plist in `ios/VirtualCloset2/` (the Xcode app target uses a synchronized file group). It is ignored by git. Without it the app displays a setup message. Firestore's `(default)` database is in **nam5 (North America)**. `firebase.json` and `.firebaserc` target **live** services; review deployments carefully.

**Service status:**

1. **Done:** Email/Password is enabled in [Authentication → Sign-in method](https://console.firebase.google.com/project/callum-virtual-closet-2026/authentication/providers) and verified via the Identity Toolkit config API.
2. **Done:** The default Firebase Storage bucket `callum-virtual-closet-2026.firebasestorage.app` was created in `US-CENTRAL1`, and `storage.rules` were deployed. Photo uploads are now configured. Blaze billing is enabled for this project.
3. Build in Xcode; Swift Package Manager fetches Firebase. Run on an iOS simulator or device. Firestore rules can be redeployed with `firebase deploy --only firestore:rules` from this directory.

## Remote MCP (deployed)

`functions/` contains a Firebase Functions v2 stateless Streamable HTTP MCP server with read/write tools for closets, items (including bulk create of up to 50), outfits, and wishlist (including bulk create and purchase conversion). It uses Firebase Admin SDK only under the **UID verified from each request's Firebase Auth ID token**. No client-supplied UID or arbitrary document path is accepted. Deletion of entries with photos is intentionally refused by MCP so that Storage files aren't orphaned; use the iOS app for these. MCP does not upload photos yet.

Run `cd functions && npm ci && npm test` for unit tests. The `mcp` 2nd-gen function is deployed in `us-central1` at `https://us-central1-callum-virtual-closet-2026.cloudfunctions.net/mcp` (Cloud Run URL: `https://mcp-vytmjbixlq-uc.a.run.app`). Redeploy from this directory with `firebase deploy --only functions`. It accepts `POST` only and requires `Authorization: Bearer <Firebase Auth ID token>` on **every request**. It rejects invalid, expired, or revoked tokens. HTTP access is public at the infrastructure layer, but unauthenticated requests get HTTP 401; do not make an Admin SDK endpoint without this verification. Deployment was smoke-tested with a disposable Firebase Auth user: 15 tools listed successfully; `create_wishes`, `list_wishlist`, and `delete_wish` worked against Firestore, and the disposable user was deleted afterward.

### Long-running Pi connection (optional, macOS)

`mcp-client/` contains a local **stdio bridge to the deployed MCP server**. To install for Pi on macOS, copy `bridge.mjs`, `login.mjs`, and `keychain.swift` to `~/.pi/agent/servers/virtual-closet/`, compile `keychain.swift` to `keychain` in that directory with `swiftc`, and create a mode-600 `config.json` there with `{"url":"https://us-central1-callum-virtual-closet-2026.cloudfunctions.net/mcp","apiKey":"<your Firebase Web API key>"}`. Then register it globally with `pi mcp add virtual-closet -- /usr/bin/env node ~/.pi/agent/servers/virtual-closet/bridge.mjs` (expand `~` to your actual home path if your shell or Pi does not). The bridge stores a revocable Firebase **refresh credential** in macOS Keychain and automatically mints fresh, short-lived ID tokens before MCP requests. No non-expiring Firebase ID token exists. Never put a refresh credential or account password in the repository or Pi configuration.

**One-time step requiring your own account credentials, done only in your local terminal:**

```bash
node ~/.pi/agent/servers/virtual-closet/login.mjs              # sign in to an existing account
node ~/.pi/agent/servers/virtual-closet/login.mjs --register   # OR create a new account
pi mcp list  # virtual-closet should show connected / 15 tools
```

The password prompt is hidden; do not paste passwords into a conversation. Until you sign in, Pi shows this server as disconnected. Reload a running Pi session with `/reload`. The refresh credential normally works across long sessions but is **revocable** (for example on account deletion or security events); simply rerun the login helper then. To sign out of Pi locally, run `~/.pi/agent/servers/virtual-closet/keychain delete` and restart/reload Pi. This personal-client approach is not a general OAuth/PKCE authorization service for other users or MCP clients.

## Data model

`users/{uid}/closets/{closetId}`, `/items/{itemId}`, `/outfits/{outfitId}`, `/wishlist/{wishId}`. Images live in Storage under `users/{uid}/items/` or `users/{uid}/wishlist/`; documents store relative paths. Converted wishlist photos keep their original Storage path. Item references use stable IDs, not item names or attributes.

The Java app used a free-text list of types; this app uses one category + one primary type, with `Other` for custom types. Styles and colors are comma-separated tags. Fit and length are separate optional attributes; accessories aren't forced to have either. Photo is optional but encouraged during entry. `photoPath` / `photoPaths` point to retained originals (including imported front/back galleries); `thumbnailPath` / `thumbnailPaths` point to separate, square 250×250 JPEG icons. Every current image preview uses only the icon paths. New photos upload both variants, and deleting or converting an item handles both. Old records without icons show placeholders rather than downloading originals. To backfill icons for existing images while authenticated with the local Keychain bridge, run `node mcp-client/backfill-thumbnails.mjs` from this directory. This migration is resumable and updates only photo-preview fields.

## Known MVP constraints

- Search and filtering happen on the downloaded items on-device; this is suitable for personal closets, not a large catalogue. Firestore's offline cache enables cached browsing/queued edits, but the photo URL must be available for an uncached image.
- Closet/item deletion updates referencing outfits in a Firestore batch; a single deletion involving 500 or more document writes is rejected rather than partially applied. For large accounts, move this into a trusted server job.
- Account deletion requires a recent login, deletes owned data and referenced images, then deletes the Firebase Auth account. If a network failure interrupts the sequence, sign back in and retry. For production, use a trusted backend deletion job to guarantee cleanup and handle orphaned photos.
- Camera capture, advanced size systems (e.g. numeric shoe sizes), image caching, and legacy Java JSON import are not part of this MVP. Photos are selected from the photo library.
- Rules currently enforce ownership and a 10 MB JPEG upload limit, not detailed field-level validation or referential integrity. Add rules/emulator integration tests and server-side cleanup before public launch.

## Validation

`xcodebuild -project VirtualCloset2.xcodeproj -scheme VirtualCloset2 -destination 'platform=iOS Simulator,name=iPhone 17 Pro' CODE_SIGNING_ALLOWED=NO test`

Unit tests cover basic type options, search, identity, tag parsing, and exact 250-pixel JPEG dimensions. MCP tests cover its protocol and item validation; the deployed MCP was also smoke-tested with disposable Firebase Auth users. Security rules, photo uploads, and account deletion still need broader emulator/device integration tests before public launch.
