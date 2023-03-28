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

// An outfit edit view to edit a given outfit
public class OutfitEditView extends View {

    private final AccountManager accountManager;
    private final Outfit outfit;

    private ClosetSearchPanel closetSearchPanel;
    private PromptedTextField closetNameField;
    private JButton setClosetFromNameButton;
    private JList<Clothing> clothingJList;

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

        this.add(closetNameField = PromptedTextField.prompt("Closet Name to Search"),
                GBC.hfillNorth(0, 0).insets(2));

        this.add(setClosetFromNameButton = new JButton("Set Closet"),
                GBC.hfillNorth(1, 0).insets(2));

        this.add(closetSearchPanel = new ClosetSearchPanel(null),
                GBC.at(0, 1).fillBoth().weight(1, 1).gridwidth(2));

        this.add(new JScrollPane(clothingJList = new JList<>()),
                GBC.at(2, 0).gridheight(2).fillBoth().weight(1, 1));
        clothingJList.setCellRenderer((list, val, idx, sel, foc) ->
                new ClothingListItem(this, val, sel));

        refreshClothingList();
    }

    // REQUIRES: addComponents has been called
    // MODIFIES: this
    // EFFECTS: Refreshes clothing list components
    private void refreshClothingList() {
        this.clothingJList.setListData(outfit.getClothing().toArray(new Clothing[0]));
    }

    @Override
    void addEventListeners() {
        setClosetFromNameButton.addActionListener(l -> {
            if (closetNameField.hasTextValue()) {
                Closet c = accountManager.getActiveAccount()
                        .getCloset(closetNameField.getText()).orElse(null);
                closetSearchPanel.setCloset(c);
            }
        });
    }


}
