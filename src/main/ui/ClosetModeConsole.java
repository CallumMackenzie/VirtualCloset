package ui;

import model.*;
import model.search.ClothingAddress;
import model.search.ClothingAddressParseException;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

// TODO
public final class ClosetModeConsole extends CommandSystem {

    private final Closet closet;

    // TODO
    public ClosetModeConsole(Scanner input, Closet closet) {
        super(input);
        this.closet = closet;
        this.run();
    }

    @Override
    protected void init() {
        this.setShouldRun(true);
        System.out.println("Closet mode initialized for \""
                + closet.getName() + "\".");
        this.initCommands();
    }

    @Override
    protected String promptInput() {
        return this.getInput("Enter a closet command \""
                + closet.getName() + "\" (ex. \"help\"): ");
    }

    // Modifies: this
    // Effects: Sets this command system to close
    private void exit() {
        this.setShouldRun(false);
        System.out.println("\tExiting closet mode for \""
                + this.closet.getName() + "\".");
    }

    // Effects: Prints the types of clothing present in this closet
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
        String input = this.getInput("Enter clothing addres expression: ");
        ClothingAddress address = null;
        try {
            address = ClothingAddress.of(input);
        } catch (ClothingAddressParseException e) {
            // TODO
            throw new RuntimeException(e);
        }
        List<Clothing> clothing = this.closet.findClothing(address);
        if (clothing.isEmpty()) {
            System.out.println("No clothing matched the given expression.");
        } else {
            System.out.println("Matches: \n" + clothing);
        }
    }

    // Modifies: this
    // Effects: guides the user through a clothing creation process
    private void createClothing() {
        String input = this.getInput("Enter a clothing type to create: ")
                .toLowerCase();
        Clothing newClothing;
        switch (input) {
            case "shirt":
                newClothing = this.createClothing(List.of("shirt"),
                        null, null, null, null, null,
                        null);
                break;
            default:
                newClothing = this.createClothing(List.of(input),
                        null, null, null, null, null,
                        null);
                break;
        }
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
        if (Objects.isNull(types = this.parseStringList("Enter clothing types", types))
                || Objects.isNull(size = this.parseEnumType("Enter size or type \"exit\": ", Size.class, size))
                || Objects.isNull(brand = this.parseString("brand", brand))
                || Objects.isNull(material = this.parseString("material", material))
                || Objects.isNull(styles
                = this.parseStringList("Enter a list of styles (ex. \"street, skate\")", styles))
                || Objects.isNull(dirty = this.parseBoolean("Enter whether the clothing is dirty", dirty))) {
            System.out.println("Cancelled.");
            return null;
        }
        return new Clothing(types, size, brand, material, styles, dirty, image);
    }

    // Effects: Returns t if it is not null, otherwise invokes and
    //          returns the value of the supplier ifNull.
    private static <T> T ifNull(T t, Supplier<T> ifNull) {
        if (Objects.isNull(t)) {
            return ifNull.get();
        }
        return t;
    }

    // TODO
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

    // TODO
    private static <T extends Enum<T>> String formatEnumTypes(Class<T> enumClass) {
        return "Types: "
                + Arrays.stream(enumClass.getEnumConstants())
                .map(Object::toString)
                .collect(Collectors.joining("\n\t - "));
    }

    // TODO
    private <T extends Enum<T>> T parseEnumType(String prompt, Class<T> enumClass, T type) {
        return ifNull(type, () -> {
            String typeInput = this.getInputTrimOnly(formatEnumTypes(enumClass)
                    + "\n" + prompt);
            try {
                return Enum.valueOf(enumClass, typeInput);
            } catch (IllegalArgumentException e) {
                System.out.println("Input did not match a given type.");
                return null;
            }
        });
    }

    // TODO
    private String parseString(String prompt, String s) {
        return ifNull(s, () -> {
            String str = this.getInput("Enter " + prompt + " or \"exit\": ");
            if (str.equalsIgnoreCase("exit")) {
                return null;
            }
            return str;
        });
    }

    // TODO
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

    // TODO
    private <T extends Enum<T>> List<T> parseEnumList(String prompt,
                                                      List<T> list,
                                                      Class<T> enumClass) {
        return ifNull(list, () -> {
            String typeInput = this.getInput(formatEnumTypes(enumClass)
                    + "\n" + prompt).toLowerCase();
            return Arrays.stream(typeInput.split(""))
                    .map(String::trim)
                    .map(ti -> {
                        try {
                            return Enum.valueOf(enumClass, ti);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Input did not match a given type.");
                            return null;
                        }
                    }).collect(Collectors.toList());
        });
    }

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
