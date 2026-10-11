import Testing
@testable import VirtualCloset2

struct VirtualCloset2Tests {
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
