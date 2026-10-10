package ui.swing.views;

import model.AccountManager;
import model.Closet;
import model.Clothing;
import model.Outfit;
import ui.swing.utils.ClosetSearchPanel;
import ui.swing.utils.ClothingListItem;
import ui.swing.utils.GBC;
import ui.swing.utils.PromptedTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

// An outfit edit view to edit a given outfit
public class OutfitEditView extends View {

    private final AccountManager accountManager;
    private final Outfit outfit;

    private ClosetSearchPanel closetSearchPanel;
    private PromptedTextField closetNameField;
    private JButton setClosetFromNameButton;
    private JList<Clothing> clothingJList;

    private JButton exitButton;
    private JButton addClothingButton;
    private JButton removeClothingButton;

    // EFFECTS: Constructs a new outfit edit view for the provided outfit
    //          attached to the given root.
    public OutfitEditView(Container root,
                          AccountManager accountManager,
                          Outfit outfit) {
        super(root);
        this.accountManager = accountManager;
        this.outfit = outfit;
    }

    @Override
    void addComponents() {
        this.setLayout(new GridBagLayout());

        this.addClosetSearchComponents();
        this.addOutfitEditComponents();

        refreshClothingList();
    }

    // REQUIRES: addComponents has been called
    // MODIFIES: this
    // EFFECTS: Refreshes clothing list components
    private void refreshClothingList() {
        this.clothingJList.setListData(outfit.getClothing().toArray(new Clothing[0]));
        addClothingButton.setEnabled(closetSearchPanel.getSelected() != null);
        removeClothingButton.setEnabled(clothingJList.getSelectedValue() != null);
    }

    @Override
    void addEventListeners() {
        this.addClosetSearchListeners();
        this.addOutfitEditListeners();
    }

    // REQUIRES: addClosetSearchComponents has not been called
    // MODIFIES: this
    // EFFECTS: Adds closet searching components
    private void addClosetSearchComponents() {
        this.add(closetNameField = PromptedTextField.prompt("Closet Name to Search"),
                GBC.hfillNorth(0, 0).insets(2).weightx(0.5));

        this.add(setClosetFromNameButton = new JButton("Set Closet"),
                GBC.hfillNorth(1, 0).insets(2).weightx(0.5));

        this.add(closetSearchPanel = new ClosetSearchPanel(null),
                GBC.at(0, 1).fillBoth().weight(1, 1).gridwidth(2)
                        .gridheight(1));
    }

    // REQUIRES: addClosetSearchListeners has not been called
    // MODIFIES: this
    // EFFECTS: Adds listeners to closet search components
    private void addClosetSearchListeners() {
        ActionListener setClosetAction = e -> {
            if (closetNameField.hasTextValue()) {
                Closet c = accountManager.getActiveAccount()
                        .getCloset(closetNameField.getText()).orElse(null);
                closetSearchPanel.setCloset(c);
            }
        };
        setClosetFromNameButton.addActionListener(setClosetAction);
        closetNameField.addActionListener(setClosetAction);
        closetSearchPanel.addListSelectionListener(l -> {
            if (l.getValueIsAdjusting()) {
                addClothingButton.setEnabled(closetSearchPanel.getSelected() != null);
            }
        });
    }

    // REQUIRES: addOutfitEditComponents has not been called
    // MODIFIES: this
    // EFFECTS: Adds outfit edit components
    private void addOutfitEditComponents() {
        this.add(new JScrollPane(clothingJList = new JList<>()),
                GBC.at(2, 1).gridheight(1).fillBoth().weight(1, 1).insets(2));
        clothingJList.setCellRenderer((list, val, idx, sel, foc) ->
                new ClothingListItem(this, val, sel));

        this.add(exitButton = new JButton("Exit"),
                GBC.hfillNorth(2, 0).insets(2));

        this.add(addClothingButton = new JButton("Add Selected"),
                GBC.hfillNorth(0, 2).insets(0, 2, 2, 2).gridwidth(2));

        this.add(removeClothingButton = new JButton("Remove Selected"),
                GBC.hfillNorth(2, 2).insets(0, 2, 2, 2));
    }

    // REQUIRES: addOutfitEditListeners has not been called
    // MODIFIES: this
    // EFFECTS: Adds listeners to outfit edit components
    private void addOutfitEditListeners() {
        exitButton.addActionListener(a ->
                this.transition(new CatalogueView(root, accountManager)));
        clothingJList.addListSelectionListener(l -> {
            if (l.getValueIsAdjusting()) {
                removeClothingButton.setEnabled(clothingJList.getSelectedValue() != null);
                if (clothingJList.getSelectedValue() != null) {
                    closetSearchPanel.displayClothingInfo(clothingJList.getSelectedValue());
                }
            }
        });
        addClothingButton.addActionListener(l -> {
            if (closetSearchPanel.getSelected() != null) {
                outfit.addClothing(closetSearchPanel.getSelected());
                refreshClothingList();
            }
        });
        removeClothingButton.addActionListener(e -> {
            if (clothingJList.getSelectedValue() != null) {
                outfit.removeClothing(clothingJList.getSelectedValue());
                refreshClothingList();
            }
        });
    }
}