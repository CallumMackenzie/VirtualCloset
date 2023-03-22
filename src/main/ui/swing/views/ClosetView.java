package ui.swing.views;

import model.AccountManager;
import model.Closet;
import model.Clothing;
import model.search.ClothingAddress;
import model.search.ClothingAddressParseException;
import ui.swing.utils.GBC;
import ui.swing.utils.PromptedTextField;

import javax.swing.*;
import java.awt.*;

// TODO
public class ClosetView extends View {

    private final AccountManager accountManager;
    private final Closet closet;

    private JTextField searchExpressionField;
    private JLabel searchExpressionErrorText;
    private JButton searchButton;
    private JList<Clothing> searchClothingJList;

    // TODO
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

        this.addSearchComponents();
    }

    @Override
    void addEventListeners() {
        this.addSearchListeners();
    }

    // REQUIRES: this.addSearchComponents has not been called
    // MODIFIES: this
    // EFFECTS: Adds search components to gui
    private void addSearchComponents() {
        this.add(searchExpressionField = PromptedTextField.prompt("Search Expression"),
                GBC.at(0, 0).hfill());
        this.add(searchButton = new JButton("Search"),
                GBC.at(1, 0).hfill());
        this.add(searchExpressionErrorText = new JLabel(),
                GBC.at(0, 1).hfill().gridwidth(2));

        this.searchClothingJList = new JList<>();
        this.searchClothingJList.setCellRenderer(ClothingListItem::new);
        this.add(new JScrollPane(searchClothingJList),
                GBC.at(0, 2).fillBoth().gridwidth(2));
    }

    // REQUIRES: this.addSearchListeners has not been called
    // MODIFIES: this
    // EFFECTS: Adds listeners to search components
    private void addSearchListeners() {
        this.searchButton.addActionListener(e -> searchClothingWithExpr());
    }

    // MODIFIES: this
    // EFFECTS: Searches clothing by given expression and saves it to the
    //          search clothing jList.
    private void searchClothingWithExpr() {
        try {
            java.util.List<Clothing> found = this.closet.findClothing(ClothingAddress.of(
                    this.searchExpressionField.getText()
            ));
            this.searchClothingJList.setListData(found.toArray(new Clothing[0]));
            searchExpressionErrorText.setText("Search expression ok.");
        } catch (ClothingAddressParseException e) {
            searchExpressionErrorText.setText(e.getMessage());
        }
    }

    // A list view item for clothing
    private static class ClothingListItem extends JPanel {

        // EFFECTS: Constructs a new account list item
        public ClothingListItem(JList<? extends Clothing> list,
                                Clothing value,
                                int index,
                                boolean isSelected,
                                boolean cellHasFocus) {
            if (isSelected) {
                this.setBackground(UIManager
                        .getDefaults().getColor("List.selectionBackground"));
            }
            this.add(new JLabel(value.toString()));
        }

    }
}
