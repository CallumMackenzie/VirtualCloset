package ui.swing.utils;

import javax.swing.*;
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

    // EFFECTS: Constructs a new ForcedConfirmDialog to freeze the window which the
    //          given component is attached to.
    public ForcedConfirmDialog(Component componentOnWindowToFreeze, String prompt) {
        this(SwingUtilities.getWindowAncestor(componentOnWindowToFreeze),
                prompt);
    }

    @Override
    protected void onComplete() {
        this.toFreeze.setEnabled(true);
        this.toFreeze.requestFocus();
    }

}
