package ui.swing.views;

import model.AccountManager;
import model.Closet;
import model.Clothing;
import model.Size;
import ui.swing.utils.*;

import javax.swing.*;
import java.awt.*;

// A clothing edit view to edit given clothing
public class ClothingEditView extends View {

    private final AccountManager accountManager;
    private final Closet closet;
    private final Clothing baseClothing;
    private final Clothing copyClothing;

    private JButton saveAndExitButton;
    private JButton cancelButton;

    // EFFECTS: Creates a new clothing edit view for the given clothing.
    //          Base clothing may or may not be in closet.
    public ClothingEditView(Container root,
                            AccountManager accountManager,
                            Closet closet,
                            Clothing base) {
        super(root);
        this.accountManager = accountManager;
        this.closet = closet;
        this.baseClothing = base;
        this.copyClothing = base.copy();
    }

    @Override
    void addComponents() {
        this.setLayout(new GridBagLayout());

        this.addMainControlComponents();
        this.addClothingEditComponents();
    }

    @Override
    void addEventListeners() {
        this.addMainControlListeners();
        this.addClothingEditListeners();
    }

    // REQUIRES: addClothingEditComponents has not been called
    // MODIFIES: this
    // EFFECTS: Adds clothing edit components
    private void addClothingEditComponents() {
        this.add(new StringEditable("Brand",
                        copyClothing::setBrand,
                        copyClothing::getBrand),
                GBC.hfillNorth(0, 1).weightx(1));
        this.add(new StringEditable("Material",
                        copyClothing::setMaterial,
                        copyClothing::getMaterial),
                GBC.hfillNorth(1, 1).weightx(1));
        this.add(new StringListEditable("Colors", copyClothing::getColors),
                GBC.at(0, 2).fillBoth().weighty(1));
        this.add(new StringListEditable("Styles", copyClothing::getStyles),
                GBC.at(1, 2).fillBoth().weighty(1));
        this.add(new StringListEditable("Types", copyClothing::getTypes),
                GBC.at(0, 3).fillBoth().weighty(1));
        this.add(new EnumListEditable<>(Size.class, "Clothing Size",
                        copyClothing::setSize, copyClothing::getSize),
                GBC.at(1, 3).fillBoth().weighty(1));
    }

    // REQUIRES: addClothingEditListeners has not been called
    // MODIFIES: this
    // EFFECTS: Adds listeners to clothing edit components
    private void addClothingEditListeners() {

    }


    // REQUIRES: addMainControlComponents has not been called
    // MODIFIES: this
    // EFFECTS: Adds main control components for exiting etc
    private void addMainControlComponents() {
        this.add(cancelButton = new JButton("Cancel Edit"),
                GBC.hfillNorth(0, 0).insets(2));
        this.add(saveAndExitButton = new JButton("Save and Exit"),
                GBC.hfillNorth(1, 0).insets(2));
    }

    // REQUIRES: addMainControlListeners has not been called
    // MODIFIES: this
    // EFFECTS: Adds listeners for main control components
    private void addMainControlListeners() {
        this.cancelButton.addActionListener(e ->
                new ForcedConfirmDialog(this,
                        "Cancel edit and lose progress?") {
                    @Override
                    protected void onConfirm() {
                        transition(new ClosetView(root, accountManager, closet));
                    }
                }.setTitle("Cancel?"));
        this.saveAndExitButton.addActionListener(e ->
                new ForcedConfirmDialog(this,
                        "Save and exit?") {
                    @Override
                    protected void onConfirm() {
                        closet.removeClothing(baseClothing);
                        closet.addClothing(copyClothing);
                        transition(new ClosetView(root, accountManager, closet));
                    }
                }.setTitle("Exit?"));
    }

}
