import SwiftUI
import FirebaseAuth

struct OutfitsView: View {
    @EnvironmentObject private var store: ClosetStore
    @State private var editing: Outfit?
    @State private var deleting: Outfit?
    var body: some View {
        List {
            if store.outfits.isEmpty { ContentUnavailableView("No outfits yet", systemImage: "person.crop.rectangle.stack", description: Text("Save combinations of pieces from any closet.")) }
            ForEach(store.outfits.sorted { $0.updatedAt > $1.updatedAt }) { outfit in
                Button { editing = outfit } label: {
                    HStack {
                        VStack(alignment: .leading) {
                            Text(outfit.name).font(.headline).foregroundStyle(.primary)
                            Text("\(outfit.itemIds.count) items").font(.caption).foregroundStyle(.secondary)
                        }
                        Spacer()
                        ForEach(Array(outfit.itemIds.prefix(3)), id: \.self) { id in
                            if let item = store.items.first(where: { $0.id == id }) { PhotoThumbnail(path: item.photoPath, symbol: item.category.symbol) }
                        }
                    }
                }.swipeActions { Button("Delete", role: .destructive) { deleting = outfit } }
            }
        }
        .navigationTitle("Outfits")
        .refreshable { await store.refresh() }
        .toolbar { Button("New outfit", systemImage: "plus") { editing = Outfit() } }
        .sheet(item: $editing) { OutfitEditor(outfit: $0) }
        .confirmationDialog("Delete outfit?", isPresented: Binding(get: { deleting != nil }, set: { if !$0 { deleting = nil } })) {
            Button("Delete", role: .destructive) {
                guard let outfit = deleting else { return }; deleting = nil
                Task { do { try await store.deleteOutfit(outfit) } catch { store.report(error) } }
            }
        }
    }
}

struct OutfitEditor: View {
    @EnvironmentObject private var store: ClosetStore
    @Environment(\.dismiss) private var dismiss
    @State private var outfit: Outfit
    @State private var busy = false
    init(outfit: Outfit) { _outfit = State(initialValue: outfit) }
    var body: some View {
        NavigationStack {
            Form {
                Section("Name") { TextField("Outfit name", text: $outfit.name) }
                Section("Selected pieces") {
                    ForEach(outfit.itemIds, id: \.self) { id in
                        if let item = store.items.first(where: { $0.id == id }) {
                            HStack {
                                PhotoThumbnail(path: item.photoPath, symbol: item.category.symbol)
                                Text(item.name)
                                Spacer()
                                Button("Remove", systemImage: "minus.circle") { outfit.itemIds.removeAll { $0 == id } }.labelStyle(.iconOnly)
                            }
                        } else { Text("Unavailable item").foregroundStyle(.secondary) }
                    }
                }
                Section("Add from any closet") {
                    ForEach(store.items.filter { !outfit.itemIds.contains($0.id) }.sorted { $0.name < $1.name }) { item in
                        Button { outfit.itemIds.append(item.id) } label: {
                            HStack {
                                PhotoThumbnail(path: item.photoPath, symbol: item.category.symbol)
                                Text(item.name).foregroundStyle(.primary)
                                Spacer()
                                Image(systemName: "plus.circle")
                            }
                        }
                    }
                }
                if busy { ProgressView() }
            }
            .navigationTitle("Outfit")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button("Cancel") { dismiss() } }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Save") {
                        busy = true
                        Task { do { try await store.saveOutfit(outfit); dismiss() } catch { store.report(error); busy = false } }
                    }.disabled(busy || outfit.name.trimmed.isEmpty)
                }
            }
        }
    }
}

struct WishlistView: View {
    @EnvironmentObject private var store: ClosetStore
    @State private var editing: Wish?
    @State private var purchasing: Wish?
    @State private var deleting: Wish?
    var body: some View {
        List {
            if store.wishes.isEmpty { ContentUnavailableView("No future purchases", systemImage: "heart", description: Text("Save pieces you might buy later.")) }
            ForEach(store.wishes.sorted { $0.createdAt > $1.createdAt }) { wish in
                Button { editing = wish } label: {
                    HStack {
                        PhotoThumbnail(path: wish.photoPath, symbol: "heart")
                        VStack(alignment: .leading) {
                            Text(wish.name).font(.headline).foregroundStyle(.primary)
                            if !wish.brand.isEmpty { Text(wish.brand).font(.caption).foregroundStyle(.secondary) }
                        }
                        Spacer()
                        if !wish.price.isEmpty { Text("\(wish.currency) \(wish.price)").foregroundStyle(.secondary) }
                    }
                }
                .swipeActions(edge: .leading) { Button("Purchased") { purchasing = wish }.tint(.green) }
                .swipeActions { Button("Delete", role: .destructive) { deleting = wish } }
            }
        }
        .navigationTitle("Wishlist")
        .refreshable { await store.refresh() }
        .toolbar { Button("Add wish", systemImage: "plus") { editing = Wish() } }
        .sheet(item: $editing) { WishEditor(wish: $0) }
        .sheet(item: $purchasing) { wish in PurchaseView(wish: wish) }
        .confirmationDialog("Delete wishlist entry?", isPresented: Binding(get: { deleting != nil }, set: { if !$0 { deleting = nil } })) {
            Button("Delete", role: .destructive) {
                guard let wish = deleting else { return }; deleting = nil
                Task { do { try await store.deleteWish(wish) } catch { store.report(error) } }
            }
        }
    }
}

struct WishEditor: View {
    @EnvironmentObject private var store: ClosetStore
    @Environment(\.dismiss) private var dismiss
    @State private var wish: Wish
    @State private var image: UIImage?
    @State private var busy = false
    init(wish: Wish) { _wish = State(initialValue: wish) }
    var body: some View {
        NavigationStack {
            Form {
                Section("Photo") { PhotoPicker(image: $image, existingPath: wish.photoPath) }
                Section("Purchase idea") {
                    TextField("Name", text: $wish.name)
                    TextField("Brand", text: $wish.brand)
                    TextField("Product URL", text: $wish.url).keyboardType(.URL).textInputAutocapitalization(.never)
                    if let url = URL(string: wish.url), ["https", "http"].contains(url.scheme?.lowercased() ?? "") {
                        Link("Open product page", destination: url)
                    }
                    HStack {
                        TextField("Currency (e.g. USD)", text: $wish.currency).textInputAutocapitalization(.characters)
                        TextField("Estimated price", text: $wish.price).keyboardType(.decimalPad)
                    }
                    TextField("Notes", text: $wish.notes, axis: .vertical)
                }
                if busy { ProgressView() }
            }
            .navigationTitle("Wishlist item")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button("Cancel") { dismiss() } }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Save") {
                        busy = true
                        Task { do { try await store.saveWish(wish, photo: image); dismiss() } catch { store.report(error); busy = false } }
                    }.disabled(busy || wish.name.trimmed.isEmpty)
                }
            }
        }
    }
}

struct PurchaseView: View {
    @EnvironmentObject private var store: ClosetStore
    @Environment(\.dismiss) private var dismiss
    let wish: Wish
    @State private var closetId = ""
    @State private var category: GarmentCategory = .tops
    @State private var busy = false
    var body: some View {
        NavigationStack {
            Form {
                Text("Add \(wish.name) to your closet. You can fill in its other details later.")
                Picker("Closet", selection: $closetId) { ForEach(store.closets) { Text($0.name).tag($0.id) } }
                Picker("Category", selection: $category) { ForEach(GarmentCategory.allCases) { Text($0.rawValue).tag($0) } }
                if store.closets.isEmpty { Text("Create a closet before marking this purchased.").foregroundStyle(.secondary) }
                if busy { ProgressView() }
            }
            .navigationTitle("Mark purchased")
            .onAppear { if closetId.isEmpty { closetId = store.closets.first?.id ?? "" } }
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button("Cancel") { dismiss() } }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Add to closet") {
                        var item = Garment(); item.name = wish.name; item.brand = wish.brand
                        item.closetId = closetId; item.category = category; item.type = category.types[0]
                        busy = true
                        Task { do { try await store.purchase(wish, as: item); dismiss() } catch { store.report(error); busy = false } }
                    }.disabled(busy || closetId.isEmpty)
                }
            }
        }
    }
}

struct AccountView: View {
    @EnvironmentObject private var store: ClosetStore
    @State private var deleting = false
    var body: some View {
        Form {
            Section("Signed in as") { Text(store.user?.email ?? "Account") }
            Section { Button("Sign out") { do { try store.signOut() } catch { store.report(error) } } }
            Section {
                Text("Your photos and closet data are private to this account.")
                Button("Delete account and data", role: .destructive) { deleting = true }
            }
        }
        .navigationTitle("Account")
        .confirmationDialog("Permanently delete your account and all closet data?", isPresented: $deleting) {
            Button("Delete everything", role: .destructive) {
                Task { do { try await store.deleteAccount() } catch { store.report(error) } }
            }
        } message: { Text("This cannot be undone. You may be asked to sign in again first.") }
    }
}
