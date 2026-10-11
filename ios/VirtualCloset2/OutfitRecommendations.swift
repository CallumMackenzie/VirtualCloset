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
    let palette: OutfitPalette?
    let score: Int
    var id: String { item.id }
    var reason: String { palette.map { "\($0.name) palette" } ?? "Neutral or tonal pairing" }
}

enum OutfitRecommendations {
    // These are broad clothing-color labels, not RGB measurements. Match common free-text
    // aliases without treating every shade of the same hue as interchangeable.
    nonisolated static func color(_ raw: String) -> String? {
        let text = raw.lowercased().replacingOccurrences(of: "-", with: " ").trimmingCharacters(in: .whitespacesAndNewlines)
        if text.isEmpty || text == "multicolor" { return nil }
        if text == "clear" { return "clear" }
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
        var result = garment.colors.flatMap { $0.components(separatedBy: CharacterSet(charactersIn: "/,&+")) }
            .compactMap(color)
        // Imported tags are sometimes incomplete or disagree with an explicit color in the name
        // (for example, navy trousers tagged only black/grey). Use the name as an extra clue.
        var name = " " + garment.name.lowercased().components(separatedBy: .alphanumerics.inverted)
            .filter { !$0.isEmpty }.joined(separator: " ") + " "
        let phrases = ["olive green", "blue grey", "blue gray", "light blue", "dark green", "dark brown",
                       "dark grey", "dark gray", "light grey", "light gray", "off white", "navy", "brown",
                       "black", "white", "cream", "beige", "grey", "gray", "blue", "green", "red",
                       "yellow", "purple", "rust", "orange", "khaki", "tan", "gold", "silver", "taupe",
                       "stone", "pink", "charcoal", "burgundy"]
        for phrase in phrases where name.contains(" \(phrase) ") {
            if let inferred = color(phrase) { result.append(inferred) }
            name = name.replacingOccurrences(of: " \(phrase) ", with: " ")
        }
        var seen = Set<String>()
        return result.filter { seen.insert($0).inserted }
    }

    private static func eligible(_ candidate: Garment, selected: [Garment]) -> Bool {
        let categories = Set(selected.map(\.category))
        if candidate.category == .other { return false }
        if categories.contains(.onePieces) && [.tops, .bottoms, .onePieces].contains(candidate.category) { return false }
        if candidate.category == .onePieces && (categories.contains(.tops) || categories.contains(.bottoms)) { return false }
        // A second bottom isn't a useful completion, but a shirt and a sweater can layer.
        if [.bottoms, .shoes, .outerwear].contains(candidate.category) && categories.contains(candidate.category) { return false }
        if (candidate.category == .tops || candidate.category == .accessories) && selected.contains(where: {
            $0.category == candidate.category && $0.displayType.localizedCaseInsensitiveCompare(candidate.displayType) == .orderedSame
        }) { return false }
        return true
    }

    private static func priority(_ category: GarmentCategory, selected: [Garment]) -> Int {
        let categories = Set(selected.map(\.category))
        switch category {
        case .tops: return categories.contains(.tops) ? 2 : (categories.contains(.bottoms) ? 6 : 4)
        case .bottoms: return categories.contains(.tops) ? 6 : 4
        case .onePieces: return 4
        case .shoes: return categories.contains(.bottoms) || categories.contains(.onePieces) ? 4 : 3
        case .outerwear: return categories.contains(.tops) || categories.contains(.onePieces) ? 3 : 2
        case .accessories: return 1
        case .other: return 0
        }
    }

    private struct Pairing {
        let score: Int
        let palette: OutfitPalette?
    }

    private static let versatile: Set<String> = [
        "black", "white", "cream", "grey", "charcoal", "navy", "brown", "beige", "tan", "taupe", "stone", "sand", "clear"
    ]

    private static func pairing(_ first: String, _ second: String, palettes: [OutfitPalette]) -> Pairing? {
        if first == second { return Pairing(score: 3, palette: nil) }
        let evidence = palettes.filter { palette in
            // The two garments must match different swatches. Otherwise a black-on-black
            // combination could incorrectly claim to be from a "Black and Gold" palette.
            palette.colors.indices.contains { left in
                matches(first, paletteColor: palette.colors[left]) && palette.colors.indices.contains { right in
                    left != right && matches(second, paletteColor: palette.colors[right])
                }
            }
        }
        if let best = evidence.min(by: { $0.colors.count < $1.colors.count }) {
            // A concise palette is stronger evidence than two colors appearing in a broad capsule.
            let strength = max(4, 11 - best.colors.count) + min(3, evidence.count - 1)
            return Pairing(score: strength, palette: best)
        }
        if matches(first, paletteColor: second) { return Pairing(score: 3, palette: nil) }
        // Everyday neutrals remain useful even if a particular online palette omitted the pair.
        if versatile.contains(first) || versatile.contains(second) { return Pairing(score: 2, palette: nil) }
        return nil
    }

    static func suggest(selected: [Garment], available: [Garment], query: String = "",
                        palettes: [OutfitPalette] = OutfitPalette.all) -> [OutfitRecommendation] {
        let selectedColors = selected.map(colors(of:)).filter { !$0.isEmpty }
        // A search uses the existing filtered list without separating out recommendations.
        guard query.trimmed.isEmpty, !selected.isEmpty, !selectedColors.isEmpty else { return [] }
        return available.compactMap { item in
            guard !selected.contains(where: { $0.id == item.id }), eligible(item, selected: selected) else { return nil }
            let candidateColors = colors(of: item)
            guard !candidateColors.isEmpty else { return nil }
            // Score how well this item works with each selected piece independently. Do not
            // require the entire outfit to occur in one palette or hide matches beyond row eight.
            let perItem: [Pairing] = selectedColors.compactMap { shades in
                shades.flatMap { shade in
                    candidateColors.compactMap { candidate in pairing(shade, candidate, palettes: palettes) }
                }.max(by: { $0.score < $1.score })
            }
            guard let best = perItem.max(by: { $0.score < $1.score }) else { return nil }
            let coverage = perItem.count
            let score = perItem.reduce(0) { $0 + $1.score } * 12 + coverage * 18 +
                (coverage == selectedColors.count ? 12 : 0) + priority(item.category, selected: selected) * 4 -
                (selectedColors.count - coverage) * 8 - (item.isDirty ? 40 : 0)
            return OutfitRecommendation(item: item, palette: best.palette, score: score)
        }.sorted {
            $0.score == $1.score ? $0.item.name.localizedStandardCompare($1.item.name) == .orderedAscending : $0.score > $1.score
        }
    }
}
