package ui.swing.views;

import model.AccountManager;
import model.Closet;
import model.Clothing;
import model.Size;
import ui.swing.utils.ClosetSearchPanel;
import ui.swing.utils.GBC;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

// A GUI view to view and edit closet contents
public class ClosetView extends View {

    private final AccountManager accountManager;
    private final Closet closet;
    private JButton createClothingButton;
    private JButton editClothingButton;
    private JButton deleteClothingButton;
    private ClosetSearchPanel closetSearchPanel;
    private JButton exitButton;

    // EFFECTS: Constructs a new closet view attached to the given root
    //          with the given accountManager, viewing the provided closet.
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

        this.add(closetSearchPanel = new ClosetSearchPanel(closet),
                GBC.at(0, 0).fillBoth().weight(1, 1).gridheight(4));

        this.add(createClothingButton = new JButton("Create Clothing"),
                GBC.hfillNorth(1, 3).insets(2));

        this.add(exitButton = new JButton("Exit"),
                GBC.hfillNorth(1, 0).insets(2));

        this.add(editClothingButton = new JButton("Edit Selected Clothing"),
                GBC.hfillNorth(1, 1).insets(2));

        this.add(deleteClothingButton = new JButton("Delete Selected Clothing"),
                GBC.hfillNorth(1, 2).insets(2));

        refresh();
    }

    @Override
    void addEventListeners() {
        this.createClothingButton.addActionListener(e ->
                this.transition(new ClothingEditView(root,
                        accountManager, closet,
                        new Clothing(new ArrayList<>(),
                                Size.UNKNOWN,
                                "", "",
                                new ArrayList<>(),
                                new ArrayList<>(),
                                false))));
        this.editClothingButton.addActionListener(e ->
                this.transition(new ClothingEditView(root, accountManager, closet,
                        closetSearchPanel.getSelected())));
        this.deleteClothingButton.addActionListener(e -> {
            this.closet.removeClothing(closetSearchPanel.getSelected());
            refresh();
        });
        this.exitButton.addActionListener(e ->
                this.transition(new HomeView(root, this.accountManager)));
        this.closetSearchPanel.addListSelectionListener(l -> {
            deleteClothingButton.setEnabled(closetSearchPanel.getSelected() != null);
            editClothingButton.setEnabled(closetSearchPanel.getSelected() != null);
        });
    }

    // REQUIRES: addComponents has been called
    // MODIFIES: this
    // EFFECTS: Refreshes all components relying on state
    private void refresh() {
        closetSearchPanel.refresh();
        deleteClothingButton.setEnabled(closetSearchPanel.getSelected() != null);
        editClothingButton.setEnabled(closetSearchPanel.getSelected() != null);
    }

}
