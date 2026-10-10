package ui.swing.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// A class to confirm saving/loading
public abstract class ConfirmDialog extends JFrame {

    private final String prompt;
    private boolean cancelRan;
    private boolean confirmRan;

    private JButton confirmButton;
    private JButton cancelButton;

    // EFFECTS: Constructs a new confirm dialog with the given
    //          action and prompts.
    public ConfirmDialog(String prompt) {
        this.prompt = prompt;
        this.cancelRan = false;
        this.confirmRan = false;

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.addWindowListeners();
        this.addComponents();
        this.addEventListeners();
        this.pack();
        this.setResizable(false);
        this.setVisible(true);
    }

    // REQUIRES: addWindowListeners has not been called already
    // MODIFIES: this
    // EFFECTS: Adds window listeners
    private void addWindowListeners() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (!confirmRan && !cancelRan) {
                    registerCancel();
                }
                onComplete();
                dispose();
            }
        });
    }

    // REQUIRES: addComponents has not been called already
    // MODIFIES: this
    // EFFECTS: Adds window components
    private void addComponents() {
        Container c = this.getContentPane();
        this.setLayout(new GridBagLayout());

        c.add(new JLabel(prompt),
                GBC.at(0, 0).hfill().insets(2)
                        .gridwidth(2));
        c.add(cancelButton = new JButton("Cancel"),
                GBC.at(0, 1).hfill().insets(3));
        c.add(confirmButton = new JButton("Confirm"),
                GBC.at(1, 1).hfill().insets(3));
    }

    // REQUIRES: addEventListeners has not been called already
    // MODIFIES: this
    // EFFECTS: Adds component event listeners
    private void addEventListeners() {
        this.confirmButton.addActionListener(e -> {
            this.registerConfirm();
            this.dispose();
        });
        this.cancelButton.addActionListener(e -> {
            this.registerCancel();
            this.dispose();
        });
    }


    // MODIFIES: this
    // EFFECTS: Runs onCancel if it has not already
    private void registerCancel() {
        if (!cancelRan) {
            this.onCancel();
        }
        this.cancelRan = true;
    }

    // MODIFIES: this
    // EFFECTS: Runs onConfirm if it has not already
    private void registerConfirm() {
        if (!confirmRan) {
            this.onConfirm();
        }
        this.confirmRan = true;
    }

    // EFFECTS: Run exactly once when dialog confirmed
    protected void onConfirm() {
    }

    // EFFECTS: Run exactly once when dialog cancelled
    protected void onCancel() {
    }

    // EFFECTS: Run exactly once when dialog closed
    protected void onComplete() {
    }

}
