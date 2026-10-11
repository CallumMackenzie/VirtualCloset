import SwiftUI
import PhotosUI

struct ContentView: View {
    @EnvironmentObject private var store: ClosetStore
    var body: some View {
        Group {
            if !store.ready { ProgressView("Loading closet…") }
            else if store.user == nil { AuthView() }
            else { MainTabs() }
        }
        .alert("Something went wrong", isPresented: Binding(get: { store.error != nil }, set: { if !$0 { store.error = nil } })) {
            Button("OK") { store.error = nil }
        } message: { Text(store.error ?? "") }
    }
}

struct AuthView: View {
    @EnvironmentObject private var store: ClosetStore
    @State private var email = ""
    @State private var password = ""
    @State private var creating = false
    @State private var busy = false
    var body: some View {
        NavigationStack {
            Form {
                Section("Your private closet") {
                    TextField("Email", text: $email).textContentType(.emailAddress).keyboardType(.emailAddress).textInputAutocapitalization(.never).autocorrectionDisabled()
                    SecureField("Password", text: $password).textContentType(creating ? .newPassword : .password)
                }
                Section {
                    Button(creating ? "Create account" : "Sign in") {
                        Task { await run { if creating { try await store.register(email: email, password: password) } else { try await store.signIn(email: email, password: password) } } }
                    }.disabled(busy || email.trimmed.isEmpty || password.isEmpty)
                    Button(creating ? "Already have an account? Sign in" : "New here? Create account") { creating.toggle() }
                    Button("Reset password") { Task { await run { try await store.resetPassword(email: email) } } }.disabled(busy || email.trimmed.isEmpty)
                }
                if busy { ProgressView() }
            }.navigationTitle("Virtual Closet")
        }
    }
    private func run(_ work: () async throws -> Void) async {
        busy = true; defer { busy = false }
        do { try await work() } catch { store.report(error) }
    }
}

struct MainTabs: View {
    @EnvironmentObject private var store: ClosetStore
    @State private var selectedTab: Tab = .closets
    @State private var closetPath: [String] = []
    @State private var showingClosetListByChoice = false

    private enum Tab: Hashable { case closets, browse, outfits, wishlist, account }

    var body: some View {
        TabView(selection: $selectedTab) {
            NavigationStack(path: $closetPath) {
                ClosetsView()
                    .navigationDestination(for: String.self) { BrowseView(closetId: $0) }
            }
            .tabItem { Label("Closets", systemImage: "square.grid.2x2") }
            .tag(Tab.closets)
            NavigationStack { BrowseView() }.tabItem { Label("Browse", systemImage: "magnifyingglass") }.tag(Tab.browse)
            NavigationStack { OutfitsView() }.tabItem { Label("Outfits", systemImage: "person.crop.rectangle.stack") }.tag(Tab.outfits)
            NavigationStack { WishlistView() }.tabItem { Label("Wishlist", systemImage: "heart") }.tag(Tab.wishlist)
            NavigationStack { AccountView() }.tabItem { Label("Account", systemImage: "person.circle") }.tag(Tab.account)
        }
        .onAppear { openOnlyCloset() }
        .onChange(of: store.closets.map(\.id)) { _, _ in openOnlyCloset() }
        .onChange(of: closetPath) { previous, current in
            if !previous.isEmpty && current.isEmpty { showingClosetListByChoice = true }
        }
        .onChange(of: selectedTab) { _, selected in
            if selected == .closets {
                showingClosetListByChoice = false
                openOnlyCloset()
            }
        }
    }

    static func onlyClosetId(in closets: [Closet]) -> String? {
        closets.count == 1 ? closets.first?.id : nil
    }

    private func openOnlyCloset() {
        guard selectedTab == .closets, !showingClosetListByChoice,
              closetPath.isEmpty,
              let id = Self.onlyClosetId(in: store.closets) else { return }
        closetPath = [id]
    }
}

// Errors are shown by the root view. Save buttons remain available after a failed write.
struct ClosetsView: View {
    @EnvironmentObject private var store: ClosetStore
    @State private var adding = false
    @State private var name = ""
    @State private var deleting: Closet?
    var body: some View {
        List {
            if store.closets.isEmpty { ContentUnavailableView("No closets yet", systemImage: "door.left.hand.open", description: Text("Create a closet to start adding pieces.")) }
            ForEach(store.closets.sorted { $0.name < $1.name }) { closet in
                NavigationLink(value: closet.id) {
                    HStack { Text(closet.name); Spacer(); Text("\(store.items.filter { $0.closetId == closet.id }.count)").foregroundStyle(.secondary) }
                }.swipeActions { Button("Delete", role: .destructive) { deleting = closet } }
            }
        }
        .navigationTitle("Closets")
        .refreshable { await store.refresh() }
        .toolbar { Button("Add closet", systemImage: "plus") { name = ""; adding = true } }
        .alert("New closet", isPresented: $adding) {
            TextField("Name", text: $name)
            Button("Create") { Task { do { try await store.saveCloset(Closet(name: name)) } catch { store.report(error) } } }
            Button("Cancel", role: .cancel) {}
        }
        .confirmationDialog("Delete \(deleting?.name ?? "") and its items? Items will also be removed from outfits.", isPresented: Binding(get: { deleting != nil }, set: { if !$0 { deleting = nil } })) {
            Button("Delete closet", role: .destructive) {
                guard let closet = deleting else { return }
                Task { do { try await store.deleteCloset(closet) } catch { store.report(error) } }
                deleting = nil
            }
        }
    }
}

struct BrowseView: View {
    @EnvironmentObject private var store: ClosetStore
    var closetId: String? = nil
    @State private var search = ""
    @State private var category: String = "All"
    @State private var cleanliness = "All"
    @State private var selectedCloset = "All"
    @State private var size: GarmentSize = .unknown
    @State private var fit: GarmentFit = .unknown
    @State private var brand = ""
    @State private var color = ""
    @State private var style = ""
    @State private var material = ""
    @State private var editing: Garment?
    @State private var deleting: Garment?
    private var visible: [Garment] {
        store.items.filter { item in
            (closetId == nil || item.closetId == closetId) && (selectedCloset == "All" || item.closetId == selectedCloset) &&
            (category == "All" || item.category.rawValue == category) &&
            (cleanliness == "All" || (cleanliness == "Clean" && !item.isDirty) || (cleanliness == "Dirty" && item.isDirty)) &&
            (size == .unknown || item.size == size) && (fit == .unknown || item.fit == fit) &&
            (brand.trimmed.isEmpty || item.brand.localizedCaseInsensitiveContains(brand.trimmed)) &&
            (material.trimmed.isEmpty || item.material.localizedCaseInsensitiveContains(material.trimmed)) &&
            (color.trimmed.isEmpty || item.colors.contains { $0.localizedCaseInsensitiveContains(color.trimmed) }) &&
            (style.trimmed.isEmpty || item.styles.contains { $0.localizedCaseInsensitiveContains(style.trimmed) }) &&
            (search.trimmed.isEmpty || item.matchScore(search) > 0)
        }.sorted { a, b in
            let left = a.matchScore(search), right = b.matchScore(search)
            return left == right ? a.name < b.name : left > right
        }
    }
    var body: some View {
        List {
            if store.closets.isEmpty {
                ContentUnavailableView("Create a closet first", systemImage: "plus.square", description: Text("Add a closet from the Closets tab."))
            } else {
                Section {
                    Picker("Category", selection: $category) {
                        Text("All").tag("All")
                        ForEach(GarmentCategory.allCases) { Text($0.rawValue).tag($0.rawValue) }
                    }
                    Picker("Condition", selection: $cleanliness) { ForEach(["All", "Clean", "Dirty"], id: \.self) { Text($0) } }
                    DisclosureGroup("More filters") {
                        if closetId == nil {
                            Picker("Closet", selection: $selectedCloset) {
                                Text("All").tag("All")
                                ForEach(store.closets) { Text($0.name).tag($0.id) }
                            }
                        }
                        Picker("Size", selection: $size) { ForEach(GarmentSize.allCases) { Text($0.rawValue).tag($0) } }
                        Picker("Fit", selection: $fit) { ForEach(GarmentFit.allCases) { Text($0.rawValue).tag($0) } }
                        TextField("Brand", text: $brand)
                        TextField("Color", text: $color)
                        TextField("Style", text: $style)
                        TextField("Material", text: $material)
                    }
                }
                if visible.isEmpty { ContentUnavailableView.search(text: search) }
                ForEach(visible) { item in
                    Button { editing = item } label: {
                        HStack(spacing: 14) {
                            PhotoThumbnail(path: item.photoPath, symbol: item.category.symbol)
                            VStack(alignment: .leading) {
                                Text(item.name).foregroundStyle(.primary).font(.headline)
                                Text("\(item.displayType) · \(store.closets.first { $0.id == item.closetId }?.name ?? "Closet removed")") .foregroundStyle(.secondary).font(.caption)
                            }
                            Spacer()
                            if item.isDirty { Image(systemName: "washer").accessibilityLabel("Dirty") }
                        }
                    }
                    .swipeActions {
                        Button("Delete", role: .destructive) { deleting = item }
                        Button(item.isDirty ? "Mark clean" : "Mark dirty") {
                            var changed = item; changed.isDirty.toggle()
                            Task { do { try await store.saveItem(changed) } catch { store.report(error) } }
                        }.tint(.orange)
                    }
                }
            }
        }
        .searchable(text: $search, prompt: "Name, color, style, fit…")
        .refreshable { await store.refresh() }
        .navigationTitle(closetId.flatMap { id in store.closets.first { $0.id == id }?.name } ?? "Browse")
        .toolbar { if !store.closets.isEmpty { Button("Add item", systemImage: "plus") { var item = Garment(); item.closetId = closetId ?? store.closets[0].id; editing = item } } }
        .sheet(item: $editing) { ItemEditor(item: $0) }
        .confirmationDialog("Delete this item? It will be removed from outfits.", isPresented: Binding(get: { deleting != nil }, set: { if !$0 { deleting = nil } })) {
            Button("Delete item", role: .destructive) {
                guard let item = deleting else { return }; deleting = nil
                Task { do { try await store.deleteItem(item) } catch { store.report(error) } }
            }
        }
    }
}

struct PhotoThumbnail: View {
    @EnvironmentObject private var store: ClosetStore
    let path: String?
    let symbol: String
    @State private var url: URL?
    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: 10).fill(.quaternary)
            if let url { AsyncImage(url: url) { image in image.resizable().scaledToFill() } placeholder: { Image(systemName: symbol) } }
            else { Image(systemName: symbol).foregroundStyle(.secondary) }
        }
        .frame(width: 56, height: 56).clipShape(RoundedRectangle(cornerRadius: 10))
        .task(id: path) { url = nil; if let path { url = await store.photoURL(path) } }
    }
}

struct GarmentPhotoGallery: View {
    @EnvironmentObject private var store: ClosetStore
    let paths: [String]
    @State private var urls: [String: URL] = [:]
    var body: some View {
        if !paths.isEmpty {
            ScrollView(.horizontal) {
                HStack(spacing: 12) {
                    ForEach(Array(paths.enumerated()), id: \.offset) { index, path in
                        VStack {
                            if let url = urls[path] {
                                AsyncImage(url: url) { image in
                                    image.resizable().scaledToFit()
                                } placeholder: { ProgressView() }
                                .frame(width: 240, height: 240)
                            } else { ProgressView().frame(width: 240, height: 240) }
                            Text(index == 0 ? "Front / primary" : "Back / additional")
                                .font(.caption).foregroundStyle(.secondary)
                        }
                    }
                }
            }
            .task(id: paths) {
                for path in paths { urls[path] = await store.photoURL(path) }
            }
        }
    }
}

struct PhotoPicker: View {
    @Binding var image: UIImage?
    var existingPath: String?
    var symbol: String = "photo"
    @State private var selection: PhotosPickerItem?
    var body: some View {
        HStack {
            if let image { Image(uiImage: image).resizable().scaledToFill().frame(width: 72, height: 72).clipShape(RoundedRectangle(cornerRadius: 10)) }
            else { PhotoThumbnail(path: existingPath, symbol: symbol) }
            PhotosPicker(image == nil && existingPath == nil ? "Add a photo (recommended)" : "Change photo", selection: $selection, matching: .images)
        }
        .onChange(of: selection) { _, newValue in
            Task { if let data = try? await newValue?.loadTransferable(type: Data.self), let selected = UIImage(data: data) { image = selected } }
        }
    }
}

struct ItemEditor: View {
    @EnvironmentObject private var store: ClosetStore
    @Environment(\.dismiss) private var dismiss
    @State private var item: Garment
    @State private var colors: String
    @State private var styles: String
    @State private var image: UIImage?
    @State private var busy = false
    init(item: Garment) {
        _item = State(initialValue: item)
        _colors = State(initialValue: item.colors.joined(separator: ", "))
        _styles = State(initialValue: item.styles.joined(separator: ", "))
    }
    var body: some View {
        NavigationStack {
            Form {
                Section("Photos") {
                    PhotoPicker(image: $image, existingPath: item.photoPath, symbol: item.category.symbol)
                    GarmentPhotoGallery(paths: item.photoPaths)
                }
                Section("Essentials") {
                    TextField("Name", text: $item.name)
                    Picker("Closet", selection: $item.closetId) { ForEach(store.closets) { Text($0.name).tag($0.id) } }
                    Picker("Category", selection: $item.category) { ForEach(GarmentCategory.allCases) { Text($0.rawValue).tag($0) } }
                        .onChange(of: item.category) { _, next in item.type = next.types[0]; item.customType = "" }
                    Picker("Type", selection: $item.type) { ForEach(item.category.types, id: \.self) { Text($0) } }
                    if item.type == "Other" { TextField("Custom type", text: $item.customType) }
                }
                Section("Details") {
                    TextField("Colors (comma separated)", text: $colors)
                    TextField("Styles (comma separated)", text: $styles)
                    TextField("Brand", text: $item.brand)
                    TextField("Material", text: $item.material)
                    Picker("Size", selection: $item.size) { ForEach(GarmentSize.allCases) { Text($0.rawValue).tag($0) } }
                    if item.category != .accessories {
                        Picker("Fit", selection: $item.fit) { ForEach(GarmentFit.allCases) { Text($0.rawValue).tag($0) } }
                        Picker("Length", selection: $item.length) { ForEach(GarmentLength.allCases) { Text($0.rawValue).tag($0) } }
                    }
                    Toggle("Dirty", isOn: $item.isDirty)
                }
                if busy { ProgressView() }
            }
            .navigationTitle("Item")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button("Cancel") { dismiss() } }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Save") {
                        item.colors = colors.tags; item.styles = styles.tags
                        busy = true
                        Task { do { try await store.saveItem(item, photo: image); dismiss() } catch { store.report(error); busy = false } }
                    }.disabled(busy || item.name.trimmed.isEmpty || item.closetId.isEmpty || (item.type == "Other" && item.customType.trimmed.isEmpty))
                }
            }
        }
    }
}
