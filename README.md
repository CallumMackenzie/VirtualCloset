# Virtual Closet

A SwiftUI iOS app for organizing clothing, shoes, accessories, outfits, and future purchases. Firebase Authentication, Cloud Firestore, and Cloud Storage provide private accounts, sync, and optional item photos. A Firebase-hosted MCP server offers read/write access outside the app, including bulk item and wishlist creation.

## Repository layout

- [`ios/`](ios/) — current iPhone app, Firebase rules and Functions MCP server. [Setup and developer notes](ios/README.md).
- [`desktop-java-prototype/`](desktop-java-prototype/) — archived original Java desktop/console prototype, including its [original README](desktop-java-prototype/README.md), design diagram, sample data, and tests.

## Current app

- One account can have multiple named closets; a closet is a container/location, **not** a clothing category. One Home closet is sufficient for most people, and the Closets tab opens it automatically when it is the only closet.
- Items have a category and type, colors, styles, brand, size, material, fit, length, dirty status, and optional photos. Front/back photos can be stored on one item.
- Browse and filter items, or combine items across closets into saved outfits. The outfit editor has search and filters for closet, category/type, color, brand, style, size, fit, and clean status.
- Save possible purchases with links, estimated prices, and notes; convert a wish to an owned item.
- Firestore listeners sync changes automatically. Pull down on a list to explicitly refresh from the server.
- The MCP server provides 15 read/write tools for closets, items, outfits, and wishlist entries. Bulk-create up to 50 items or wishes per call; it verifies a Firebase Auth ID token on **every request** and scopes access to that user's UID.

## Get started

1. Open [`ios/VirtualCloset2.xcodeproj`](ios/VirtualCloset2.xcodeproj) in Xcode 26.6 or newer.
2. Set up Firebase as described in [`ios/README.md`](ios/README.md), including a **local, untracked** `GoogleService-Info.plist`. This file and any personal clothing photos are intentionally excluded from git.
3. Run on an iOS simulator or device; create an email/password account.

The existing Firebase deployment uses project `callum-virtual-closet-2026`. The project-specific `.firebaserc` points to **live services**; review the target before deploying rules or functions. Hosting 2nd-gen Functions and new Storage buckets requires a billing-enabled Firebase plan. Do not commit service-account keys, refresh tokens, user photos, or import-progress files.

### Tests

```bash
cd ios
xcodebuild -project VirtualCloset2.xcodeproj -scheme VirtualCloset2 \
  -destination 'platform=iOS Simulator,name=iPhone 17 Pro' CODE_SIGNING_ALLOWED=NO test
(cd functions && npm ci && npm test)
```

The Java prototype is preserved for reference, not used by the Swift app; its local JSON format is not automatically migrated.
