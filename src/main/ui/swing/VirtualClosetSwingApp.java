package ui.swing;

import model.AccountManager;
import ui.swing.utils.AssetLoader;
import ui.swing.utils.LookAndFeelManager;
import ui.swing.views.AccountChooserView;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;

// Virtual Closet swing GUI application
public class VirtualClosetSwingApp extends JFrame {

    private static final String APP_TITLE = "Virtual Closet";

    private AccountManager accountManager;

    // EFFECTS: Creates and runs a new virtual closet swing application
    public VirtualClosetSwingApp() {
        LookAndFeelManager.setLookAndFeel();

        this.loadAssets();

        this.initWindowParams();
        this.initAppState();
        this.initAppView();

        this.pack();
        this.setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: Loads assets asynchronously
    private void loadAssets() {
        AssetLoader al = AssetLoader.getInstance();
        File imgDir = new File("./data/img");
        for (File f : Objects.requireNonNull(imgDir.listFiles())) {
            if (f.isFile()) {
                al.loadImage(Paths.get(f.getPath()));
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: Initializes app state
    private void initAppState() {
        this.accountManager = new AccountManager();
        try {
            this.accountManager.loadState();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // MODIFIES: this
    // EFFECTS: Sets up initial app view
    private void initAppView() {
        new AccountChooserView(this.getContentPane(),
                this.accountManager).init();
    }

    // MODIFIES: this
    // EFFECTS: Sets up window parameters such as size, title, etc
    private void initWindowParams() {
        this.setTitle(APP_TITLE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
