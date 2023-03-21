package ui.swing;

import model.AccountManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

// Virtual Closet swing GUI application
public class VirtualClosetSwingApp extends JFrame {

    private static final String APP_TITLE = "Virtual Closet";

    private AccountManager accountManager;

    private View currentView;

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

        this.setCurrentView(new AccountChooserView(accountManager));
    }

    // MODIFIES: this
    // EFFECTS: Sets the current view to the given
    private void setCurrentView(View view) {
        this.currentView = view;
        view.init();

        Container cp = this.getContentPane();
        cp.removeAll();
        cp.add(this.currentView);
        this.pack();
    }

    // MODIFIES: this
    // EFFECTS: Sets up window parameters such as size, title, etc
    private void initWindowParams() {
        this.setTitle(APP_TITLE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
