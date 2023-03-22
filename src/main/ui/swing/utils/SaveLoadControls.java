package ui.swing.utils;

import model.AccountManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

// UI Controls for saving and loading data
public class SaveLoadControls extends JPanel {

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
        this.addSaveListeners();
        this.addLoadListeners();
    }

    // REQUIRES: addSaveListeners has not been called
    // MODIFIES: this
    // EFFECTS: Initializes save event listeners
    private void addSaveListeners() {
        this.saveButton.addActionListener(e ->
                new SaveConfirmDialog(SwingUtilities.getWindowAncestor(this)));
    }

    // REQUIRES: addLoadListeners has not been called
    // MODIFIES: this
    // EFFECTS: Initializes save event listeners
    private void addLoadListeners() {
        this.loadButton.addActionListener(e ->
                new LoadConfirmDialog(SwingUtilities.getWindowAncestor(this)));
    }

    // EFFECTS: Invoked when data is saved.
    protected void onSave() {
    }

    // EFFECTS: Invoked when data is loaded.
    protected void onLoad() {
    }

    // A save confirm dialog class
    private class SaveConfirmDialog extends ForcedConfirmDialog {

        // EFFECTS: Constructs a new save confirm dialog with the given root window
        public SaveConfirmDialog(Window w) {
            super(w, "Overwrite saved data with disk data?");
            this.setTitle("Save Data?");
        }

        @Override
        protected void onCancel() {
            saveButton.setText(SAVE_PROMPT);
        }

        @Override
        protected void onConfirm() {
            try {
                accountManager.saveState();
                saveButton.setText(SAVE_DONE_PROMPT);
                onSave();
            } catch (IOException ex) {
                saveButton.setText(SAVE_FAILED_PROMPT);
            }
        }
    }

    // A load confirm dialog class
    private class LoadConfirmDialog extends ForcedConfirmDialog {

        // EFFECTS: Constructs a new load confirm dialog with the given root window
        public LoadConfirmDialog(Window w) {
            super(w, "Overwrite current data with disk data?");
            this.setTitle("Load Data?");
        }

        @Override
        protected void onConfirm() {
            try {
                accountManager.loadState();
                loadButton.setText(LOAD_DONE_PROMPT);
                onLoad();
            } catch (IOException ex) {
                loadButton.setText(LOAD_FAILED_PROMPT);
            }
        }

        @Override
        protected void onCancel() {
            loadButton.setText(LOAD_PROMPT);
        }
    }

}
