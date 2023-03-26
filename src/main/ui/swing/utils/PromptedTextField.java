package ui.swing.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

// A text field with a prompt
public abstract class PromptedTextField extends JTextField {

    // EFFECTS: Creates a new prompted text field.
    public PromptedTextField() {
        this.addEventListeners();
        this.setPromptState();
    }

    // EFFECTS: Returns an anonymous PromptTextField instance with the given
    //          string prompt
    public static PromptedTextField prompt(String prompt) {
        return new PromptedTextField() {
            @Override
            public String getPrompt() {
                return prompt;
            }
        };
    }

    // MODIFIES: this
    // EFFECTS: Sets edit variables
    protected void setEditState() {
        this.setForeground(getEditColor());
    }

    // MODIFIES: this
    // EFFECTS: Sets prompt variables
    protected void setPromptState() {
        this.setText(getPrompt());
        this.setForeground(getPromptColor());
    }

    @Override
    public void setText(String t) {
        if (t.isEmpty()) {
            setPromptState();
        } else {
            super.setText(t);
        }
    }

    // MODIFIES: this
    // EFFECTS: Adds event listeners to dictate prompt
    private void addEventListeners() {
        this.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent fe) {
                if (getText().equals(getPrompt())
                        && PromptedTextField.this.isEnabled()
                        && PromptedTextField.this.isEditable()) {
                    PromptedTextField.super.setText("");
                    setEditState();
                }
            }

            @Override
            public void focusLost(FocusEvent fe) {
                if (getText().isEmpty()) {
                    setPromptState();
                }
            }
        });
    }

    // EFFECTS: Returns the prompt to be used when updating
    public abstract String getPrompt();

    // EFFECTS: Returns the text color to be used when prompting, or
    //          null if it should stay the same.
    protected Color getPromptColor() {
        return UIManager.getDefaults().getColor("TextField.inactiveForeground");
    }

    // EFFECTS: Returns the text color to be used when normal
    //          input is being done.
    protected Color getEditColor() {
        return UIManager.getDefaults().getColor("TextField.foreground");
    }

}
