package ui.swing.utils;

import java.awt.*;

// A confirm dialog which freezes the given other window
public abstract class ForcedConfirmDialog extends ConfirmDialog {

    private final Window toFreeze;

    // EFFECTS: Constructs a new ForcedConfirmDialog to freeze the given window
    public ForcedConfirmDialog(Window toFreeze, String prompt) {
        super(prompt);
        this.toFreeze = toFreeze;
        this.toFreeze.setEnabled(false);
    }

    @Override
    protected void onComplete() {
        this.toFreeze.setEnabled(true);
        this.toFreeze.requestFocus();
    }

}
