import SwiftUI
import FirebaseCore

@main
struct VirtualCloset2App: App {
    private let configured: Bool
    init() {
        if let path = Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist"),
           let options = FirebaseOptions(contentsOfFile: path) {
            FirebaseApp.configure(options: options)
            configured = true
        } else {
            configured = false
        }
    }
    var body: some Scene {
        WindowGroup {
            if configured { ConnectedApp() }
            else {
                ContentUnavailableView("Firebase setup needed", systemImage: "gearshape", description: Text("Add GoogleService-Info.plist to the app target. See README.md."))
            }
        }
    }
}

private struct ConnectedApp: View {
    @StateObject private var store = ClosetStore()
    var body: some View { ContentView().environmentObject(store) }
}
