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
    @Test func outfitPalettesAreBundledWithSources() {
        #expect(OutfitPalette.all.count >= 20)
        #expect(OutfitPalette.all.allSatisfy { $0.colors.count >= 2 && $0.source.scheme == "https" })
        #expect(OutfitRecommendations.color("Olive green") == "olive")
        #expect(OutfitRecommendations.color("Dark brown") == "chocolate")
        #expect(OutfitRecommendations.color("Blue-grey") == "slate")
    }
    @Test func recommendationsMatchColorsWithoutRepeatingBottomsOrTops() {
        var pants = Garment(); pants.name = "Navy pants"; pants.category = .bottoms; pants.colors = ["Navy"]
        var otherPants = Garment(); otherPants.name = "White jeans"; otherPants.category = .bottoms; otherPants.colors = ["White"]
        var shirt = Garment(); shirt.name = "Cream shirt"; shirt.category = .tops; shirt.colors = ["Cream"]
        var shoes = Garment(); shoes.name = "Brown shoes"; shoes.category = .shoes; shoes.colors = ["Brown"]
        var clashing = Garment(); clashing.name = "Pink shirt"; clashing.category = .tops; clashing.colors = ["Pink"]
        let available = [otherPants, shirt, shoes, clashing]
        let first = OutfitRecommendations.suggest(selected: [pants], available: available)
        #expect(first.contains { $0.id == shirt.id })
        #expect(first.contains { $0.id == shoes.id })
        #expect(!first.contains { $0.id == otherPants.id })
        #expect(first.first?.id == shirt.id)
        #expect(first.firstIndex(where: { $0.id == clashing.id })! > first.firstIndex(where: { $0.id == shirt.id })!)
        let next = OutfitRecommendations.suggest(selected: [pants, shirt], available: available)
        #expect(!next.contains { $0.item.category == .bottoms })
        #expect(next.contains { $0.id == shoes.id })
        // Search keeps the original filtered results and relevance sorting; no recommended strip.
        var filter = GarmentFilter(); filter.query = "cream"
        #expect(filter.matches(shirt))
        #expect(OutfitRecommendations.suggest(selected: [pants], available: available.filter(filter.matches), query: filter.query).isEmpty)
    }
    @Test func recommendationsIncludeStrongMatchesAcrossDifferentPalettesAndBeyondEightRows() {
        var blackPants = Garment(); blackPants.category = .bottoms; blackPants.colors = ["Black"]
        var oliveTop = Garment(); oliveTop.category = .tops; oliveTop.colors = ["Olive green"]
        var goldChain = Garment(); goldChain.category = .accessories; goldChain.type = "Jewellery"; goldChain.colors = ["Gold"]
        // No single bundled palette contains black, olive and gold, but gold pairs with each.
        let mixed = OutfitRecommendations.suggest(selected: [blackPants, oliveTop], available: [goldChain])
        #expect(mixed.map(\.id) == [goldChain.id])

        var greyShoes = Garment(); greyShoes.category = .shoes; greyShoes.colors = ["Grey"]
        var creamSweater = Garment(); creamSweater.category = .tops; creamSweater.type = "Sweater"; creamSweater.colors = ["Cream"]
        #expect(OutfitRecommendations.suggest(selected: [blackPants, greyShoes], available: [creamSweater]).contains { $0.id == creamSweater.id })
        let whiteTops = (0..<12).map { index -> Garment in
            var top = Garment(); top.name = "White top \(index)"; top.category = .tops; top.colors = ["White"]
            return top
        }
        #expect(OutfitRecommendations.suggest(selected: [blackPants], available: whiteTops).count == 12)
    }
    @Test func explicitNameColorsSupplementIncorrectImportedTags() {
        var pants = Garment(); pants.name = "Navy pinstripe pants"; pants.category = .bottoms; pants.colors = ["Black", "Grey"]
        var shirt = Garment(); shirt.name = "Olive shirt"; shirt.category = .tops; shirt.colors = ["Olive green"]
        let navyOlive = OutfitPalette(name: "Navy and Olive", colors: ["navy", "olive"], source: URL(string: "https://example.com")!)
        let results = OutfitRecommendations.suggest(selected: [pants], available: [shirt], palettes: [navyOlive])
        #expect(results.first?.palette?.name == "Navy and Olive")
    }
    @Test func monochromePairDoesNotClaimAnUnrelatedPaletteAccent() {
        var pants = Garment(); pants.category = .bottoms; pants.colors = ["Black"]
        var shirt = Garment(); shirt.category = .tops; shirt.colors = ["Black"]
        let blackAndGold = OutfitPalette(name: "Black and Gold", colors: ["black", "gold"], source: URL(string: "https://example.com")!)
        let match = OutfitRecommendations.suggest(selected: [pants], available: [shirt], palettes: [blackAndGold])
        #expect(match.first?.palette == nil)
    }
    @Test func dirtyPiecesAreRankedLowerRatherThanHidden() {
        var pants = Garment(); pants.category = .bottoms; pants.colors = ["Black"]
        var clean = Garment(); clean.name = "White shirt"; clean.category = .tops; clean.colors = ["White"]
        var dirty = Garment(); dirty.name = "Another white shirt"; dirty.category = .tops; dirty.type = "Sweater"; dirty.colors = ["White"]; dirty.isDirty = true
        let results = OutfitRecommendations.suggest(selected: [pants], available: [dirty, clean])
        #expect(results.map(\.id) == [clean.id, dirty.id])
    }
    @Test func differentTopLayersAreAllowedButDuplicateBottomsAndTypesAreNot() {
        var pants = Garment(); pants.category = .bottoms; pants.colors = ["Black"]
        var tee = Garment(); tee.category = .tops; tee.type = "T-shirt"; tee.colors = ["White"]
        var sweater = Garment(); sweater.category = .tops; sweater.type = "Sweater"; sweater.colors = ["Cream"]
        var otherTee = Garment(); otherTee.category = .tops; otherTee.type = "T-shirt"; otherTee.colors = ["Cream"]
        var secondPants = Garment(); secondPants.category = .bottoms; secondPants.colors = ["Grey"]
        let results = OutfitRecommendations.suggest(selected: [pants, tee], available: [sweater, otherTee, secondPants])
        #expect(results.map(\.id) == [sweater.id])
    }
    @Test func onePieceAndAccessoriesDoNotSuggestDuplicateTypes() {
        var dress = Garment(); dress.category = .onePieces; dress.colors = ["Black"]
        var top = Garment(); top.category = .tops; top.colors = ["White"]
        var bottom = Garment(); bottom.category = .bottoms; bottom.colors = ["White"]
        var goldBelt = Garment(); goldBelt.category = .accessories; goldBelt.type = "Belt"; goldBelt.colors = ["Gold"]
        var goldHat = Garment(); goldHat.category = .accessories; goldHat.type = "Hat"; goldHat.colors = ["Gold"]
        let suggestions = OutfitRecommendations.suggest(selected: [dress, goldBelt], available: [top, bottom, goldHat, goldBelt])
        #expect(suggestions.map(\.id) == [goldHat.id])
    }
    @Test func tagsTrimEmptyValues() {
        #expect(" blue, , cotton ,".tags == ["blue", "cotton"])
    }
}
