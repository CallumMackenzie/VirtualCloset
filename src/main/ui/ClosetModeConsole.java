package ui;

import model.Closet;
import model.Clothing;
import model.Size;
import model.search.ClothingAddress;
import model.search.ClothingAddressParseException;
import model.search.EnumListCapture;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
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

    // EFFECTS: Returns t if it is not null, otherwise invokes and
    //          returns the value of the supplier ifNull.
    private static <T> T ifNull(T t, Supplier<T> ifNull) {
        if (Objects.isNull(t)) {
            return ifNull.get();
        }
        return t;
    }

    // EFFECTS: Formats a list of enum types in the given enum class to a string.
    private static <T extends Enum<T>> String formatEnumTypes(Class<T> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(Object::toString)
                .collect(Collectors.joining(", "));
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

    // MODIFIES: this
    // EFFECTS: Guides the user through a clothing creation process.
    private void createClothing() {
        String input = this.getInput("\tEnter a clothing type to create: ")
                .toLowerCase();
        Clothing newClothing = this.createClothing(Arrays.asList(input),
                null, null, null, null, null,
                null);
        if (Objects.isNull(newClothing)) {
            return;
        }
        this.closet.addClothing(newClothing);
    }

    // EFFECTS: Guides user through a clothing creation process and returns.
    //          Requests any null items from the user.
    private Clothing createClothing(List<String> types,
                                    Size size,
                                    String brand,
                                    String material,
                                    List<String> styles,
                                    Boolean dirty,
                                    Image image) {
        // TODO: Add color
        if (Objects.isNull(types = this.parseStringList("clothing types", types))
                || Objects.isNull(size = this.parseEnumType("size", Size.class, size))
                || Objects.isNull(brand = this.parseString("brand", brand))
                || Objects.isNull(material = this.parseString("material", material))
                || Objects.isNull(styles
                = this.parseStringList("styles (ex. \"street, skate\")", styles))
                || Objects.isNull(dirty = this.parseBoolean("\tEnter whether the clothing is dirty", dirty))) {
            System.out.println("Cancelled.");
            return null;
        }
        return new Clothing(types, size, brand, material, styles, Arrays.asList(), dirty, image);
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

    // EFFECTS: Prompts the user for an enum input, parses it from a string, and returns the
    //          enum value. If there is no corresponding value, returns null. If the input type
    //          is not null, returns it.
    private <T extends Enum<T>> T parseEnumType(String prompt, Class<T> enumClass, T type) {
        return ifNull(type, () -> {
            String typeInput = this.getInputTrimOnly(
                    "\tTypes: "
                            + formatEnumTypes(enumClass)
                            + "\n\tEnter " + prompt + " or type \"exit\": ");
            if (typeInput.equalsIgnoreCase("exit")) {
                return null;
            }
            T val = EnumListCapture.stringToEnumLoose(enumClass, typeInput);
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
            String typesInput = this.getInput("\tEnter " + prompt + " or type \"exit\": ")
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
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
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
                new ConsoleCommand(() -> this.printClosetStringList("Types",
                        this.closet.getTypes()),
                        "Lists the types of clothing in this closet.",
                        "list types"),
                new ConsoleCommand(() -> this.printClosetStringList("Brands",
                        this.closet.getBrands()),
                        "Lists the brands of clothing in this closet.",
                        "list brands"),
                new ConsoleCommand(() -> this.printClosetStringList("Styles",
                        this.closet.getStyles()),
                        "Lists the styles of clothing in this closet.",
                        "list styles"),
                new ConsoleCommand(this::clothingAddressSearch,
                        "Search closet by clothing address.",
                        "search")
        );
    }

}
