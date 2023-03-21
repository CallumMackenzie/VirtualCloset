package ui.swing.views;

import model.Account;
import model.AccountManager;
import model.Closet;
import ui.swing.utils.GBC;

import javax.swing.*;
import java.awt.*;

// TODO
public class HomeView extends View {

    private final AccountManager accountManager;
    private final Account active;

    private JButton exitButton;

    private JList<Closet> closetJList;

    // TODO
    public HomeView(Container root, AccountManager accountManager) {
        super(root);
        this.accountManager = accountManager;
        this.active = accountManager.getActiveAccount();
    }

    @Override
    void addComponents() {
        this.setLayout(new GridBagLayout());

        this.addTopBarComponents();
        this.addClosetListComponents();
    }

    @Override
    void addEventListeners() {
        this.addTopBarListeners();
    }

    // REQUIRES: addTopBarComponents has not been called
    // MODIFIES: this
    // EFFECTS: Adds top bar view components
    private void addTopBarComponents() {
        this.add(exitButton = new JButton("Exit"),
                GBC.at(1, 0).north());

        this.add(new JLabel("Welcome " + active.getName() + "!"),
                GBC.at(0, 0).north());
    }

    // REQUIRES: addTopBarListeners has not been called
    // MODIFIES: this
    // EFFECTS: Adds top bar component listeners
    private void addTopBarListeners() {
        this.exitButton.addActionListener(e -> this.transition(new AccountChooserView(
                this.root,
                accountManager
        )));
    }

    // REQUIRES: addClosetListComponents has not been called
    // MODIFIES: this
    // EFFECTS: Adds closet list components
    private void addClosetListComponents() {
        this.closetJList = new JList<>();
        this.closetJList.setCellRenderer(ClosetListItem::new);
        this.refreshClosetListData();
        this.add(new JScrollPane(this.closetJList),
                GBC.at(0, 1)
                        .north()
                        .fillBoth()
                        .weight(0.5, 1)
                        .insets(5)
                        .gridheight(GridBagConstraints.REMAINDER));
    }

    // MODIFIES: this
    // EFFECTS: Refreshes components which use closet list data
    private void refreshClosetListData() {
        this.closetJList.setListData(this.active.getClosets().toArray(new Closet[0]));
    }

    // A list view item for a closet
    private static class ClosetListItem extends JPanel {

        // EFFECTS: Constructs a new closet list item
        public ClosetListItem(JList<? extends Closet> list,
                              Closet value,
                              int index,
                              boolean isSelected,
                              boolean cellHasFocus) {
            if (isSelected) {
                this.setBackground(UIManager
                        .getDefaults().getColor("List.selectionBackground"));
            }
            this.add(new JLabel(value.getName()));
        }
    }
}
