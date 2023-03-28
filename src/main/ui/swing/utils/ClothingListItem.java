package ui.swing.utils;

import model.Clothing;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// A list view item for clothing
public class ClothingListItem extends JPanel {


    private static final ConcurrentMap<Path, Image> cachedImages = new ConcurrentHashMap<>();

    private final Clothing value;
    private final Container revalidate;

    // EFFECTS: Constructs a new clothing list item. Revalidate used to
    //          refresh the GUI state when components are updated.
    public ClothingListItem(Container revalidate,
                            Clothing value,
                            boolean isSelected) {
        this.value = value;
        this.revalidate = revalidate;
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
                AssetLoader.getInstance().getImage(p, img -> {
                    Image scaled = img.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                    SwingUtilities.invokeLater(() -> {
                        cachedImages.put(p, scaled);
                        revalidate.revalidate();
                        revalidate.repaint();
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
