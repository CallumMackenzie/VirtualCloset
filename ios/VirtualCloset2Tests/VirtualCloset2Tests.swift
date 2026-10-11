import Testing
import UIKit
@testable import VirtualCloset2

struct VirtualCloset2Tests {
    @Test @MainActor func iconIsExactly250PixelsOnRetinaDevices() {
        let source = UIGraphicsImageRenderer(size: CGSize(width: 600, height: 400)).image { context in
            UIColor.blue.setFill()
            context.fill(CGRect(x: 0, y: 0, width: 600, height: 400))
        }
        let jpeg = PhotoIcon.jpegData(from: source)
        let icon = jpeg.flatMap(UIImage.init(data:))
        #expect(icon?.cgImage?.width == 250)
        #expect(icon?.cgImage?.height == 250)
    }
    @Test @MainActor func thumbnailsPersistAndOnlyRefreshWhenNewer() async {
        let directory = FileManager.default.temporaryDirectory.appendingPathComponent(UUID().uuidString)
        defer { try? FileManager.default.removeItem(at: directory) }
        let cache = ThumbnailCache(directory: directory)
        let path = "items/item-thumb.jpg"
        let older = Date(timeIntervalSince1970: 100)
        let newer = Date(timeIntervalSince1970: 200)
        let source = UIGraphicsImageRenderer(size: CGSize(width: 400, height: 600)).image { context in
            UIColor.red.setFill()
            context.fill(CGRect(x: 0, y: 0, width: 400, height: 600))
        }
        let bytes = PhotoIcon.jpegData(from: source)!
        #expect(await cache.needsRefresh(for: "accountA", path: path, remoteDate: older))
        #expect(await cache.save(bytes, for: "accountA", path: path, updatedAt: older) != nil)
        #expect(!(await cache.needsRefresh(for: "accountA", path: path, remoteDate: older)))
        #expect(await cache.needsRefresh(for: "accountA", path: path, remoteDate: newer))
        #expect(await cache.needsRefresh(for: "accountB", path: path, remoteDate: older))
        #expect(await cache.save(bytes, for: "accountA", path: path, updatedAt: newer) != nil)
        // An older response arriving late must not roll the cache back.
        #expect(await cache.save(bytes, for: "accountA", path: path, updatedAt: older) != nil)
        #expect(!(await cache.needsRefresh(for: "accountA", path: path, remoteDate: newer)))

        let reopened = ThumbnailCache(directory: directory)
        #expect(await reopened.image(for: "accountA", path: path) != nil)
        #expect(!(await reopened.needsRefresh(for: "accountA", path: path, remoteDate: newer)))
        await reopened.removeAccount("accountA")
        #expect(await reopened.image(for: "accountA", path: path) == nil)
    }
    @Test func categoriesHaveTypes() {
        for category in GarmentCategory.allCases { #expect(!category.types.isEmpty) }
        #expect(GarmentCategory.accessories.types.contains("Scarf"))
    }
    @Test func clothingSearchScoresAttributes() {
        var shirt = Garment()
        shirt.name = "Blue Oxford"
        shirt.colors = ["Blue"]
        shirt.styles = ["Formal"]
        shirt.fit = .relaxed
        #expect(shirt.matchScore("blue formal") == 2)
        #expect(shirt.matchScore("relaxed") == 1)
        #expect(shirt.matchScore("red") == 0)
    }
    @Test func onlySingleClosetHasDefaultDestination() {
        let home = Closet(id: "home", name: "Home")
        let storage = Closet(id: "storage", name: "Storage")
        #expect(MainTabs.onlyClosetId(in: []) == nil)
        #expect(MainTabs.onlyClosetId(in: [home]) == "home")
        #expect(MainTabs.onlyClosetId(in: [home, storage]) == nil)
    }
    @Test func itemIdentityDoesNotDependOnAttributes() {
        let first = Garment(), second = Garment()
        #expect(first.id != second.id)
        #expect(GarmentFit.allCases.contains(.baggy))
        #expect(GarmentLength.allCases.contains(.cropped))
    }
    @Test func outfitPickerFiltersAcrossAttributesAndSearchTerms() {
        var shirt = Garment()
        shirt.name = "Blue Oxford Shirt"
        shirt.closetId = "home"
        shirt.category = .tops
        shirt.type = "Shirt"
        shirt.colors = ["Blue", "White"]
        shirt.styles = ["Formal"]
        shirt.brand = "Acme"
        shirt.size = .m
        shirt.fit = .slim

        var filter = GarmentFilter()
        #expect(filter.matches(shirt))
        filter.query = "blue formal"
        #expect(filter.matches(shirt))
        filter.query = "blue hoodie"
        #expect(!filter.matches(shirt))
        filter.query = ""
        filter.category = "Tops"
        filter.type = "Shirt"
        filter.closetId = "home"
        filter.color = "blue"
        filter.brand = "acme"
        filter.style = "formal"
        filter.size = "M"
        filter.fit = "Slim"
        filter.cleanOnly = true
        #expect(filter.matches(shirt))
        #expect(filter.isActive)
        shirt.isDirty = true
        #expect(!filter.matches(shirt))
        filter.cleanOnly = false
        filter.closetId = "storage"
        #expect(!filter.matches(shirt))
        #expect(!GarmentFilter().isActive)
    }
    @Test func tagsTrimEmptyValues() {
        #expect(" blue, , cotton ,".tags == ["blue", "cotton"])
    }
}
