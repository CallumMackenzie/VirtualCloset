package ui.swing.views;

import model.AccountManager;
import model.Closet;
import model.Clothing;
import model.Size;
import model.search.ClothingAddress;
import model.search.ClothingAddressParseException;
import ui.swing.utils.AssetLoader;
import ui.swing.utils.GBC;
import ui.swing.utils.PromptedTextField;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// A GUI view to view and edit closet contents
public class ClosetView extends View {

    private final AccountManager accountManager;
    private final Closet closet;

    private PromptedTextField searchExpressionField;
    private JTextArea searchExpressionErrorText;
    private JButton searchButton;
    private JList<Clothing> searchClothingJList;

    private JButton createClothingButton;
    private JButton editClothingButton;
    private JButton deleteClothingButton;

    private JTextArea clothingInfoTextArea;

    private JButton exitButton;

    private ConcurrentMap<Path, Image> cachedImages;

    // EFFECTS: Constructs a new closet view attached to the given root
    //          with the given accountManager, viewing the provided closet.
    public ClosetView(Container root,
                      AccountManager accountManager,
                      Closet closet) {
        super(root);
        this.accountManager = accountManager;
        this.closet = closet;
        this.cachedImages = new ConcurrentHashMap<>();
    }

    @Override
    void addComponents() {
        this.setLayout(new GridBagLayout());

        this.addSearchComponents();
        this.addEditComponents();

        this.setSelectedClothing(null);
    }

    @Override
    void addEventListeners() {
        this.addSearchListeners();
        this.addEditListeners();
    }

    // REQUIRES: this.addSearchComponents has not been called
    // MODIFIES: this
    // EFFECTS: Adds search components to gui
    private void addSearchComponents() {
        this.add(searchExpressionField = PromptedTextField.prompt("Search Expression"),
                GBC.hfillNorth(0, 0).weightx(0.8).insets(2));
        this.add(searchButton = new JButton("Search"),
                GBC.hfillNorth(1, 0).weightx(0.2).insets(2));
        this.add(new JScrollPane(searchExpressionErrorText = new JTextArea()),
                GBC.hfillNorth(0, 1).gridwidth(3).insets(2)
                        .weighty(0.05));
        searchExpressionErrorText.setEditable(false);
        searchExpressionErrorText.setLineWrap(true);
        searchExpressionErrorText.setWrapStyleWord(true);

        this.searchClothingJList = new JList<>();
        this.searchClothingJList.setCellRenderer(ClothingListItem::new);
        this.add(new JScrollPane(searchClothingJList),
                GBC.at(0, 2)
                        .fillBoth()
                        .north()
                        .weighty(1)
                        .gridwidth(2)
                        .insets(4));
    }

    // REQUIRES: this.addSearchListeners has not been called
    // MODIFIES: this
    // EFFECTS: Adds listeners to search components
    private void addSearchListeners() {
        this.searchButton.addActionListener(e -> searchClothingWithExpr());
        this.searchExpressionField.addActionListener(e -> searchClothingWithExpr());
        this.searchClothingJList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                this.setSelectedClothing(searchClothingJList.getSelectedValue());
            }
        });
    }

    // REQUIRES: this.addEditComponents has not been called
    // MODIFIES: this
    // EFFECTS: Adds edit components to gui
    private void addEditComponents() {
        this.add(createClothingButton = new JButton("Create Clothing"),
                GBC.hfillNorth(0, 3).insets(2));

        this.add(exitButton = new JButton("Exit"),
                GBC.hfillNorth(2, 0).insets(2));

        this.add(editClothingButton = new JButton("Edit Selected Clothing"),
                GBC.hfillNorth(2, 3).insets(2));

        this.add(deleteClothingButton = new JButton("Delete Selected Clothing"),
                GBC.hfillNorth(1, 3).insets(2));

        this.clothingInfoTextArea = new JTextArea();
        this.clothingInfoTextArea.setEditable(false);
        this.clothingInfoTextArea.setLineWrap(true);
        this.clothingInfoTextArea.setWrapStyleWord(true);
        this.add(new JScrollPane(this.clothingInfoTextArea),
                GBC.at(2, 2).fillBoth()
                        .north()
                        .insets(2));
    }

    // REQUIRES: this.addEditListeners has not been called
    // MODIFIES: this
    // EFFECTS: Adds edit component listeners
    private void addEditListeners() {
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
                        searchClothingJList.getSelectedValue())));
        this.deleteClothingButton.addActionListener(e -> {
            this.closet.removeClothing(searchClothingJList.getSelectedValue());
            this.setSelectedClothing(null);
            this.searchClothingWithExpr();
        });
        this.exitButton.addActionListener(e ->
                this.transition(new HomeView(root, this.accountManager)));
    }

    // MODIFIES: this
    // EFFECTS: Searches clothing by given expression and saves it to the
    //          search clothing jList.
    private void searchClothingWithExpr() {
        if (searchExpressionField.hasTextValue()) {
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
        } else {
            this.searchExpressionErrorText.setText("No expression entered.");
        }
    }

    // MODIFIES: this
    // EFFECTS: Updates selected clothing views
    private void setSelectedClothing(Clothing c) {
        this.editClothingButton.setEnabled(c != null);
        this.deleteClothingButton.setEnabled(c != null);
        this.clothingInfoTextArea.setText(c == null
                ? "NA" : c.toString().replaceAll("\t+", ""));
    }

    // A list view item for clothing
    private class ClothingListItem extends JPanel {

        private final Clothing value;

        // EFFECTS: Constructs a new clothing list item
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
            this.addTypeComponents();
            this.add(new JLabel(value.getBrand()),
                    GBC.at(1, 0).hfill().insets(2));
        }

        // REQUIRES: this.addTypeComponents has not been called
        // MODIFIES: this
        // EFFECTS: Adds components which indicate type
        private void addTypeComponents() {
            JPanel typePanel = new JPanel(new GridBagLayout());
            double weight = (double) 1 / value.getTypes().size();
            for (int i = 0; i < value.getTypes().size(); ++i) {
                String type = value.getTypes().get(i);
                JLabel typeLabel = new JLabel("?");
                Path p = Paths.get("./data/img/" + type + ".png");
                if (cachedImages.containsKey(p)) {
                    typeLabel.setIcon(new ImageIcon(cachedImages.get(p)));
                    typeLabel.setText("");
                } else {
                    AssetLoader.getInstance().getImage(p,
                            img -> {
                                Image scaled = img.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                                SwingUtilities.invokeLater(() -> {
                                    cachedImages.put(p, scaled);
                                    ClosetView.this.revalidate();
                                    ClosetView.this.repaint();
                                });
                            });
                }
                typePanel.add(typeLabel, GBC.at(i, 0).weightx(weight)
                        .anchor(GBC.Anchor.West));
            }
            this.add(typePanel, GBC.at(0, 0).hfill());
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
