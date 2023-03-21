package ui.swing.views;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

// A view pane within a GUI
public abstract class View extends JPanel {

    protected final Container root;

    // EFFECTS: Creates a new view with the given root
    public View(Container root) {
        this.root = root;
    }

    // REQUIRES: init has not been called
    // MODIFIES: this
    // EFFECTS: Initializes the given view and adds it to the root
    public void init() {
        this.addComponents();
        this.addEventListeners();
        this.root.add(this);
        this.root.revalidate();
        this.root.repaint();
    }

    // MODIFIES: this
    // EFFECTS: Replaces this view with the given
    public void transition(View other) {
        this.close();
        other.init();
    }

    // MODIFIES: this
    // EFFECTS: Closes the given view
    public void close() {
        this.removeSelf();
    }

    // EFFECTS: Removes self from root
    public void removeSelf() {
        this.root.remove(this);
    }

    // REQUIRES: addComponents has not been called before
    // MODIFIES: this
    // EFFECTS: Adds components to this view
    abstract void addComponents();

    // REQUIRES: addEventListeners has not been called before
    // MODIFIES: this
    // EFFECTS: Adds event listeners to the components of this view
    abstract void addEventListeners();

}
