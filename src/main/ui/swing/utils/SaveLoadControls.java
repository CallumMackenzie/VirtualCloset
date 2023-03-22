package ui.swing.utils;

import model.AccountManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

// UI Controls for saving and loading data
public abstract class SaveLoadControls extends JPanel {

    private static final String SAVE_PROMPT = "Save";
    private static final String SAVE_DONE_PROMPT = "Saved";
    private static final String SAVE_FAILED_PROMPT = "Save Failed";

    private static final String LOAD_PROMPT = "Load";
    private static final String LOAD_DONE_PROMPT = "Loaded";
    private static final String LOAD_FAILED_PROMPT = "Load Failed";

    private final AccountManager accountManager;
    private JButton saveButton;
    private JButton loadButton;

    // EFFECTS: Constructs new save load controls from the given
    //          account manager.
    public SaveLoadControls(AccountManager accountManager) {
        super(new GridBagLayout());
        this.accountManager = accountManager;
        this.addComponents();
        this.addEventListeners();
    }

    // REQUIRES: initEventListeners has not been called
    // MODIFIES: this
    // EFFECTS: Initializes GUI components
    private void addComponents() {
        this.add(saveButton = new JButton(SAVE_PROMPT),
                GBC.at(0, 0).hfill().weightx(0.5).insets(2));
        this.add(loadButton = new JButton(LOAD_PROMPT),
                GBC.at(1, 0).hfill().weightx(0.5).insets(2));
    }

    // REQUIRES: addEventListeners has not been called
    // MODIFIES: this
    // EFFECTS: Initializes event listeners
    private void addEventListeners() {
        this.saveButton.addActionListener(e -> {
            ConfirmDialog c = new ConfirmDialog("Overwrite saved data with disk data?",
                    () -> {
                        try {
                            this.accountManager.saveState();
                            this.saveButton.setText(SAVE_DONE_PROMPT);
                            this.onSave();
                        } catch (IOException ex) {
                            this.saveButton.setText(SAVE_FAILED_PROMPT);
                        }
                    }, () -> this.saveButton.setText(SAVE_PROMPT));
            c.setTitle("Save Data?");
        });
        this.loadButton.addActionListener(e -> {
            ConfirmDialog c = new ConfirmDialog("Overwrite current data with disk data?",
                    () -> {
                        try {
                            this.accountManager.loadState();
                            this.loadButton.setText(LOAD_DONE_PROMPT);
                            this.onLoad();
                        } catch (IOException ex) {
                            this.loadButton.setText(LOAD_FAILED_PROMPT);
                        }
                    }, () -> this.loadButton.setText(LOAD_PROMPT));
            c.setTitle("Load Data?");
        });
    }

    // EFFECTS: Invoked when data is saved.
    protected abstract void onSave();

    // EFFECTS: Invoked when data is loaded.
    protected abstract void onLoad();

}
