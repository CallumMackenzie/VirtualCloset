package ui.swing.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

// Loads assets asynchronously and provides an interface to access them
public class AssetLoader {

    private static AssetLoader assetLoader = null;

    private final LinkedBlockingQueue<Path> imageLoadQueue;
    private final ConcurrentMap<Path, Image> imageAssets;
    private final ConcurrentMap<Path, Runnable> imageCompletionListeners;
    private final ConcurrentSkipListSet<Path> failedLoading;
    private Thread imageLoaderThread;

    // EFFECTS: Creates a new asset loader with an empty image load queue
    private AssetLoader() {
        this.imageLoadQueue = new LinkedBlockingQueue<>();
        imageAssets = new ConcurrentHashMap<>();
        imageCompletionListeners = new ConcurrentHashMap<>();
        failedLoading = new ConcurrentSkipListSet<>();
    }

    // EFFECTS: Creates a new asset loader if there is no instance,
    //          otherwise returns the static instance.
    public static AssetLoader getInstance() {
        if (assetLoader == null) {
            return assetLoader = new AssetLoader();
        }
        return assetLoader;
    }

    // MODIFIES: this
    // EFFECTS: Sets the given image asset to be loaded asynchronously
    public void loadImage(Path path) {
        if (!imageAssets.containsKey(path)) {
            imageLoadQueue.add(path);
            if (this.imageLoaderThread == null
                    || !this.imageLoaderThread.isAlive()) {
                this.imageLoaderThread = new Thread(this::loadAllInQueue);
                this.imageLoaderThread.start();
            }
        } else if (imageAssets.get(path) != null) {
            if (this.imageCompletionListeners.containsKey(path)) {
                this.imageCompletionListeners.get(path).run();
            }
        }
    }

    // EFFECTS: Returns the image loaded at the given path, or a promise to that
    //          image if it is already loaded. If the given image is not registered
    //          to be loaded, it is loaded.
    public void getImage(Path path, Consumer<Image> onLoad) {
        this.imageCompletionListeners.put(path, () ->
                onLoad.accept(this.imageAssets.get(path)));
        this.loadImage(path);
    }

    // MODIFIES: this
    // EFFECTS: Loads all images from the imageLoadQueue
    private void loadAllInQueue() {
        while (!imageLoadQueue.isEmpty()) {
            Path nextImage = imageLoadQueue.poll();
            if (!failedLoading.contains(nextImage)) {
                try {
                    Image img = ImageIO.read(new File(nextImage.toUri()));
                    imageAssets.put(nextImage, img);
                    if (imageCompletionListeners.containsKey(nextImage)) {
                        imageCompletionListeners.get(nextImage).run();
                        imageCompletionListeners.remove(nextImage);
                    }
                    System.out.println("Loaded " + nextImage);
                } catch (IOException e) {
                    System.err.println("Could not load image " + nextImage);
                    failedLoading.add(nextImage);
                }
            }
        }
    }

}
