import Foundation
import FirebaseFirestore

enum GarmentCategory: String, CaseIterable, Identifiable, Codable {
    case tops = "Tops", bottoms = "Bottoms", onePieces = "One-pieces", outerwear = "Outerwear"
    case shoes = "Shoes", accessories = "Accessories", other = "Other"
    var id: String { rawValue }
    var types: [String] {
        switch self {
        case .tops: ["T-shirt", "Shirt", "Blouse", "Sweater", "Hoodie", "Tank top", "Other"]
        case .bottoms: ["Jeans", "Trousers", "Shorts", "Skirt", "Leggings", "Other"]
        case .onePieces: ["Dress", "Jumpsuit", "Suit", "Other"]
        case .outerwear: ["Jacket", "Coat", "Blazer", "Vest", "Other"]
        case .shoes: ["Sneakers", "Boots", "Dress shoes", "Sandals", "Heels", "Other"]
        case .accessories: ["Hat", "Belt", "Scarf", "Bag", "Jewellery", "Sunglasses", "Watch", "Other"]
        case .other: ["Other"]
        }
    }
    var symbol: String {
        switch self {
        case .tops: "tshirt"
        case .bottoms: "figure.walk"
        case .onePieces: "person.crop.rectangle"
        case .outerwear: "cloud"
        case .shoes: "shoe"
        case .accessories: "sunglasses"
        case .other: "square.dashed"
        }
    }
}

enum GarmentFit: String, CaseIterable, Identifiable { case unknown = "Unknown", fitted = "Fitted", slim = "Slim", regular = "Regular", relaxed = "Relaxed", oversized = "Oversized", baggy = "Baggy"; var id: String { rawValue } }
enum GarmentLength: String, CaseIterable, Identifiable { case unknown = "Unknown", cropped = "Cropped", standard = "Standard", longline = "Longline"; var id: String { rawValue } }
enum GarmentSize: String, CaseIterable, Identifiable { case unknown = "Unknown", xxs = "XXS", xs = "XS", s = "S", m = "M", l = "L", xl = "XL", xxl = "XXL"; var id: String { rawValue } }

struct Closet: Identifiable {
    var id: String = UUID().uuidString
    var name: String
    init(id: String = UUID().uuidString, name: String) { self.id = id; self.name = name }
    init?(snapshot: DocumentSnapshot) {
        guard let name = snapshot.data()?["name"] as? String else { return nil }
        self.init(id: snapshot.documentID, name: name)
    }
    var data: [String: Any] { ["name": name] }
}

struct Garment: Identifiable {
    var id: String = UUID().uuidString
    var closetId: String = ""
    var name: String = ""
    var category: GarmentCategory = .tops
    var type: String = "T-shirt"
    var customType: String = ""
    var styles: [String] = []
    var colors: [String] = []
    var brand: String = ""
    var size: GarmentSize = .unknown
    var material: String = ""
    var fit: GarmentFit = .unknown
    var length: GarmentLength = .unknown
    var isDirty: Bool = false
    var photoPath: String? = nil
    var photoPaths: [String] = []
    var createdAt: Date = .now
    var updatedAt: Date = .now
    var displayType: String { type == "Other" && !customType.isEmpty ? customType : type }
    init() {}
    init?(snapshot: DocumentSnapshot) {
        guard let d = snapshot.data(), let name = d["name"] as? String, let closet = d["closetId"] as? String else { return nil }
        self.init()
        id = snapshot.documentID; self.name = name; closetId = closet
        category = GarmentCategory(rawValue: d["category"] as? String ?? "") ?? .other
        type = d["type"] as? String ?? "Other"; customType = d["customType"] as? String ?? ""
        styles = d["styles"] as? [String] ?? []; colors = d["colors"] as? [String] ?? []
        brand = d["brand"] as? String ?? ""; material = d["material"] as? String ?? ""
        size = GarmentSize(rawValue: d["size"] as? String ?? "") ?? .unknown
        fit = GarmentFit(rawValue: d["fit"] as? String ?? "") ?? .unknown
        length = GarmentLength(rawValue: d["length"] as? String ?? "") ?? .unknown
        isDirty = d["isDirty"] as? Bool ?? false; photoPath = d["photoPath"] as? String
        photoPaths = d["photoPaths"] as? [String] ?? photoPath.map { $0.isEmpty ? [] : [$0] } ?? []
        createdAt = (d["createdAt"] as? Timestamp)?.dateValue() ?? .now
        updatedAt = (d["updatedAt"] as? Timestamp)?.dateValue() ?? .now
    }
    var data: [String: Any] {
        ["closetId": closetId, "name": name, "category": category.rawValue, "type": type,
         "customType": customType, "styles": styles, "colors": colors, "brand": brand,
         "size": size.rawValue, "material": material, "fit": fit.rawValue, "length": length.rawValue,
         "isDirty": isDirty, "photoPath": photoPath ?? "", "photoPaths": photoPaths, "createdAt": Timestamp(date: createdAt),
         "updatedAt": Timestamp(date: updatedAt)]
    }
    func matchScore(_ query: String) -> Int {
        let words = query.lowercased().split(whereSeparator: \.isWhitespace).map(String.init)
        let values = [name, displayType, category.rawValue, brand, material, size.rawValue, fit.rawValue, length.rawValue] + colors + styles
        return words.reduce(0) { score, word in
            score + (values.contains { $0.localizedCaseInsensitiveContains(word) } ? 1 : 0)
        }
    }
}

struct Outfit: Identifiable {
    var id: String = UUID().uuidString
    var name: String = ""
    var itemIds: [String] = []
    var updatedAt: Date = .now
    init() {}
    init?(snapshot: DocumentSnapshot) {
        guard let d = snapshot.data(), let name = d["name"] as? String else { return nil }
        id = snapshot.documentID; self.name = name
        itemIds = d["itemIds"] as? [String] ?? []
        updatedAt = (d["updatedAt"] as? Timestamp)?.dateValue() ?? .now
    }
    var data: [String: Any] { ["name": name, "itemIds": itemIds, "updatedAt": Timestamp(date: updatedAt)] }
}

struct Wish: Identifiable {
    var id: String = UUID().uuidString
    var name: String = ""
    var brand: String = ""
    var url: String = ""
    var price: String = ""
    var currency: String = "USD"
    var notes: String = ""
    var photoPath: String? = nil
    var createdAt: Date = .now
    init() {}
    init?(snapshot: DocumentSnapshot) {
        guard let d = snapshot.data(), let name = d["name"] as? String else { return nil }
        id = snapshot.documentID; self.name = name
        brand = d["brand"] as? String ?? ""; url = d["url"] as? String ?? ""
        price = d["price"] as? String ?? ""; currency = d["currency"] as? String ?? "USD"
        notes = d["notes"] as? String ?? ""; photoPath = d["photoPath"] as? String
        createdAt = (d["createdAt"] as? Timestamp)?.dateValue() ?? .now
    }
    var data: [String: Any] {
        ["name": name, "brand": brand, "url": url, "price": price, "currency": currency,
         "notes": notes, "photoPath": photoPath ?? "", "createdAt": Timestamp(date: createdAt)]
    }
}

extension String {
    var trimmed: String { trimmingCharacters(in: .whitespacesAndNewlines) }
    var tags: [String] { split(separator: ",").map { String($0).trimmed }.filter { !$0.isEmpty } }
}
