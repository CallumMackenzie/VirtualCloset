package ui;

import model.Account;
import model.Catalogue;
import model.Outfit;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// A console interface for a catalogue mode
public class CatalogueModeConsole extends CommandSystem {

    private final Account account;
    private final Catalogue catalogue;

    // EFFECTS: Creates a new catalogue console interface for the given
    //          catalogue and input system.
    public CatalogueModeConsole(DynamicScanner dynamicScanner,
                                Account account) {
        super(dynamicScanner);
        this.catalogue = account.getCatalogue();
        this.account = account;
        this.run();
    }

    // EFFECTS: Prompts for input and returns it
    @Override
    protected String promptInput() {
        return this.getInput("Enter a catalogue command (ex. \"help\"): ");
    }

    // REQUIRES: this.initCommands has not been called yet
    // MODIFIES: this
    // EFFECTS: Initializes catalogue commands
    @Override
    protected void initCommands() {
        this.initBasicCommands("Lists the catalogue commands.",
                "Exits catalogue mode.",
                "exit");
        this.initOutfitCommands();
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
                        "No outfits in this catalogue!",
                        "Lists names of all outfits in this catalogue.",
                        "list names", "names"),
                new ConsoleCommand(this::modifyNamedOutfit,
                        () -> !this.catalogue.getOutfits().isEmpty(),
                        "No outfits in this catalogue!",
                        "Enters edit mode for the named outfit.",
                        "modify named"),
                new ConsoleCommand(this::removeByName,
                        () -> !this.catalogue.getOutfits().isEmpty(),
                        "No outfits in this catalogue!",
                        "Removes all outfits with the given name (case insensitive)",
                        "remove by name", "remove named")
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
        System.out.println("\t- "
                + outfits.stream().map(Outfit::getName)
                .collect(Collectors.joining("\n\t- ")));
    }

    // MODIFIES: this
    // EFFECTS: Gets an outfit name and enters modification mode for it
    private void modifyNamedOutfit() {
        String name = this.getInput("\tEnter outfit name to modify: ");
        List<Outfit> matched = this.catalogue.getOutfitsByName(name);
        if (matched.isEmpty()) {
            System.out.println("\tNo outfits match the name \"" + name + "\".");
        } else if (matched.size() > 1) {
            System.out.println("\tMore than one outfit is named \"" + name + "\". "
                    + "Select the index of which one to modify: \n"
                    + matched.stream().map(Outfit::toString)
                    .collect(Collectors.joining("\n")));
        } else {
            new OutfitCreationConsole(this.getInput(),
                    this.account, matched.get(0));
        }
    }

    // MODIFIES: this
    // EFFECTS: Removes all clothing with the given name
    private void removeByName() {
        String name = this.getInput("\tEnter name of clothing to remove: ");
        this.catalogue.removeAllWithName(name);
        System.out.println("\tRemoved all with name \"" + name + "\".");
    }

    // MODIFIES: this
    // EFFECTS: Enters outfit edit mode
    private void modifyOutfit(Outfit o) {
        System.out.println("\tEntering outfit creation mode.");
        new OutfitCreationConsole(this.getInput(), this.account, o);
    }
}
