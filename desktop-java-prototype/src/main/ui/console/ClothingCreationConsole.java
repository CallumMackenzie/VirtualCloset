package ui.console;

import model.Closet;
import model.Clothing;
import model.Size;
import model.search.EnumListCapture;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static model.search.CAStateMachineBuilder.LIST_END_STR;
import static model.search.CAStateMachineBuilder.LIST_SEPARATOR_STR;

public class ClothingCreationConsole extends CommandSystem {

    private final Clothing clothing;
    private boolean shouldSave = true;

    // REQUIRES: clothing must have interior lists which are mutable.
    // EFFECTS: Creates a new clothing creation console from the given article
    //          of clothing.
    public ClothingCreationConsole(DynamicScanner ds,
                                   Closet closet,
                                   Clothing clothing) {
        super(ds);
        this.clothing = clothing;
        closet.removeClothing(this.clothing);
        this.run();
        if (shouldSave) {
            closet.addClothing(this.clothing);
        }
    }

    // EFFECTS: Prompts the user for input and returns it.
    @Override
    protected String promptInput() {
        return this.getInput("Enter a clothing creation command (ex \"help\"): ");
    }

    // REQUIRES: this.init has not been called
    // MODIFIES: this
    // EFFECTS: Prints a message and initializes the command system.
    @Override
    protected void init() {
        System.out.println("\tEntering clothing creation mode.");
        super.init();
    }

    // REQUIRES: this.initCommands has not been called
    // MODIFIES: this
    // EFFECTS: Sets up commands for this system
    @Override
    protected void initCommands() {
        this.initBasicCommands("Lists clothing creation commands.",
                "Exits clothing creation mode.",
                "exit");
        this.initClothingEditCommands();
        this.initStringListEditCommands();
    }

    // MODIFIES: this
    // EFFECTS: Exits the creation mode without saving
    private void exitNoSave() {
        this.stop();
        this.shouldSave = false;
    }

    // REQUIRES: this.initClothingCommands has not been called
    // MODIFIES: this
    // EFFECTS: Initializes clothing commands
    private void initClothingEditCommands() {
        this.addCommands(
                new ConsoleCommand(this::exitNoSave,
                        "Exits clothing creation mode and does not save.",
                        "discard"),
                new ConsoleCommand(this.createStringEditFn(this.clothing::setBrand, "brand"),
                        "Sets the brand for this clothing (ex. \"Nike\").",
                        "set brand"),
                new ConsoleCommand(this.createStringEditFn(this.clothing::setMaterial, "material"),
                        "Sets the material for this clothing (ex. \"cotton\").",
                        "set material"),
                new ConsoleCommand(this.createEnumEditFn(Size.class, this.clothing::setSize, "size"),
                        "Sets the size for this clothing (ex. " + Size.XL + ").",
                        "set size"),
                new ConsoleCommand(this::printClothing,
                        "Prints this clothing to the console.",
                        "print")
        );
    }

    // REQUIRES: this.initStringListEditCommands has not been called
    // MODIFIES: this
    // EFFECTS: Initializes string list edit commands
    private void initStringListEditCommands() {
        this.addCommands(
                new ConsoleCommand(this.createStringListAddFn(this.clothing::getTypes, "types"),
                        "Adds a clothing type to this article of clothing (ex. \"shirt\").",
                        "add type"),
                new ConsoleCommand(this.createStringListRemoveFn(this.clothing::getTypes, "types"),
                        () -> !this.clothing.getTypes().isEmpty(), "No clothing types!",
                        "Removes a clothing type from this article of clothing (ex \"sweater\").",
                        "remove type"),
                new ConsoleCommand(this.createStringListAddFn(this.clothing::getStyles, "styles"),
                        "Adds a style to this article of clothing (ex. \"casual\").",
                        "add style"),
                new ConsoleCommand(this.createStringListAddFn(this.clothing::getColors, "colors"),
                        "Adds a color to this article of clothing (ex. \"orange\").",
                        "add color"),
                new ConsoleCommand(this.createStringListRemoveFn(this.clothing::getColors, "colors"),
                        () -> !this.clothing.getColors().isEmpty(), "No clothing colors!",
                        "Removes a color from this article of clothing (ex. \"blue\").",
                        "remove color"),
                new ConsoleCommand(this.createStringListRemoveFn(this.clothing::getStyles, "styles"),
                        () -> !this.clothing.getStyles().isEmpty(), "No clothing styles!",
                        "Removes a style from this article of clothing (ex. \"athletic\").",
                        "remove style")
        );
    }


    // EFFECTS: Prints the clothing string representation to the console
    private void printClothing() {
        System.out.println(this.clothing);
    }

    // MODIFIES: this
    // EFFECTS: Confirms the user wants to exit and does so if they do.
    @Override
    protected void stop() {
        String shouldExit = this.getInput("\tAre you sure you want to exit (y/n)? ");
        if (shouldExit.equalsIgnoreCase("y")) {
            super.stop();
            System.out.println("\tExiting clothing creation mode.");
        } else {
            System.out.println("\tExit not confirmed. Staying in clothing creation mode.");
        }
    }

    // EFFECTS: Returns a runnable which prompts the user to select an enum variant
    //          of the class provided, parses it, and passes it to setEnumFn if it
    //          matched a valid enum constant loosely. Otherwise, prints an error message.
    private <T extends Enum<T>> Runnable createEnumEditFn(
            Class<T> enumClass,
            Consumer<T> setEnumFn,
            String enumName
    ) {
        return () -> {
            System.out.println("\tValues: " + Arrays.stream(enumClass.getEnumConstants())
                    .map(T::toString).collect(Collectors.joining(", ")));
            String input = this.getInput("\tEnter a new value for " + enumName + ": ");
            T newValue = EnumListCapture.stringToEnumLoose(enumClass, input);
            if (newValue == null) {
                System.out.println("\tInput \"" + input + "\" did not match any given values.");
            } else {
                setEnumFn.accept(newValue);
                System.out.println("\tSet " + enumName + " to " + newValue + ".");
            }
        };
    }

    // EFFECTS: Returns a runnable which retrieves an input from the user to pass to
    //          the given string consumer.
    private Runnable createStringEditFn(
            Consumer<String> setStringFn,
            String fieldName
    ) {
        return () -> {
            String input = this.getInput("\tEnter a new value for " + fieldName + ": ");
            setStringFn.accept(input);
            System.out.println("\tSet " + fieldName + " to \"" + input + "\".");
        };
    }

    // EFFECTS: Returns a runnable which retrieves an input from the user to add,
    //          and removes the given input from the list provided by the input getter
    //          method. Then, prints a message to the user about whether it was removed
    //          or not.
    private Runnable createStringListRemoveFn(
            Supplier<List<String>> getStringList,
            String listName
    ) {
        return () -> {
            String type = this.getInput("\tEnter an element to remove from " + listName + ": ");
            if (getStringList.get().remove(type)) {
                System.out.println("\tRemoved \"" + type + "\" from " + listName + ".");
            } else {
                System.out.println("\tCould not remove \"" + type + "\" from " + listName + ".");
            }
        };
    }

    // EFFECTS: Returns a runnable which retrieves an input from the user to add,
    //          ensures it is valid, and adds it to the list given by the provided
    //          getter method.
    private Runnable createStringListAddFn(
            Supplier<List<String>> getStringList,
            String listName
    ) {
        return () -> {
            String type = this.getInput("\tEnter an element to add to " + listName + ": ");
            if (type.contains(LIST_SEPARATOR_STR)) {
                System.out.println("\tInput cannot contain \"" + LIST_SEPARATOR_STR + "\"!");
            } else if (type.contains(LIST_END_STR)) {
                System.out.println("\tInput cannot contain \"" + LIST_END_STR + "\"!");
            } else {
                getStringList.get().add(type);
                System.out.println("\tAdded type \"" + type + "\" to " + listName + ".");
            }
        };
    }
}
