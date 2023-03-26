package ui.swing.utils;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Supplier;

// A class representing a collection of predefined components for an
// editable string list.
public class StringListEditable extends JPanel {

    private final Supplier<List<String>> getter;
    private final String paramName;

    private JList<String> valueList;

    private PromptedTextField newValueTextField;
    private JButton removeSelectedButton;
    private JButton addNewValButton;

    // EFFECTS: Constructs a new string list editable with the given getter
    //          and parameter name.
    public StringListEditable(String paramName,
                              Supplier<List<String>> getter) {
        this.getter = getter;
        this.paramName = paramName;

        this.addComponents();
        this.addListeners();

        this.refreshListValues();
        this.refreshComponentStates();
    }

    // REQUIRES: addComponents has been called
    // MODIFIES: this
    // EFFECTS: Refreshes component states
    private void refreshComponentStates() {
        addNewValButton.setEnabled(!newValueTextField.getText()
                .equals(newValueTextField.getPrompt())
                && !newValueTextField.getText().isEmpty());
        removeSelectedButton.setEnabled(valueList.getSelectedValue() != null);
    }

    // REQUIRES: this.valueList is not null
    // MODIFIES: this
    // EFFECTS: Refreshes list values from getter
    private void refreshListValues() {
        valueList.setListData(this.getter.get().toArray(new String[0]));
    }

    // REQUIRES: addComponents has not been called
    // MODIFIES: this
    // EFFECTS: Adds components to this
    private void addComponents() {
        this.setLayout(new GridBagLayout());

        this.add(new JLabel(paramName), GBC.hfillNorth(0, 0).insets(2));

        this.add(removeSelectedButton = new JButton("Remove Selected " + paramName),
                GBC.hfillNorth(1, 0).insets(2));

        valueList = new JList<>();
        this.add(new JScrollPane(valueList), GBC.at(0, 1)
                .fillBoth()
                .gridwidth(2)
                .weighty(1)
                .insets(3));

        this.add(newValueTextField = PromptedTextField.prompt(paramName),
                GBC.hfillNorth(0, 2).insets(2).weightx(0.8));

        this.add(addNewValButton = new JButton("Add " + paramName),
                GBC.hfillNorth(1, 2).insets(2));
    }

    // REQUIRES: addListeners has not been called
    // MODIFIES: this
    // EFFECTS: Adds listeners to the components attached to this
    private void addListeners() {
        valueList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                this.refreshComponentStates();
            }
        });
        removeSelectedButton.addActionListener(e -> {
            this.getter.get().remove(valueList.getSelectedValue());
            this.refreshListValues();
            this.refreshComponentStates();
        });
        addNewValButton.addActionListener(e -> {
            this.getter.get().add(newValueTextField.getText());
            this.refreshListValues();
            newValueTextField.setText("");
            this.refreshComponentStates();
        });
        newValueTextField.addCaretListener(c -> refreshComponentStates());
    }
}
