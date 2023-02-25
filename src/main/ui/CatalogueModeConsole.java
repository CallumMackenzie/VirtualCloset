package ui;

import model.Catalogue;

// A console interface for a catalogue mode
public class CatalogueModeConsole extends CommandSystem {

    private final Catalogue catalogue;

    // EFFECTS: Creates a new catalogue console interface for the given
    //          catalogue and input system.
    public CatalogueModeConsole(DynamicScanner dynamicScanner,
                                Catalogue catalogue) {
        super(dynamicScanner);
        this.catalogue = catalogue;
    }

    // EFFECTS: Prompts for input and returns it
    @Override
    protected String promptInput() {
        return this.getInput("Enter a catalogue command (ex. \"help\"): ");
    }

    // MODIFIES: this
    // EFFECTS: Initializes the console interface
    @Override
    protected void init() {
        // TODO
    }
}
