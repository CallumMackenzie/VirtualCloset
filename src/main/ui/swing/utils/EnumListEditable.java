package ui.swing.utils;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

// TODO
public class EnumListEditable<T extends Enum<T>> extends JPanel {

    private final Class<? extends T> enumClass;
    private final String paramName;
    private JComboBox<T> enumComboBox;
    private final Supplier<T> getter;
    private final Consumer<T> setter;

    // TODO
    public EnumListEditable(Class<? extends T> enumClass,
                            String paramName,
                            Consumer<T> setter,
                            Supplier<T> getter) {
        this.enumClass = enumClass;
        this.paramName = paramName;
        this.getter = getter;
        this.setter = setter;
        this.addComponents();
        this.addListeners();
        this.refreshComponentStates();
    }

    // REQUIRES: addComponents has not been called
    // MODIFIES: this
    // EFFECTS: Adds components to this
    private void addComponents() {
        this.setLayout(new GridBagLayout());

        this.add(new JLabel(paramName), GBC.at(0, 0).insets(3)
                .hfill().anchor(GBC.Anchor.Center));

        enumComboBox = new JComboBox<>(enumClass.getEnumConstants());
        this.add(enumComboBox, GBC.hfillNorth(1, 0).insets(3)
                .hfill().anchor(GBC.Anchor.Center));
    }

    // REQUIRES: addComponents has not been called
    // MODIFIES: this
    // EFFECTS: Adds components to this
    private void addListeners() {
        this.enumComboBox.addActionListener(l -> {
            Object selected = enumComboBox.getSelectedItem();
            if (selected != null
                    && selected.getClass().equals(this.enumClass)) {
                this.setter.accept((T) selected);
            }
        });
    }


    // REQUIRES: addComponents has been called
    // MODIFIES: this
    // EFFECTS: Refreshes component states
    private void refreshComponentStates() {
        this.enumComboBox.setSelectedItem(getter.get());
    }

}
