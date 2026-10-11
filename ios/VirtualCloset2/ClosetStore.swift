import Foundation
import Combine
import UIKit
import FirebaseAuth
import FirebaseFirestore
import FirebaseStorage

@MainActor
final class ClosetStore: ObservableObject {
    @Published private(set) var user: User?
    @Published private(set) var ready = false
    @Published private(set) var closets: [Closet] = []
    @Published private(set) var items: [Garment] = []
    @Published private(set) var outfits: [Outfit] = []
    @Published private(set) var wishes: [Wish] = []
    @Published var error: String?
    private var authListener: AuthStateDidChangeListenerHandle?
    private var listeners: [ListenerRegistration] = []
    private let db = Firestore.firestore()
    private let storage = Storage.storage()
    private let thumbnailCache = ThumbnailCache()
    private var thumbnailRefreshes: [String: (id: UUID, task: Task<UIImage?, Never>)] = [:]

    init() {
        authListener = Auth.auth().addStateDidChangeListener { [weak self] _, user in
            Task { @MainActor [weak self] in self?.switchUser(user) }
        }
    }
    deinit { if let authListener { Auth.auth().removeStateDidChangeListener(authListener) } }
    private func switchUser(_ next: User?) {
        listeners.forEach { $0.remove() }; listeners.removeAll()
        closets = []; items = []; outfits = []; wishes = []
        user = next; ready = true
        guard let uid = next?.uid else { return }
        let root = db.collection("users").document(uid)
        listeners.append(root.collection("closets").addSnapshotListener { [weak self] snap, error in
            Task { @MainActor [weak self] in guard self?.user?.uid == uid else { return }; self?.closets = snap?.documents.compactMap(Closet.init(snapshot:)) ?? []; self?.report(error) }
        })
        listeners.append(root.collection("items").addSnapshotListener { [weak self] snap, error in
            Task { @MainActor [weak self] in guard self?.user?.uid == uid else { return }; self?.items = snap?.documents.compactMap(Garment.init(snapshot:)) ?? []; self?.report(error) }
        })
        listeners.append(root.collection("outfits").addSnapshotListener { [weak self] snap, error in
            Task { @MainActor [weak self] in guard self?.user?.uid == uid else { return }; self?.outfits = snap?.documents.compactMap(Outfit.init(snapshot:)) ?? []; self?.report(error) }
        })
        listeners.append(root.collection("wishlist").addSnapshotListener { [weak self] snap, error in
            Task { @MainActor [weak self] in guard self?.user?.uid == uid else { return }; self?.wishes = snap?.documents.compactMap(Wish.init(snapshot:)) ?? []; self?.report(error) }
        })
    }
    func report(_ error: Error?) { if let error { self.error = error.localizedDescription } }

    // Snapshot listeners update automatically; pull-to-refresh explicitly checks the server.
    func refresh() async {
        guard let uid = user?.uid else { return }
        let root = db.collection("users").document(uid)
        do {
            async let closetsSnapshot = root.collection("closets").getDocuments(source: .server)
            async let itemsSnapshot = root.collection("items").getDocuments(source: .server)
            async let outfitsSnapshot = root.collection("outfits").getDocuments(source: .server)
            async let wishlistSnapshot = root.collection("wishlist").getDocuments(source: .server)
            let (c, i, o, w) = try await (closetsSnapshot, itemsSnapshot, outfitsSnapshot, wishlistSnapshot)
            guard user?.uid == uid else { return }
            closets = c.documents.compactMap(Closet.init(snapshot:))
            items = i.documents.compactMap(Garment.init(snapshot:))
            outfits = o.documents.compactMap(Outfit.init(snapshot:))
            wishes = w.documents.compactMap(Wish.init(snapshot:))
        } catch { report(error) }
    }
    private func collection(_ name: String) throws -> CollectionReference {
        guard let uid = user?.uid else { throw StoreError.signedOut }
        return db.collection("users").document(uid).collection(name)
    }
    enum StoreError: LocalizedError {
        case signedOut, duplicateCloset, invalidInput, tooManyItems
        var errorDescription: String? {
            switch self {
            case .signedOut: "Sign in to continue."
            case .duplicateCloset: "A closet with that name already exists."
            case .invalidInput: "Check the required fields and try again."
            case .tooManyItems: "This closet is too large to delete in one operation."
            }
        }
    }
    func register(email: String, password: String) async throws {
        let result = try await Auth.auth().createUser(withEmail: email.trimmed, password: password)
        try await db.collection("users").document(result.user.uid).setData(["createdAt": FieldValue.serverTimestamp()])
    }
    func signIn(email: String, password: String) async throws {
        _ = try await Auth.auth().signIn(withEmail: email.trimmed, password: password)
    }
    func resetPassword(email: String) async throws { try await Auth.auth().sendPasswordReset(withEmail: email.trimmed) }
    func signOut() throws { try Auth.auth().signOut() }
    func deleteAccount() async throws {
        guard let user else { throw StoreError.signedOut }
        // Firebase Auth requires a recent sign-in for deletion. Check before touching data.
        guard let last = user.metadata.lastSignInDate, Date().timeIntervalSince(last) < 240 else {
            throw AccountDeletionError.recentLoginRequired
        }
        let root = db.collection("users").document(user.uid)
        let names = ["items", "outfits", "closets", "wishlist"]
        for name in names {
            // Each batch is below Firestore's 500-write limit.
            var documents = try await root.collection(name).getDocuments(source: .server).documents
            if name == "items" || name == "wishlist" {
                for document in documents {
                    let data = document.data()
                    let paths = (data["photoPaths"] as? [String] ?? []) +
                        (data["thumbnailPaths"] as? [String] ?? []) +
                        [data["photoPath"] as? String ?? "", data["thumbnailPath"] as? String ?? ""]
                    for path in Set(paths) { await removePhoto(path) }
                }
            }
            while !documents.isEmpty {
                let batch = db.batch()
                for doc in documents.prefix(400) { batch.deleteDocument(doc.reference) }
                try await batch.commit()
                documents.removeFirst(min(400, documents.count))
            }
        }
        try await root.delete()
        try await user.delete()
        for (key, pending) in thumbnailRefreshes where key.hasPrefix("\(user.uid)/") { pending.task.cancel() }
        await thumbnailCache.removeAccount(user.uid)
    }
    enum AccountDeletionError: LocalizedError {
        case recentLoginRequired
        var errorDescription: String? { "For security, sign out and sign back in before deleting your account." }
    }

    func saveCloset(_ closet: Closet) async throws {
        guard !closet.name.trimmed.isEmpty else { throw StoreError.invalidInput }
        guard !closets.contains(where: { $0.id != closet.id && $0.name.trimmed.localizedCaseInsensitiveCompare(closet.name.trimmed) == .orderedSame }) else { throw StoreError.duplicateCloset }
        try await collection("closets").document(closet.id).setData(["name": closet.name.trimmed])
    }
    func deleteCloset(_ closet: Closet) async throws {
        let related = try await collection("items").whereField("closetId", isEqualTo: closet.id).getDocuments(source: .server).documents.compactMap(Garment.init(snapshot:))
        let ids = Set(related.map(\.id))
        let allOutfits = try await collection("outfits").getDocuments(source: .server).documents.compactMap(Outfit.init(snapshot:))
        let changed = allOutfits.filter { $0.itemIds.contains(where: ids.contains) }
        guard related.count + changed.count + 1 <= 499 else { throw StoreError.tooManyItems }
        let batch = db.batch()
        for item in related { batch.deleteDocument(try collection("items").document(item.id)) }
        for var outfit in changed {
            outfit.itemIds.removeAll { ids.contains($0) }
            batch.setData(outfit.data, forDocument: try collection("outfits").document(outfit.id))
        }
        batch.deleteDocument(try collection("closets").document(closet.id))
        try await batch.commit()
        for item in related { await removeItemPhotos(item) }
    }
    func saveItem(_ item: Garment, photo: UIImage? = nil) async throws {
        guard !item.name.trimmed.isEmpty, !item.closetId.isEmpty,
              item.type != "Other" || !item.customType.trimmed.isEmpty else { throw StoreError.invalidInput }
        var updated = item
        updated.name = item.name.trimmed; updated.updatedAt = .now
        if let photo {
            let pair = try await uploadPhotoPair(photo, directory: "items", id: item.id)
            updated.photoPath = pair.original
            updated.thumbnailPath = pair.thumbnail
            let remaining = item.photoPaths.filter { $0 != item.photoPath }
            updated.photoPaths = [pair.original] + remaining
            updated.thumbnailPaths = [pair.thumbnail] + remaining.map { path in
                guard let index = item.photoPaths.firstIndex(of: path), item.thumbnailPaths.indices.contains(index) else { return "" }
                return item.thumbnailPaths[index]
            }
        }
        do {
            try await collection("items").document(item.id).setData(updated.data)
            if photo != nil {
                await removePhoto(item.photoPath)
                await removePhoto(item.thumbnailPath)
            }
        } catch {
            if photo != nil {
                await removePhoto(updated.photoPath)
                await removePhoto(updated.thumbnailPath)
            }
            throw error
        }
    }
    func deleteItem(_ item: Garment) async throws {
        let allOutfits = try await collection("outfits").getDocuments(source: .server).documents.compactMap(Outfit.init(snapshot:))
        guard allOutfits.count < 499 else { throw StoreError.tooManyItems }
        let batch = db.batch()
        batch.deleteDocument(try collection("items").document(item.id))
        for var outfit in allOutfits where outfit.itemIds.contains(item.id) {
            outfit.itemIds.removeAll { $0 == item.id }; outfit.updatedAt = .now
            batch.setData(outfit.data, forDocument: try collection("outfits").document(outfit.id))
        }
        try await batch.commit()
        await removeItemPhotos(item)
    }
    func saveOutfit(_ outfit: Outfit) async throws {
        guard !outfit.name.trimmed.isEmpty else { throw StoreError.invalidInput }
        var updated = outfit; updated.name = outfit.name.trimmed; updated.updatedAt = .now
        var seen = Set<String>()
        updated.itemIds = outfit.itemIds.filter { id in seen.insert(id).inserted && items.contains(where: { $0.id == id }) }
        try await collection("outfits").document(updated.id).setData(updated.data)
    }
    func deleteOutfit(_ outfit: Outfit) async throws { try await collection("outfits").document(outfit.id).delete() }
    func saveWish(_ wish: Wish, photo: UIImage? = nil) async throws {
        guard !wish.name.trimmed.isEmpty else { throw StoreError.invalidInput }
        var updated = wish; updated.name = wish.name.trimmed
        if let photo {
            let pair = try await uploadPhotoPair(photo, directory: "wishlist", id: wish.id)
            updated.photoPath = pair.original
            updated.thumbnailPath = pair.thumbnail
        }
        do {
            try await collection("wishlist").document(updated.id).setData(updated.data)
            if photo != nil {
                await removePhoto(wish.photoPath)
                await removePhoto(wish.thumbnailPath)
            }
        } catch {
            if photo != nil {
                await removePhoto(updated.photoPath)
                await removePhoto(updated.thumbnailPath)
            }
            throw error
        }
    }
    func deleteWish(_ wish: Wish) async throws {
        try await collection("wishlist").document(wish.id).delete()
        await removePhoto(wish.photoPath)
        await removePhoto(wish.thumbnailPath)
    }
    // Convert with a single Firestore batch. Reuse the photo path; don't delete its file.
    func purchase(_ wish: Wish, as item: Garment) async throws {
        guard !item.closetId.isEmpty, !item.name.trimmed.isEmpty else { throw StoreError.invalidInput }
        var item = item; item.photoPath = wish.photoPath
        item.thumbnailPath = wish.thumbnailPath
        item.photoPaths = wish.photoPath.map { [$0] } ?? []
        item.thumbnailPaths = wish.thumbnailPath.map { [$0] } ?? []
        let batch = db.batch()
        batch.setData(item.data, forDocument: try collection("items").document(item.id))
        batch.deleteDocument(try collection("wishlist").document(wish.id))
        try await batch.commit()
    }
    func cachedThumbnail(_ path: String) async -> UIImage? {
        guard let uid = user?.uid, path.hasSuffix("-thumb.jpg") else { return nil }
        return await thumbnailCache.image(for: uid, path: path)
    }

    // Called only when a preview appears. Existing image remains visible while metadata is checked.
    // The image bytes are downloaded only if Storage reports a newer modification date.
    func refreshedThumbnail(_ path: String) async -> UIImage? {
        guard let uid = user?.uid, path.hasSuffix("-thumb.jpg") else { return nil }
        let key = "\(uid)/\(path)"
        if let pending = thumbnailRefreshes[key] { return await pending.task.value }
        let id = UUID()
        let task = Task { [weak self] () -> UIImage? in
            guard let self else { return nil }
            let reference = storage.reference(withPath: "users/\(uid)/\(path)")
            do {
                let metadata = try await reference.getMetadata()
                guard !Task.isCancelled,
                      await thumbnailCache.needsRefresh(for: uid, path: path, remoteDate: metadata.updated) else { return nil }
                let bytes = try await reference.data(maxSize: 2 * 1024 * 1024)
                guard !Task.isCancelled else { return nil }
                return await thumbnailCache.save(bytes, for: uid, path: path, updatedAt: metadata.updated)
            } catch { return nil } // Offline or unavailable: leave the last known image in place.
        }
        thumbnailRefreshes[key] = (id, task)
        let updated = await task.value
        if thumbnailRefreshes[key]?.id == id { thumbnailRefreshes.removeValue(forKey: key) }
        return updated
    }
    // Keep the original separately; never request it for list or gallery previews.
    private func uploadPhotoPair(_ image: UIImage, directory: String, id: String) async throws -> (original: String, thumbnail: String) {
        guard let full = image.jpegData(compressionQuality: 0.7),
              let thumbnail = PhotoIcon.jpegData(from: image) else { throw StoreError.invalidInput }
        let base = "\(directory)/\(id)-\(UUID().uuidString)"
        let original = "\(base).jpg", icon = "\(base)-thumb.jpg"
        try await upload(full, path: original)
        do { try await upload(thumbnail, path: icon) }
        catch { await removePhoto(original); throw error }
        return (original, icon)
    }
    private func upload(_ bytes: Data, path: String) async throws {
        guard let uid = user?.uid else { throw StoreError.signedOut }
        let key = "users/\(uid)/\(path)"
        let metadata = StorageMetadata()
        metadata.contentType = "image/jpeg"
        metadata.cacheControl = "private, max-age=31536000, immutable"
        try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<Void, Error>) in
            storage.reference(withPath: key).putData(bytes, metadata: metadata) { _, error in
                if let error { continuation.resume(throwing: error) }
                else { continuation.resume(returning: ()) }
            }
        }
    }
    private func removeItemPhotos(_ item: Garment) async {
        let paths = item.photoPaths + item.thumbnailPaths + [item.photoPath, item.thumbnailPath].compactMap { $0 }
        for path in Set(paths) { await removePhoto(path) }
    }
    private func removePhoto(_ path: String?) async {
        guard let path, !path.isEmpty, let uid = user?.uid else { return }
        thumbnailRefreshes["\(uid)/\(path)"]?.task.cancel()
        try? await storage.reference(withPath: "users/\(uid)/\(path)").delete()
        await thumbnailCache.remove(path, for: uid)
    }
}
