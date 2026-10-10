import Foundation
import Security

let service = "com.camackenzie.virtualcloset.mcp"
let account = "firebase-refresh-token"
let query: [String: Any] = [kSecClass as String: kSecClassGenericPassword,
                            kSecAttrService as String: service, kSecAttrAccount as String: account]
func fail(_ status: OSStatus) -> Never {
    let text = SecCopyErrorMessageString(status, nil) as String? ?? "Keychain error \(status)"
    fputs("Keychain: \(text)\n", stderr)
    exit(1)
}
switch CommandLine.arguments.dropFirst().first {
case "get":
    var result: CFTypeRef?
    var search = query
    search[kSecReturnData as String] = true
    search[kSecMatchLimit as String] = kSecMatchLimitOne
    let status = SecItemCopyMatching(search as CFDictionary, &result)
    guard status == errSecSuccess else { fail(status) }
    guard let data = result as? Data, let token = String(data: data, encoding: .utf8) else { exit(1) }
    print(token)
case "set":
    let data = FileHandle.standardInput.readDataToEndOfFile()
    guard !data.isEmpty else { fputs("Empty credential\n", stderr); exit(1) }
    let attrs: [String: Any] = [kSecValueData as String: data]
    let status = SecItemUpdate(query as CFDictionary, attrs as CFDictionary)
    if status == errSecItemNotFound {
        var item = query
        item[kSecValueData as String] = data
        let addStatus = SecItemAdd(item as CFDictionary, nil)
        if addStatus != errSecSuccess { fail(addStatus) }
    } else if status != errSecSuccess { fail(status) }
case "delete":
    let status = SecItemDelete(query as CFDictionary)
    if status != errSecSuccess && status != errSecItemNotFound { fail(status) }
default:
    fputs("Usage: keychain get|set|delete\n", stderr)
    exit(2)
}
