package ui.swing.utils;

import model.Closet;
import model.Clothing;
import model.search.ClothingAddress;
import model.search.ClothingAddressParseException;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

// A panel to search a given closet
public class ClosetSearchPanel extends JPanel {

    private Closet closet;
    private PromptedTextField searchExpressionField;
    private JTextArea searchExpressionErrorText;
    private JButton searchButton;
    private JList<Clothing> searchClothingJList;
    private JTextArea clothingInfoTextArea;

    // EFFECTS: Constructs a new closet search panel for the given
    //          closet.
    public ClosetSearchPanel(Closet closet) {
        this.closet = closet;

        this.addComponents();
        this.addListeners();
    }

    // REQUIRES: addComponents has not been called
    // MODIFIES: this
    // EFFECTS: Adds components to the view
    private void addComponents() {
        this.setLayout(new GridBagLayout());

        this.add(searchExpressionField = PromptedTextField.prompt("Search Expression"),
                GBC.hfillNorth(0, 0).weightx(0.8).insets(2).gridwidth(2));
        this.add(searchButton = new JButton("Search"),
                GBC.hfillNorth(2, 0).weightx(0.2).insets(2));
        this.add(new JScrollPane(searchExpressionErrorText = new JTextArea()),
                GBC.hfillNorth(0, 1).gridwidth(2).insets(2).weighty(0.1).fillBoth());
        searchExpressionErrorText.setEditable(false);
        searchExpressionErrorText.setLineWrap(true);
        searchExpressionErrorText.setWrapStyleWord(true);

        this.searchClothingJList = new JList<>();
        this.searchClothingJList.setCellRenderer(
                (list, val, idx, sel, foc) -> new ClothingListItem(this, val, sel));
        this.add(new JScrollPane(searchClothingJList),
                GBC.at(0, 2).fillBoth().north()
                        .weighty(1).gridwidth(2).insets(4));

        this.clothingInfoTextArea = new JTextArea();
        this.clothingInfoTextArea.setEditable(false);
        this.clothingInfoTextArea.setLineWrap(true);
        this.clothingInfoTextArea.setWrapStyleWord(true);
        this.add(new JScrollPane(this.clothingInfoTextArea),
                GBC.at(2, 1).fillBoth().north().insets(2).gridheight(2));

        this.refresh();
    }

    // REQUIRES: addListeners has not been called
    // MODIFIES: this
    // EFFECTS: Adds listeners to view components
    private void addListeners() {
        this.searchButton.addActionListener(e -> refreshSearch());
        this.searchExpressionField.addActionListener(e -> refreshSearch());
        this.searchClothingJList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                this.refreshSelectedClothing();
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: Searches clothing by given expression and saves it to the
    //          search clothing jList.
    private void refreshSearch() {
        if (searchExpressionField.hasTextValue() && closet != null) {
            try {
                java.util.List<Clothing> found = this.closet.findClothing(ClothingAddress.of(
                        this.searchExpressionField.getText()
                ));
                Clothing[] clothingArr = new Clothing[found.size()];
                for (int i = found.size() - 1; i >= 0; --i) {
                    clothingArr[i] = found.get(found.size() - i - 1);
                }
                this.searchClothingJList.setListData(clothingArr);
                searchExpressionErrorText.setText("Search expression ok.");
            } catch (ClothingAddressParseException e) {
                searchExpressionErrorText.setText("Error in expression: " + e.getMessage()
                        + " At \"" + e.getErrorState().getStateCaptured() + "\".");
            }
        } else if (closet == null) {
            this.searchExpressionErrorText.setText("No closet set.");
        } else {
            this.searchExpressionErrorText.setText("No expression entered.");
        }
    }

    // MODIFIES: this
    // EFFECTS: Updates selected clothing views
    public void refreshSelectedClothing() {
        displayClothingInfo(searchClothingJList.getSelectedValue());
    }

    // MODIFIES: this
    // EFFECTS: Refreshes all components
    public void refresh() {
        this.refreshSearch();
        this.refreshSelectedClothing();
    }

    // EFFECTS: Returns the current selected clothing, or null if there is none
    public Clothing getSelected() {
        return this.searchClothingJList.getSelectedValue();
    }

    // MODIFIES: this
    // EFFECTS: Sets clothing info pane to display info for the provided clothing
    public void displayClothingInfo(Clothing c) {
        this.clothingInfoTextArea.setText(c == null
                ? "NA" : c.toString().replaceAll("\t+", ""));
    }

    // MODIFIES: this
    // EFFECTS: Adds a list selection listener to the search panel
    public void addListSelectionListener(ListSelectionListener l) {
        this.searchClothingJList.addListSelectionListener(l);
    }

    // MODIFIES: this
    // EFFECTS: Sets components as enabled or not
    public void setEnabled(boolean enabled) {
        this.refresh();
        this.searchExpressionField.setEnabled(enabled);
        this.searchButton.setEnabled(enabled);
        this.searchClothingJList.setEnabled(enabled);
    }

    // MODIFIES: this
    // EFFECTS: Sets the closet used to the given
    public void setCloset(Closet closet) {
        this.closet = closet;
        this.setEnabled(closet != null);
        this.refresh();
    }

}
