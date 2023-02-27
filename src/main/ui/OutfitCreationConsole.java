package ui;

import model.Account;
import model.Closet;
import model.Clothing;
import model.Outfit;
import model.search.ClothingAddress;

import java.util.List;
import java.util.Optional;

// An interface to create outfits
public class OutfitCreationConsole extends CommandSystem {

    private final Account account;
    private final Outfit outfit;

    // EFFECTS: Creates a new outfit creation console from the given outfit
    //          and dynamic scanner.
    public OutfitCreationConsole(DynamicScanner ds,
                                 Account account,
                                 Outfit o) {
        super(ds);
        this.outfit = o;
        this.account = account;
        this.run();
    }

    // EFFECTS: Prompts user for input and returns it
    @Override
    protected String promptInput() {
        return getInput("Enter outfit creation command (ex \"help\") for \""
                + this.outfit.getName() + "\": ");
    }

    // REQUIRES: this.initCommands has not been called
    // MODIFIES: this
    // EFFECTS: Initializes commands
    @Override
    protected void initCommands() {
        this.initBasicCommands("Lists commands for the outfit creation console.",
                "Exits outfit creation mode.", "exit");
        this.initOutfitCommands();
    }

    // REQUIRES: this.initOutfitCommands has not been called
    // MODIFIES: this
    // EFFECTS: Initializes outfit commands
    private void initOutfitCommands() {
        this.addCommands(
                new ConsoleCommand(this::setOutfitName,
                        "Sets the name of this outfit.",
                        "set name", "name"),
                new ConsoleCommand(this::listOutfitClothing,
                        "Lists the clothing in this outfit.",
                        "list", "clothing"),
                new ConsoleCommand(this::addClothingByClosestMatch,
                        "Adds user-provided clothing from the given closet"
                                + " most closely matching the search params given.",
                        "add", "add clothing"),
                new ConsoleCommand(this::removeClothingIndexed,
                        () -> !this.outfit.getClothing().isEmpty(),
                        "No clothing in outfit!",
                        "Removes selected clothing from this outfit.",
                        "remove", "remove clothing")
        );
    }

    // MODIFIES: this
    // EFFECTS: Prints a message and indicates this command system should exit
    @Override
    protected void stop() {
        System.out.println("\tExiting outfit creation mode.");
        super.stop();
    }

    // MODIFIES: this
    // EFFECTS: Adds the user-provided clothing to the outfit
    private void addClothingByClosestMatch() {
        String closetName = this.getInput("\tEnter name of closet to search: ");
        Optional<Closet> closet = this.account.getCloset(closetName);
        if (!closet.isPresent()) {
            System.out.println("\tCloset \"" + closetName + "\" does not exist.");
        } else {
            String searchExpression = this.getInput("\tEnter search expression: ");
            ClothingAddress address = this.address(searchExpression);
            if (address != null) {
                List<Clothing> clothing = closet.get().findClothing(address);
                if (clothing.isEmpty()) {
                    System.out.println("\tNo matches for the given expression!");
                } else {
                    System.out.println(formatIndexed(clothing));
                    int idx = this.forceGetIntInput(
                            "\tSelect index of clothing to add: ",
                            () -> System.out.println("\tInput was not a number!"));
                    this.addClothingAtIndex(clothing, idx);
                }
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: Removes the clothing at the given index from the outfit.
    private void removeClothingIndexed() {
        List<Clothing> clothing = this.outfit.getClothing();
        Clothing toRemove = this.getClothingIndexed(clothing);
        if (toRemove != null) {
            this.outfit.removeClothing(toRemove);
            System.out.println("\tRemoved clothing.");
        }
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

    // MODIFIES: this
    // EFFECTS: Adds the clothing at index i to the outfit if it is in
    //          a valid range of the given clothing list. Prints a message
    //          for the user regarding events which occurred.
    private void addClothingAtIndex(List<Clothing> clothing, int idx) {
        if (idx < 0 || idx >= clothing.size()) {
            System.out.println("\tNumber out of index range. Exiting.");
        } else {
            this.outfit.addClothing(clothing.get(idx));
            System.out.println("\tAdded clothing to outfit: \n"
                    + clothing.get(idx));
        }
    }

}
