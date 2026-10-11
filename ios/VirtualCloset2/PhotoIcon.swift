import UIKit

// A stored, square 250-pixel JPEG for all image previews. Full images remain untouched.
enum PhotoIcon {
    static let pixels = 250

    static func jpegData(from image: UIImage) -> Data? {
        guard image.size.width > 0, image.size.height > 0 else { return nil }
        let side = CGFloat(pixels)
        let bounds = CGRect(x: 0, y: 0, width: side, height: side)
        // Fit the whole garment inside the square rather than cropping off its edges.
        let scale = min(side / image.size.width, side / image.size.height)
        let size = CGSize(width: image.size.width * scale, height: image.size.height * scale)
        let rect = CGRect(x: (side - size.width) / 2, y: (side - size.height) / 2,
                          width: size.width, height: size.height)
        let format = UIGraphicsImageRendererFormat()
        format.scale = 1 // 250 pixels, not 250 points at the device screen scale.
        format.opaque = true
        return UIGraphicsImageRenderer(size: bounds.size, format: format)
            .jpegData(withCompressionQuality: 0.8) { context in
                UIColor.white.setFill()
                context.fill(bounds)
                image.draw(in: rect)
            }
    }
}
