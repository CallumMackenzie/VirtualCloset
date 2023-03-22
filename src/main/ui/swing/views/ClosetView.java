package ui.swing.views;

import model.AccountManager;
import model.Closet;

import java.awt.*;

// TODO
public class ClosetView extends View {

    private final AccountManager accountManager;
    private final Closet closet;

    // TODO
    public ClosetView(Container root,
                      AccountManager accountManager,
                      Closet closet) {
        super(root);
        this.accountManager = accountManager;
        this.closet = closet;
    }

    @Override
    void addComponents() {
        this.setLayout(new GridBagLayout());
    }

    @Override
    void addEventListeners() {

    }
}
