import CryptoKit
import Foundation
import UIKit

// Stored in Caches rather than Documents; iOS may evict it, and we'll re-fetch when needed.
// Separate per-account directories prevent one account's images from appearing in another.
actor ThumbnailCache {
    private struct Version: Codable { let updatedAt: Date? }

    private let directory: URL
    private let images = NSCache<NSString, UIImage>()
    private var dates: [String: Date] = [:]
    private var knownKeys: Set<String> = []

    init(directory: URL = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask)[0]
        .appendingPathComponent("VirtualClosetThumbnails", isDirectory: true)) {
        self.directory = directory
        images.totalCostLimit = 24 * 1024 * 1024
    }

    private func hash(_ value: String) -> String {
        SHA256.hash(data: Data(value.utf8)).map { String(format: "%02x", $0) }.joined()
    }
    private func file(for uid: String, path: String) -> URL {
        directory.appendingPathComponent(hash(uid), isDirectory: true)
            .appendingPathComponent(hash(path) + ".jpg")
    }
    private func key(_ uid: String, _ path: String) -> String { "\(uid)/\(path)" }

    func image(for uid: String, path: String) -> UIImage? {
        let cacheKey = key(uid, path)
        if let image = images.object(forKey: cacheKey as NSString) { return image }
        let url = file(for: uid, path: path)
        guard let data = try? Data(contentsOf: url), let image = UIImage(data: data) else { return nil }
        images.setObject(image, forKey: cacheKey as NSString, cost: 250 * 250 * 4)
        knownKeys.insert(cacheKey)
        if let versionData = try? Data(contentsOf: url.appendingPathExtension("json")),
           let version = try? JSONDecoder().decode(Version.self, from: versionData) {
            dates[cacheKey] = version.updatedAt
        }
        return image
    }

    func needsRefresh(for uid: String, path: String, remoteDate: Date?) -> Bool {
        guard image(for: uid, path: path) != nil else { return true }
        guard let remoteDate, let cachedDate = dates[key(uid, path)] else { return true }
        return remoteDate > cachedDate
    }

    @discardableResult
    func save(_ data: Data, for uid: String, path: String, updatedAt: Date?) -> UIImage? {
        guard let image = UIImage(data: data), image.cgImage?.width == 250,
              image.cgImage?.height == 250 else { return nil }
        let cacheKey = key(uid, path)
        // A slower overlapping request must not replace a newer image.
        if let updatedAt, let previous = dates[cacheKey], updatedAt <= previous {
            return self.image(for: uid, path: path)
        }
        let url = file(for: uid, path: path)
        do {
            try FileManager.default.createDirectory(at: url.deletingLastPathComponent(), withIntermediateDirectories: true)
            try data.write(to: url, options: .atomic)
            try JSONEncoder().encode(Version(updatedAt: updatedAt)).write(to: url.appendingPathExtension("json"), options: .atomic)
        } catch {
            // Still show the downloaded image if the OS won't let us persist the cache.
        }
        dates[cacheKey] = updatedAt
        knownKeys.insert(cacheKey)
        images.setObject(image, forKey: cacheKey as NSString, cost: 250 * 250 * 4)
        return image
    }

    func remove(_ path: String, for uid: String) {
        let url = file(for: uid, path: path)
        try? FileManager.default.removeItem(at: url)
        try? FileManager.default.removeItem(at: url.appendingPathExtension("json"))
        let cacheKey = key(uid, path)
        dates.removeValue(forKey: cacheKey)
        images.removeObject(forKey: cacheKey as NSString)
        knownKeys.remove(cacheKey)
    }

    func removeAccount(_ uid: String) {
        try? FileManager.default.removeItem(at: directory.appendingPathComponent(hash(uid), isDirectory: true))
        for cacheKey in knownKeys.filter({ $0.hasPrefix("\(uid)/") }) {
            dates.removeValue(forKey: cacheKey)
            images.removeObject(forKey: cacheKey as NSString)
            knownKeys.remove(cacheKey)
        }
    }
}
