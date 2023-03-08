package ui.console;

import model.Closet;
import model.Clothing;
import model.Size;
import model.search.ClothingAddress;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

// A console command system for a single closet
public final class ClosetModeConsole extends CommandSystem {

    private final Closet closet;

    // EFFECTS: Creates a new closet mode console with the given
    //          input stream and closet.
    public ClosetModeConsole(DynamicScanner input, Closet closet) {
        super(input);
        this.closet = closet;
        this.run();
    }

    // MODIFIES: this
    // EFFECTS: Initializes the command system
    @Override
    protected void init() {
        super.init();
        System.out.println("Closet mode initialized for \""
                + closet.getName() + "\".");
    }

    // EFFECTS: Prompts for input for the next command and returns it.
    @Override
    protected String promptInput() {
        return this.getInput("Enter a closet command \""
                + closet.getName() + "\" (ex. \"help\"): ");
    }

    // MODIFIES: this
    // EFFECTS: Sets this command system to close
    @Override
    protected void stop() {
        super.stop();
        System.out.println("\tExiting closet mode for \""
                + this.closet.getName() + "\".");
    }

    // EFFECTS: Formats and prints the given list to the console
    //          with the given title.
    private <T> void printClosetStringList(String title,
                                           Collection<T> types) {
        if (types.isEmpty()) {
            System.out.println("\tNo " + title.toLowerCase() + " in closet.");
        } else {
            System.out.println(title + " in closet: \n\t - "
                    + types.stream().map(T::toString)
                    .collect(Collectors.joining("\n\t - ")));
        }
    }

    // MODIFIES: this
    // EFFECTS: searches this closet with the clothing address parsed
    //          from the user
    private void clothingAddressSearch() {
        String input = this.getInput("\tEnter clothing address expression: ");
        ClothingAddress ca = this.address(input);
        if (ca != null) {
            List<Clothing> clothing = this.closet.findClothing(ca);
            if (clothing.isEmpty()) {
                System.out.println("\tNo clothing matched the given expression.");
            } else {
                System.out.println("Matches: \n" + clothing);
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: Prompts the user to select clothing to edit from a search expression.
    private void editClothing() {
        ClothingAddress addr = this.address(this.getInput(
                "\tEnter clothing address expression: "));
        if (addr != null) {
            List<Clothing> clothing = this.closet.findClothing(addr);
            Clothing c = this.getClothingIndexed(clothing);
            if (c != null) {
                new ClothingCreationConsole(this.getInput(),
                        this.closet,
                        c);
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: Guides the user through a clothing creation process.
    private void createClothing() {
        Clothing newClothing = new Clothing(new ArrayList<>(),
                Size.UNKNOWN,
                "",
                "",
                new ArrayList<>(),
                new ArrayList<>(),
                false);
        new ClothingCreationConsole(getInput(), this.closet,
                newClothing);
    }

    // REQUIRES: this.initCommands has not been called
    // MODIFIES: this
    // EFFECTS: Sets up the commands for this console interface.
    @Override
    protected void initCommands() {
        this.initBasicCommands("Displays commands available.",
                "Exits closet mode for \"" + this.closet.getName() + "\".",
                "exit");
        this.initSearchCommands();
        this.initClosetCommands();
    }

    // MODIFIES: this
    // EFFECTS: Sets up search commands
    private void initSearchCommands() {
        this.addCommands(new ConsoleCommand(this::clothingAddressSearch,
                "Search closet by clothing address.",
                "search"));
    }

    // MODIFIES: this
    // EFFECTS: Sets up listing commands such as listing clothing types,
    //          brands, etc present in the closet
    private void initClosetCommands() {
        this.addCommands(new ConsoleCommand(() -> this.printClosetStringList("Types",
                        this.closet.getTypes()),
                        "Lists the types of clothing in this closet.",
                        "types"),
                new ConsoleCommand(() -> this.printClosetStringList("Brands",
                        this.closet.getBrands()),
                        "Lists the brands of clothing in this closet.",
                        "brands"),
                new ConsoleCommand(() -> this.printClosetStringList("Styles",
                        this.closet.getStyles()),
                        "Lists the styles of clothing in this closet.",
                        "styles"),
                new ConsoleCommand(() -> this.printClosetStringList("Sizes",
                        this.closet.getSizes()), "Lists the sizes of clothing in this closet.",
                        "list sizes", "sizes"),
                new ConsoleCommand(() -> this.printClosetStringList("Colors",
                        this.closet.getColors()),
                        "Lists the colors of clothing in this closet.",
                        "colors"),
                new ConsoleCommand(this::createClothing, "Creates a new piece of clothing.",
                        "create clothing", "new"),
                new ConsoleCommand(this::editClothing, "Edits a piece of clothing chosen by search.",
                        "edit clothing", "edit"));
    }
}
