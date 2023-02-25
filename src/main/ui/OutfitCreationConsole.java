package ui;

import model.Clothing;
import model.Outfit;

import java.util.List;

// An interface to create outfits
public class OutfitCreationConsole extends CommandSystem {

    private final Outfit outfit;

    // EFFECTS: Creates a new outfit creation console from the given outfit
    //          and dynamic scanner.
    public OutfitCreationConsole(DynamicScanner ds, Outfit o) {
        super(ds);
        this.outfit = o;
        this.run();
    }

    // EFFECTS: Prompts user for input and returns it
    @Override
    protected String promptInput() {
        return getInput("Enter outfit creation command (ex \"help\"): ");
    }

    // MODIFIES: this
    // EFFECTS: Initializes the outfit creation interface
    @Override
    protected void init() {
        this.setShouldRun(true);
        this.initCommands();
    }

    // REQUIRES: this.initCommands has not been called
    // MODIFIES: this
    // EFFECTS: Initializes commands
    private void initCommands() {
        this.addCommands(
                new ConsoleCommand(this::setOutfitName,
                        "Sets the name of this outfit.",
                        "set name", "name"),
                new ConsoleCommand(this::stop,
                        "Exits outfit edit mode.",
                        "exit"),
                new ConsoleCommand(this::listOutfitClothing,
                        "Lists the clothing in this outfit.",
                        "list", "clothing")
                // TODO: Add clothing command
                // TODO: Remove clothing command
        );
    }

    // MODIFIES: this
    // EFFECTS: Sets the outfit name to the user input value
    private void setOutfitName() {
        String newName = this.getInput("\tEnter new outfit name: ");
        this.outfit.setName(newName);
        System.out.println("\tSet outfit name to \"" + newName + "\".");
    }

    // MODIFIES: this
    // EFFECTS: Lists the clothing in this outfit
    private void listOutfitClothing() {
        List<Clothing> clothing = this.outfit.getClothing();
        if (clothing.isEmpty()) {
            System.out.println("\tNo clothing in this outfit.");
        } else {
            System.out.println(clothing);
        }
    }
}
