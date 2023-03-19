package ui.swing;

import javax.swing.*;

// TODO
public abstract class View extends JPanel {

    // TODO
    public View() {
    }

    // TODO
    public void init() {
        this.addComponents();
        this.addEventListeners();
    }

    // TODO
    abstract void addComponents();

    // TODO
    abstract void addEventListeners();

}
