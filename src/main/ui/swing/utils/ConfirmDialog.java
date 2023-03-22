package ui.swing.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// A class to confirm saving/loading
public class ConfirmDialog extends JFrame {

    private final String prompt;
    private final Runnable onConfirm;
    private final Runnable onCancel;
    private boolean cancelRan;
    private boolean confirmRan;

    private JButton confirmButton;
    private JButton cancelButton;

    // EFFECTS: Constructs a new confirm dialog with the given
    //          action and prompts.
    public ConfirmDialog(String prompt,
                         Runnable onConfirm,
                         Runnable onCancel) {
        this.prompt = prompt;
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;
        this.cancelRan = false;
        this.confirmRan = false;

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.addWindowListeners();
        this.addComponents();
        this.addEventListeners();
        this.pack();
        this.setVisible(true);
    }

    // REQUIRES: addWindowListeners has not been called already
    // MODIFIES: this
    // EFFECTS: Adds window listeners
    private void addWindowListeners() {
        this.addWindowStateListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if (!confirmRan && !cancelRan) {
                    registerCancel();
                }
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
            this.onCancel.run();
        }
        this.cancelRan = true;
    }

    // MODIFIES: this
    // EFFECTS: Runs onConfirm if it has not already
    private void registerConfirm() {
        if (!confirmRan) {
            this.onConfirm.run();
        }
        this.confirmRan = true;
    }

}
