package ui.swing.utils;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

// A component to edit a string value through the given getters and setters.
public class StringEditable extends JPanel {

    private final String paramName;
    private final Consumer<String> setter;
    private final Supplier<String> getter;

    private JTextField valueTextField;

    // EFFECTS: Constructs a new string editable panel with the given getter,
    //          setter and param name.
    public StringEditable(String paramName,
                          Consumer<String> setter,
                          Supplier<String> getter) {
        this.paramName = paramName;
        this.setter = setter;
        this.getter = getter;

        this.addComponents();
        this.addListeners();

        this.refreshComponentStates();
    }

    // REQUIRES: addComponents has not been called
    // MODIFIES: this
    // EFFECTS: Adds components to this
    private void addComponents() {
        this.setLayout(new GridBagLayout());

        this.add(new JLabel(paramName),
                GBC.hfillNorth(0, 0)
                        .insets(2)
                        .weightx(0.3));

        valueTextField = PromptedTextField.prompt(paramName);
        this.add(valueTextField,
                GBC.hfillNorth(1, 0)
                        .insets(2)
                        .weightx(0.7));
    }

    // REQUIRES: addComponents has not been called
    // MODIFIES: this
    // EFFECTS: Adds components to this
    private void addListeners() {
        valueTextField.addCaretListener(l ->
                this.setter.accept(valueTextField.getText()));
    }

    // REQUIRES: addComponents has been called
    // MODIFIES: this
    // EFFECTS: Refreshes component states
    private void refreshComponentStates() {
        valueTextField.setText(this.getter.get());
    }

}
