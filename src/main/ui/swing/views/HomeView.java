package ui.swing.views;

import model.AccountManager;

import javax.swing.*;
import java.awt.*;

// TODO
public class HomeView extends View {

    private final AccountManager accountManager;
    private JButton exitButton;

    // TODO
    public HomeView(Container root, AccountManager accountManager) {
        super(root);
        this.accountManager = accountManager;
    }

    @Override
    void addComponents() {
        this.exitButton = new JButton("Exit");
        this.add(exitButton);
    }

    @Override
    void addEventListeners() {
        this.exitButton.addActionListener(e -> {
            this.transition(new AccountChooserView(
                    this.root,
                    accountManager
            ));
        });
    }
}
