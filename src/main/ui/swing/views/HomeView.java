package ui.swing.views;

import model.Account;
import model.AccountManager;
import model.Closet;
import ui.swing.utils.GBC;
import ui.swing.utils.PromptedTextField;

import javax.swing.*;
import java.awt.*;

// TODO
public class HomeView extends View {

    private static final String CLOSET_NAME_PROMPT = "Closet Name";

    private final AccountManager accountManager;
    private final Account active;
    private Closet selectedCloset;

    private JButton exitButton;

    private JList<Closet> closetJList;
    private JButton addClosetButton;
    private JTextField addClosetNameField;

    private JLabel selectedClosetName;
    private JButton openSelectedClosetButton;

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
        this.addSelectedComponents();

        this.setSelectedCloset(null);
    }

    @Override
    void addEventListeners() {
        this.addTopBarListeners();
        this.addClosetListListeners();
        this.addSelectedClosetListeners();
    }

    // REQUIRES: addSelectedComponents has not been called
    // MODIFIES: this
    // EFFECTS: Adds top bar view components
    private void addSelectedComponents() {
        this.add(selectedClosetName = new JLabel(),
                GBC.hFillNorth(2, 1).gridwidth(2));

        this.add(openSelectedClosetButton = new JButton("Open Closet"),
                GBC.hFillNorth(2, 2));

        // TODO
        this.add(new JButton("Delete Closet"),
                GBC.hFillNorth(3, 2));
    }

    // REQUIRES: addSelectedClosetListeners has not been called
    // MODIFIES: this
    // EFFECTS: Adds top bar component listeners
    private void addSelectedClosetListeners() {
        openSelectedClosetButton.addActionListener(e ->
                this.transition(new ClosetView(this.root)));
    }

    // REQUIRES: addTopBarComponents has not been called
    // MODIFIES: this
    // EFFECTS: Adds top bar view components
    private void addTopBarComponents() {
        this.add(new JLabel("Welcome " + active.getName() + "!"),
                GBC.at(0, 0).insets(2));

        this.add(exitButton = new JButton("Exit"),
                GBC.at(2, 0).hFill().insets(2));

        // TODO
        this.add(new JButton("Open Catalogue"),
                GBC.at(1, 0).hFill().insets(2));
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
                        .gridheight(1)
                        .gridwidth(2));

        this.add(addClosetButton = new JButton("Create New Closet"),
                GBC.hFillNorth(1, 2).insets(2)
                        .weightx(0.3));

        this.add(addClosetNameField = PromptedTextField.prompt(CLOSET_NAME_PROMPT),
                GBC.hFillNorth(0, 2).insets(2)
                        .weightx(0.7));
    }

    // REQUIRES: addClosetListListeners has not been called
    // MODIFIES: this
    // EFFECTS: Adds top bar component listeners
    private void addClosetListListeners() {
        this.addClosetButton.addActionListener(e -> {
            String t = this.addClosetNameField.getText();
            if (!t.isEmpty() && !t.equals(CLOSET_NAME_PROMPT)) {
                active.addCloset(t);
                this.addClosetNameField.setText("");
                this.refreshClosetListData();
            }
        });

        this.closetJList.addListSelectionListener(lst -> {
            if (lst.getValueIsAdjusting()) {
                this.setSelectedCloset(closetJList.getSelectedValue());
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: Refreshes components which use closet list data
    private void refreshClosetListData() {
        this.closetJList.setListData(this.active.getClosets().toArray(new Closet[0]));
    }

    // MODIFIES: this
    // EFFECTS: Sets selected closet and gui params based on input
    private void setSelectedCloset(Closet c) {
        this.selectedCloset = c;
        this.selectedClosetName.setText(c == null ?
                "No Selected Closet" : "Selected Closet: " + c.getName());
        openSelectedClosetButton.setEnabled(c != null);
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
