package ui;

import model.*;
import model.search.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

// A console command system for a single closet
public final class ClosetModeConsole extends CommandSystem {

    private final Closet closet;

    // EFFECTS: Creates a new closet mode console with the given
    //          input stream and closet.
    public ClosetModeConsole(Scanner input, Closet closet) {
        super(input);
        this.closet = closet;
        this.run();
    }

    // MODIFIES: this
    // EFFECTS: Initializes the command system
    @Override
    protected void init() {
        this.setShouldRun(true);
        System.out.println("Closet mode initialized for \""
                + closet.getName() + "\".");
        this.initCommands();
    }

    // EFFECTS: Prompts for input for the next command and returns it.
    @Override
    protected String promptInput() {
        return this.getInput("Enter a closet command \""
                + closet.getName() + "\" (ex. \"help\"): ");
    }

    // MODIFIES: this
    // EFFECTS: Sets this command system to close
    private void exit() {
        this.setShouldRun(false);
        System.out.println("\tExiting closet mode for \""
                + this.closet.getName() + "\".");
    }

    // EFFECTS: Prints the types of clothing present in this closet
    private void listTypes() {
        if (this.closet.getTypes().isEmpty()) {
            System.out.println("\tNo types in closet.");
        } else {
            System.out.println("Types in closet: \n\t - "
                    + String.join("\n\t - ", closet.getTypes()));
        }
    }

    // Modifies: this
    // Effects: searches this closet with the clothing address parsed
    //          from the user
    private void clothingAddressSearch() {
        String input = this.getInput("Enter clothing address expression: ");
        try {
            ClothingAddress address = ClothingAddress.of(input);
            List<Clothing> clothing = this.closet.findClothing(address);
            if (clothing.isEmpty()) {
                System.out.println("\tNo clothing matched the given expression.");
            } else {
                System.out.println("Matches: \n" + clothing);
            }
        } catch (ClothingAddressParseException e) {
            System.out.println("\t" + e.getMessage()
                    + " Occurred at: \"" + e.getErrorState().getStateCaptured() + "\".");
        }
    }

    // Modifies: this
    // Effects: Guides the user through a clothing creation process.
    private void createClothing() {
        String input = this.getInput("\tEnter a clothing type to create: ")
                .toLowerCase();
        Clothing newClothing = this.createClothing(List.of(input),
                null, null, null, null, null,
                null);
        if (Objects.isNull(newClothing)) {
            return;
        }
        this.closet.addClothing(newClothing);
    }

    // Effects: Guides user through a clothing creation process and returns.
    //          Requests any null items from the user.
    private Clothing createClothing(List<String> types,
                                    Size size,
                                    String brand,
                                    String material,
                                    List<String> styles,
                                    Boolean dirty,
                                    Image image) {
        // TODO: Add color
        if (Objects.isNull(types = this.parseStringList("\tEnter clothing types", types))
                || Objects.isNull(size = this.parseEnumType("\tEnter size or type \"exit\": ", Size.class, size))
                || Objects.isNull(brand = this.parseString("brand", brand))
                || Objects.isNull(material = this.parseString("material", material))
                || Objects.isNull(styles
                = this.parseStringList("\tEnter a list of styles (ex. \"street, skate\")", styles))
                || Objects.isNull(dirty = this.parseBoolean("\tEnter whether the clothing is dirty", dirty))) {
            System.out.println("Cancelled.");
            return null;
        }
        return new Clothing(types, size, brand, material, styles, List.of(), dirty, image);
    }

    // Effects: Returns t if it is not null, otherwise invokes and
    //          returns the value of the supplier ifNull.
    private static <T> T ifNull(T t, Supplier<T> ifNull) {
        if (Objects.isNull(t)) {
            return ifNull.get();
        }
        return t;
    }

    // EFFECTS: If the input boolean is not null, returns it. Prompts the user
    //          for some boolean input and returns true or false depending on
    //          the user input.
    private Boolean parseBoolean(String prompt, Boolean bool) {
        return ifNull(bool, () -> {
            String in = this.getInput(prompt + ", \"yes\" or \"no\": ");
            if (in.equalsIgnoreCase("yes")) {
                return true;
            } else if (in.equalsIgnoreCase("no")) {
                return false;
            }
            return null;
        });
    }

    // EFFECTS: Formats a list of enum types in the given enum class to a string.
    private static <T extends Enum<T>> String formatEnumTypes(Class<T> enumClass) {
        return "Types: "
                + Arrays.stream(enumClass.getEnumConstants())
                .map(Object::toString)
                .collect(Collectors.joining("\n\t - "));
    }

    // EFFECTS: Prompts the user for an enum input, parses it from a string, and returns the
    //          enum value. If there is no corresponding value, returns null. If the input type
    //          is not null, returns it.
    private <T extends Enum<T>> T parseEnumType(String prompt, Class<T> enumClass, T type) {
        return ifNull(type, () -> {
            String typeInput = this.getInputTrimOnly(formatEnumTypes(enumClass)
                    + "\n" + prompt);
            T val = EnumListCapture.stringToEnumLoose(enumClass, typeInput.trim());
            if (val == null) {
                System.out.println("Input \"" + typeInput + "\" did not match any given value.");
            }
            return val;
        });
    }

    // EFFECTS: If the given string is null, prompts the user for a string
    //          input and returns it unless the string is "exit", in which
    //          case returns null. If the given string is not null, returns it.
    private String parseString(String prompt, String s) {
        return ifNull(s, () -> {
            String str = this.getInput("\tEnter " + prompt + " or \"exit\": ");
            if (str.equalsIgnoreCase("exit")) {
                return null;
            }
            return str;
        });
    }

    // EFFECTS: If the given list types is null, prompts the user for a list of strings
    //          and returns it. Otherwise, returns the input list.
    private List<String> parseStringList(String prompt, List<String> types) {
        return ifNull(types, () -> {
            String typesInput = this.getInput(prompt + " or type \"exit\": ")
                    .toLowerCase();
            if (typesInput.equalsIgnoreCase("exit")) {
                return null;
            }
            return Arrays.stream(typesInput.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
        });
    }

    // MODIFIES: this
    // EFFECTS: Sets up the commands for this console interface.
    private void initCommands() {
        this.addCommands(
                new ConsoleCommand(this::exit,
                        "Exits closet mode for \"" + this.closet.getName() + "\".",
                        "exit"),
                new ConsoleCommand(this::help,
                        "Displays commands available.",
                        "help", "h"),
                new ConsoleCommand(this::createClothing,
                        "Creates a new piece of clothing.",
                        "create clothing", "new"),
                new ConsoleCommand(this::listTypes,
                        "Lists the types of clothing in this closet.",
                        "list types"),
                new ConsoleCommand(this::clothingAddressSearch,
                        "Search closet by clothing address.",
                        "search"));
    }

}
