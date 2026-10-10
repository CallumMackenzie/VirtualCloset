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
    @Test func tagsTrimEmptyValues() {
        #expect(" blue, , cotton ,".tags == ["blue", "cotton"])
    }
}
