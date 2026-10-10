package ui.swing.views;

import model.Account;
import model.AccountManager;
import model.Closet;
import ui.swing.utils.GBC;
import ui.swing.utils.PromptedTextField;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

// A view to see user closets and open the catalogue
public class HomeView extends View {

    private static final String DELETE_CLOSET_INITIAL = "Delete Closet";
    private static final String DELETE_CLOSET_CONFIRM = "Confirm Delete?";

    private final AccountManager accountManager;
    private final Account active;

    private JButton exitButton;

    private JList<Closet> closetJList;
    private JButton addClosetButton;
    private PromptedTextField addClosetNameField;

    private JLabel selectedClosetName;
    private JButton openSelectedClosetButton;
    private JButton deleteSelectedClosetButton;
    private boolean deleteSelectedClosetConfirmed;
    private JButton openCatalogueButton;
    private JTextArea closetInfoPanel;

    // EFFECTS: Constructs a new home view from the given root pane and account
    //          manager.
    public HomeView(Container root, AccountManager accountManager) {
        super(root);
        this.accountManager = accountManager;
        this.active = accountManager.getActiveAccount();
    }

    // EFFECTS: Returns the prefix appended with either NA if o is null
    //          or the result of fn if it is not.
    private static String formatNaNullStr(String prefix,
                                          Closet c,
                                          Function<Closet, Object> getter) {
        return prefix + (c == null ? "NA" : getter.apply(c)) + "\n";
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
                GBC.hfillNorth(2, 1).gridwidth(2));

        closetInfoPanel = new JTextArea();
        closetInfoPanel.setEditable(false);
        closetInfoPanel.setWrapStyleWord(true);
        closetInfoPanel.setLineWrap(true);
        JScrollPane jsp = new JScrollPane(closetInfoPanel);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(jsp, GBC.at(2, 2).fillBoth().gridwidth(2).insets(3));

        this.add(openSelectedClosetButton = new JButton("Open Closet"),
                GBC.hfillNorth(2, 3).insets(2));

        this.add(deleteSelectedClosetButton = new JButton(DELETE_CLOSET_INITIAL),
                GBC.hfillNorth(3, 3).insets(2));
    }

    // REQUIRES: addSelectedClosetListeners has not been called
    // MODIFIES: this
    // EFFECTS: Adds top bar component listeners
    private void addSelectedClosetListeners() {
        openSelectedClosetButton.addActionListener(e ->
                this.transition(new ClosetView(this.root,
                        this.accountManager,
                        closetJList.getSelectedValue())));
        deleteSelectedClosetButton.addActionListener(e -> {
            if (active != null) {
                if (deleteSelectedClosetConfirmed) {
                    active.removeCloset(closetJList.getSelectedValue().getName());
                    this.setSelectedCloset(null);
                    this.refreshClosetListData();
                    deleteSelectedClosetConfirmed = false;
                    deleteSelectedClosetButton.setText(DELETE_CLOSET_INITIAL);
                } else {
                    deleteSelectedClosetConfirmed = true;
                    deleteSelectedClosetButton.setText(DELETE_CLOSET_CONFIRM);
                }
            }
        });
    }

    // REQUIRES: addTopBarComponents has not been called
    // MODIFIES: this
    // EFFECTS: Adds top bar view components
    private void addTopBarComponents() {
        this.add(new JLabel("Welcome " + active.getName() + "!"),
                GBC.at(0, 0).insets(2));

        this.add(exitButton = new JButton("Exit"),
                GBC.at(2, 0).hfill().insets(2).gridwidth(2));

        this.add(openCatalogueButton = new JButton("Open Catalogue"),
                GBC.at(1, 0).hfill().insets(2));
    }

    // REQUIRES: addTopBarListeners has not been called
    // MODIFIES: this
    // EFFECTS: Adds top bar component listeners
    private void addTopBarListeners() {
        this.exitButton.addActionListener(e ->
                this.transition(new AccountChooserView(this.root, accountManager)));
        this.openCatalogueButton.addActionListener(e ->
                this.transition(new CatalogueView(this.root, accountManager)));
    }

    // REQUIRES: addClosetListComponents has not been called
    // MODIFIES: this
    // EFFECTS: Adds closet list components
    private void addClosetListComponents() {
        final int height = 2;

        this.closetJList = new JList<>();
        this.closetJList.setCellRenderer(ClosetListItem::new);
        this.refreshClosetListData();
        this.add(new JScrollPane(this.closetJList),
                GBC.at(0, 1)
                        .north()
                        .fillBoth()
                        .weight(0.5, 1)
                        .insets(5)
                        .gridheight(height)
                        .gridwidth(2));

        this.add(addClosetButton = new JButton("Create New Closet"),
                GBC.hfillNorth(1, height + 1).insets(2)
                        .weightx(0.3));

        this.add(addClosetNameField = PromptedTextField.prompt("Closet Name"),
                GBC.hfillNorth(0, height + 1).insets(2)
                        .weightx(0.7));
    }

    // REQUIRES: addClosetListListeners has not been called
    // MODIFIES: this
    // EFFECTS: Adds top bar component listeners
    private void addClosetListListeners() {
        this.addClosetButton.addActionListener(e -> {
            if (addClosetNameField.hasTextValue()) {
                active.addCloset(addClosetNameField.getText());
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
        this.selectedClosetName.setText(c == null
                ? "No Selected Closet" : "Selected Closet: " + c.getName());
        openSelectedClosetButton.setEnabled(c != null);
        deleteSelectedClosetButton.setEnabled(c != null);
        deleteSelectedClosetConfirmed = false;
        deleteSelectedClosetButton.setText(DELETE_CLOSET_INITIAL);

        closetInfoPanel.setText(
                formatNaNullStr("Count: ",
                        c, x -> x.getClothing().size())
                        + formatNaNullStr("Types: ",
                        c, Closet::getTypes)
                        + formatNaNullStr("Sizes: ",
                        c, Closet::getSizes)
                        + formatNaNullStr("Colors: ",
                        c, Closet::getColors)
                        + formatNaNullStr("Brands: ",
                        c, Closet::getBrands)
                        + formatNaNullStr("Styles: ",
                        c, Closet::getStyles));
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
