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
                GBC.at(0, 0).hfill().weightx(0.8).insets(2));
        this.add(searchButton = new JButton("Search"),
                GBC.at(1, 0).hfill().weightx(0.2).insets(2));
        this.add(searchExpressionErrorText = new JLabel(),
                GBC.at(0, 1).hfill().gridwidth(2).insets(2));

        this.searchClothingJList = new JList<>();
        this.searchClothingJList.setCellRenderer(ClothingListItem::new);
        this.add(new JScrollPane(searchClothingJList),
                GBC.at(0, 2).fillBoth().gridwidth(2).insets(7));
    }

    // REQUIRES: this.addSearchListeners has not been called
    // MODIFIES: this
    // EFFECTS: Adds listeners to search components
    private void addSearchListeners() {
        this.searchButton.addActionListener(e -> searchClothingWithExpr());
        this.searchExpressionField.addActionListener(e -> searchClothingWithExpr());
    }

    // MODIFIES: this
    // EFFECTS: Searches clothing by given expression and saves it to the
    //          search clothing jList.
    private void searchClothingWithExpr() {
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
            searchExpressionErrorText.setText(e.getMessage());
        }
    }

    // A list view item for clothing
    private static class ClothingListItem extends JPanel {

        private final Clothing value;

        // EFFECTS: Constructs a new account list item
        public ClothingListItem(JList<? extends Clothing> list,
                                Clothing value,
                                int index,
                                boolean isSelected,
                                boolean cellHasFocus) {
            this.value = value;
            this.setLayout(new GridBagLayout());

            if (isSelected) {
                this.setBackground(UIManager.getDefaults()
                        .getColor("List.selectionBackground"));
            }
            this.addColorComponents();
            this.add(new JLabel(value.getTypes().toString()),
                    GBC.at(0, 0).hfill().insets(2));
            this.add(new JLabel(value.getBrand()),
                    GBC.at(1, 0).hfill().insets(2));
        }

        // REQUIRES: this.addColorComponents has not been called
        // MODIFIES: this
        // EFFECTS: Adds components which indicate color
        private void addColorComponents() {
            JPanel colorPanel = new JPanel(new GridBagLayout());
            double fracWeight = (double) 1 / value.getColors().size();
            for (int i = 0; i < value.getColors().size(); ++i) {
                String color = value.getColors().get(i);
                GBC gbc = GBC.at(i, 0)
                        .fillBoth()
                        .weightx(fracWeight);
                Component component;
                try {
                    Color c = (Color) Color.class.getField(color).get(null);
                    component = new JPanel();
                    component.setBackground(c);
                    component.setForeground(c);
                } catch (Exception e) {
                    component = new JLabel("?");
                }
                colorPanel.add(component, gbc);
                this.add(colorPanel, GBC.at(2, 0).fillBoth().insets(4));
            }
        }

    }
}
