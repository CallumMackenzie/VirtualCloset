package ui;

import model.Catalogue;
import model.Outfit;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// A console interface for a catalogue mode
public class CatalogueModeConsole extends CommandSystem {

    private final Catalogue catalogue;

    // EFFECTS: Creates a new catalogue console interface for the given
    //          catalogue and input system.
    public CatalogueModeConsole(DynamicScanner dynamicScanner,
                                Catalogue catalogue) {
        super(dynamicScanner);
        this.catalogue = catalogue;
        this.run();
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
        this.setShouldRun(true);
        this.initCommands();
    }

    // REQUIRES: this.initCommands has not been called yet
    // MODIFIES: this
    // EFFECTS: Initializes catalogue commands
    private void initCommands() {
        this.initBasicCommands();
        this.initOutfitCommands();
    }

    // REQUIRES: this.initBasicCommands has not been called
    // MODIFIES: this
    // EFFECTS: Initializes basic commands
    private void initBasicCommands() {
        this.addCommands(
                new ConsoleCommand(this::help,
                        "Prints a list of commands.",
                        "help"),
                new ConsoleCommand(this::stop,
                        "Exits catalogue mode.",
                        "exit")
        );
    }

    // REQUIRES: this.initOutfitCommands has not been called
    // MODIFIES: this
    // EFFECT: Initializes outfit commands
    private void initOutfitCommands() {
        this.addCommands(
                new ConsoleCommand(this::createOutfit,
                        "Creates a new outfit and enters edit mode for it.",
                        "new outfit", "new"),
                new ConsoleCommand(this::listOutfitNames,
                        () -> !this.catalogue.getOutfits().isEmpty(),
                        "No outfits in this closet!",
                        "Lists names of all outfits in this catalogue.",
                        "list names", "names")
        );
    }

    // EFFECTS: Creates a new outfit and enters editing mode for it
    // MODIFIES: this
    private void createOutfit() {
        String name = this.getInput("\tEnter name for outfit: ");
        Outfit o = new Outfit(name, new ArrayList<>());
        this.modifyOutfit(o);
        this.catalogue.addOutfit(o);
        System.out.println("\tOutfit added to catalogue.");
    }

    // EFFECTS: Lists outfit names in closet
    private void listOutfitNames() {
        List<Outfit> outfits = this.catalogue.getOutfits();
        System.out.println("\t- " +
                outfits.stream().map(Outfit::getName)
                .collect(Collectors.joining("\n\t- ")));
    }

    // EFFECTS: Prints an info message for the user
    @Override
    protected void help() {
        super.help();
    }

    // EFFECTS: Enters outfit edit mode
    // MODIFIES: this
    private void modifyOutfit(Outfit o) {
        System.out.println("\tEntering outfit creation mode.");
        new OutfitCreationConsole(this.getInput(), o);
    }
}
