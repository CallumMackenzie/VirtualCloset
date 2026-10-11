import Foundation

// Curated from the source URLs bundled with each palette; no account data leaves the device.
struct OutfitPalette: Decodable {
    let name: String
    let colors: [String]
    let source: URL

    static let all: [OutfitPalette] = {
        guard let url = Bundle.main.url(forResource: "OutfitPalettes", withExtension: "json"),
              let data = try? Data(contentsOf: url),
              let palettes = try? JSONDecoder().decode([OutfitPalette].self, from: data) else { return [] }
        return palettes
    }()
}

struct OutfitRecommendation: Identifiable {
    let item: Garment
    let palette: OutfitPalette
    let score: Int
    var id: String { item.id }
}

enum OutfitRecommendations {
    // These are broad clothing-color labels, not RGB measurements. Match common free-text
    // aliases without treating every shade of the same hue as interchangeable.
    nonisolated static func color(_ raw: String) -> String? {
        let text = raw.lowercased().replacingOccurrences(of: "-", with: " ").trimmingCharacters(in: .whitespacesAndNewlines)
        if text.isEmpty || text == "clear" || text == "multicolor" { return nil }
        if text.contains("navy") || text.contains("midnight blue") { return "navy" }
        if text.contains("light blue") || text.contains("sky blue") || text.contains("baby blue") { return "light blue" }
        if text.contains("blue grey") || text.contains("blue gray") || text.contains("slate") { return "slate" }
        if text.contains("denim") { return "denim" }
        if text.contains("sapphire") { return "sapphire" }
        if text.contains("blue") { return "blue" }
        if text.contains("charcoal") || text.contains("dark grey") || text.contains("dark gray") { return "charcoal" }
        if text.contains("grey") || text.contains("gray") { return "grey" }
        if text.contains("black") { return "black" }
        if text.contains("off white") || text.contains("ivory") || text.contains("ecru") || text.contains("cream") { return "cream" }
        if text.contains("white") { return "white" }
        if text.contains("burgundy") || text.contains("maroon") || text.contains("oxblood") || text.contains("wine") { return "burgundy" }
        if text.contains("ruby") { return "ruby" }
        if text.contains("rust") || text.contains("terracotta") { return "rust" }
        if text.contains("coral") { return "coral" }
        if text.contains("red") { return "red" }
        if text.contains("forest") || text.contains("dark green") || text.contains("hunter green") { return "forest" }
        if text.contains("emerald") { return "emerald" }
        if text.contains("olive") { return "olive" }
        if text.contains("sage") { return "sage" }
        if text.contains("green") { return "green" }
        if text.contains("chocolate") || text.contains("dark brown") || text.contains("espresso") { return "chocolate" }
        if text.contains("camel") { return "camel" }
        if text.contains("taupe") { return "taupe" }
        if text.contains("stone") || text.contains("mushroom") { return "stone" }
        if text.contains("sand") { return "sand" }
        if text.contains("tan") || text.contains("khaki") { return "tan" }
        if text.contains("beige") || text.contains("latte") { return "beige" }
        if text.contains("brown") { return "brown" }
        if text.contains("mustard") { return "mustard" }
        if text.contains("straw") { return "straw" }
        if text.contains("yellow") { return "yellow" }
        if text.contains("orange") { return "orange" }
        if text.contains("blush") || text.contains("rose") { return "blush" }
        if text.contains("pink") { return "pink" }
        if text.contains("amethyst") || text.contains("lavender") { return "amethyst" }
        if text.contains("purple") { return "purple" }
        if text.contains("gold") { return "gold" }
        if text.contains("silver") { return "silver" }
        return nil
    }

    private static let families: [String: Set<String>] = [
        "blue": ["navy", "light blue", "denim", "sapphire", "slate"],
        "green": ["olive", "sage", "forest", "emerald"],
        "grey": ["charcoal", "slate"],
        "brown": ["chocolate", "camel", "tan", "beige", "taupe", "stone", "sand"],
        "beige": ["camel", "tan", "stone", "sand", "cream", "taupe"],
        "yellow": ["mustard", "straw"],
        "orange": ["rust", "coral"],
        "purple": ["amethyst"],
        "pink": ["blush"],
        "red": ["burgundy", "ruby"],
        "white": ["cream"]
    ]

    private static func matches(_ garmentColor: String, paletteColor: String) -> Bool {
        garmentColor == paletteColor || families[garmentColor]?.contains(paletteColor) == true ||
            families[paletteColor]?.contains(garmentColor) == true
    }

    nonisolated private static func colors(of garment: Garment) -> [String] {
        garment.colors.flatMap { $0.components(separatedBy: CharacterSet(charactersIn: "/,&+")) }
            .compactMap(color)
    }

    private static func eligible(_ candidate: Garment, selected: [Garment]) -> Bool {
        let categories = Set(selected.map(\.category))
        if candidate.category == .other || candidate.isDirty { return false }
        if categories.contains(.onePieces) && [.tops, .bottoms, .onePieces].contains(candidate.category) { return false }
        if candidate.category == .onePieces && (categories.contains(.tops) || categories.contains(.bottoms)) { return false }
        if candidate.category != .accessories && categories.contains(candidate.category) { return false }
        if candidate.category == .accessories && selected.contains(where: {
            $0.category == .accessories && $0.displayType.localizedCaseInsensitiveCompare(candidate.displayType) == .orderedSame
        }) { return false }
        return true
    }

    private static func priority(_ category: GarmentCategory, selected: [Garment]) -> Int {
        let categories = Set(selected.map(\.category))
        switch category {
        case .tops: return categories.contains(.bottoms) ? 6 : 4
        case .bottoms: return categories.contains(.tops) ? 6 : 4
        case .onePieces: return 4
        case .shoes: return categories.contains(.bottoms) || categories.contains(.onePieces) ? 4 : 3
        case .outerwear: return categories.contains(.tops) || categories.contains(.onePieces) ? 3 : 2
        case .accessories: return 1
        case .other: return 0
        }
    }

    static func suggest(selected: [Garment], available: [Garment], query: String = "", limit: Int = 8,
                        palettes: [OutfitPalette] = OutfitPalette.all) -> [OutfitRecommendation] {
        let selectedColors = selected.map(colors(of:)).filter { !$0.isEmpty }
        // A search uses the existing filtered list without separating out recommendations.
        guard query.trimmed.isEmpty, !selected.isEmpty, !selectedColors.isEmpty, limit > 0 else { return [] }
        let ranked: [OutfitRecommendation] = available.compactMap { item in
            guard !selected.contains(where: { $0.id == item.id }), eligible(item, selected: selected) else { return nil }
            let candidateColors = colors(of: item)
            guard !candidateColors.isEmpty else { return nil }
            let fits = palettes.compactMap { palette -> OutfitRecommendation? in
                guard selectedColors.allSatisfy({ colors in
                    colors.contains(where: { shade in palette.colors.contains(where: { matches(shade, paletteColor: $0) }) })
                }), candidateColors.contains(where: { shade in palette.colors.contains(where: { matches(shade, paletteColor: $0) }) }) else { return nil }
                let contrast = candidateColors.contains { candidate in
                    !selectedColors.flatMap { $0 }.contains(where: { matches(candidate, paletteColor: $0) })
                }
                let score = priority(item.category, selected: selected) * 100 + (contrast ? 20 : 0) +
                    selectedColors.count * 4 + max(0, 6 - palette.colors.count)
                return OutfitRecommendation(item: item, palette: palette, score: score)
            }
            return fits.max { $0.score < $1.score }
        }.sorted {
            $0.score == $1.score ? $0.item.name.localizedStandardCompare($1.item.name) == .orderedAscending : $0.score > $1.score
        }
        // Offer a mix of tops, shoes and layers instead of filling the entire strip with one type.
        var result: [OutfitRecommendation] = []
        var counts: [GarmentCategory: Int] = [:]
        for match in ranked where result.count < limit {
            if counts[match.item.category, default: 0] < 3 {
                result.append(match)
                counts[match.item.category, default: 0] += 1
            }
        }
        for match in ranked where result.count < limit && !result.contains(where: { $0.id == match.id }) {
            result.append(match)
        }
        return result
    }
}
