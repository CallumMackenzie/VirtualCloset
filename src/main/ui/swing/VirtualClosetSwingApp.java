package ui.swing;

import model.AccountManager;
import ui.swing.utils.LookAndFeelManager;
import ui.swing.views.AccountChooserView;

import javax.swing.*;
import java.io.IOException;

// Virtual Closet swing GUI application
public class VirtualClosetSwingApp extends JFrame {

    private static final String APP_TITLE = "Virtual Closet";

    private AccountManager accountManager;

    // EFFECTS: Creates and runs a new virtual closet swing application
    public VirtualClosetSwingApp() {
        super();
        LookAndFeelManager.setLookAndFeel();
        this.initWindowParams();
        this.initAppState();
        this.setVisible(true);
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

        AccountChooserView av = new AccountChooserView(this.getContentPane(), accountManager);
        av.init();
        this.pack();
    }

    // MODIFIES: this
    // EFFECTS: Sets up window parameters such as size, title, etc
    private void initWindowParams() {
        this.setTitle(APP_TITLE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
