package ui.swing;

import javax.swing.*;

// Virtual Closet swing GUI application
public class VirtualClosetSwingApp extends JFrame {

    private static final String APP_TITLE = "Virtual Closet";

    // EFFECTS: Creates and runs a new virtual closet swing application
    public VirtualClosetSwingApp() {
        super();
        this.initWindowParams();
        this.addComponents();
        this.pack();
        this.setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: Sets up window parameters such as size, title, etc
    private void initWindowParams() {
        this.setTitle(APP_TITLE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // REQUIRES: Components have not been added yet
    // MODIFIES: this
    // EFFECTS: Adds components to view
    private void addComponents() {

    }

}
