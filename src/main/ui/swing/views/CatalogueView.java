package ui.swing.views;

import model.Account;
import model.AccountManager;
import model.Catalogue;
import model.Outfit;
import ui.swing.utils.GBC;
import ui.swing.utils.PromptedTextField;

import javax.swing.*;
import java.awt.*;

// A GUI view to view and edit catalogue contents
public class CatalogueView extends View {

    private final AccountManager accountManager;
    private final Account active;
    private final Catalogue catalogue;

    private JList<Outfit> outfitJList;
    private JButton exitButton;
    private JButton createOutfitButton;
    private PromptedTextField createOutfitNameField;
    private JLabel selectedOutfitName;
    private JButton editSelectedOutfitButton;
    private JButton deleteSelectedOutfitButton;

    // REQUIRES: accountManager has an active account
    // EFFECTS: Constructs a new catalogue view attached to the given root
    //          with the provided account manager viewing the provided catalogue.
    public CatalogueView(Container root,
                         AccountManager accountManager) {
        super(root);
        this.accountManager = accountManager;
        this.active = accountManager.getActiveAccount();
        this.catalogue = active.getCatalogue();
    }

    @Override
    void addComponents() {
        this.setLayout(new GridBagLayout());

        this.add(new JLabel(active.getName() + "'s Catalogue"),
                GBC.hfillNorth(0, 0).insets(2));

        this.outfitJList = new JList<>();
        this.outfitJList.setCellRenderer(OutfitListItem::new);
        this.add(new JScrollPane(outfitJList),
                GBC.at(0, 1).fillBoth().insets(2).north().weight(0.65, 1)
                        .gridheight(3));

        this.add(exitButton = new JButton("Exit"),
                GBC.hfillNorth(1, 0).insets(2));

        this.add(createOutfitNameField = PromptedTextField.prompt("New Outfit Name"),
                GBC.hfillNorth(0, 4).insets(2));

        this.add(createOutfitButton = new JButton("Create Outfit"),
                GBC.hfillNorth(1, 4).insets(2));

        this.add(selectedOutfitName = new JLabel(),
                GBC.hfillNorth(1, 1).insets(2));

        this.add(editSelectedOutfitButton = new JButton("Edit Selected"),
                GBC.hfillNorth(1, 2).insets(2));

        this.add(deleteSelectedOutfitButton = new JButton("Delete Selected"),
                GBC.hfillNorth(1, 3).insets(2));

        this.refreshOutfitList();
        this.refreshSelectedOutfit();
    }

    @Override
    void addEventListeners() {
        exitButton.addActionListener(e ->
                this.transition(new HomeView(root, accountManager)));
        outfitJList.addListSelectionListener(l -> {
            if (l.getValueIsAdjusting()) {
                this.refreshSelectedOutfit();
            }
        });
        createOutfitButton.addActionListener(e -> {
            if (createOutfitNameField.hasTextValue()) {
                // TODO
            }
        });
        editSelectedOutfitButton.addActionListener(e -> {
            // TODO
        });
        deleteSelectedOutfitButton.addActionListener(e -> {
            Outfit selected = outfitJList.getSelectedValue();
            if (selected != null) {
                catalogue.removeAllWithName(selected.getName());
            }
        });
    }

    // REQUIRES: addComponents has been called
    // MODIFIES: this
    // EFFECTS: Refreshes components which depend on the outfit list
    private void refreshOutfitList() {
        this.outfitJList.setListData(catalogue.getOutfits().toArray(new Outfit[0]));
    }

    // REQUIRES: addComponents has been called
    // MODIFIES: this
    // EFFECTS: Refreshes components which depend on the selected outfit
    private void refreshSelectedOutfit() {
        Outfit selected = outfitJList.getSelectedValue();
        selectedOutfitName.setText("Selected Outfit: "
                + (selected == null ? "NA" : selected.getName()));
        deleteSelectedOutfitButton.setEnabled(selected != null);
        editSelectedOutfitButton.setEnabled(selected != null);
    }

    // A list view item for an outfit
    private static class OutfitListItem extends JPanel {

        private final Outfit value;

        // EFFECTS: Constructs a new outfit list item
        public OutfitListItem(JList<? extends Outfit> list,
                              Outfit value,
                              int index,
                              boolean isSelected,
                              boolean cellHasFocus) {
            this.value = value;
            this.add(new JLabel(value.getName()));
        }
    }
}
