package ui.console;

import model.Clothing;
import model.search.ClothingAddress;
import model.search.ClothingAddressParseException;

import java.util.*;
import java.util.stream.Collectors;

// A command system with a list of commands to process
// input with
public abstract class CommandSystem {

    // Subscribers to outermost user input events.
    private final List<ConsoleCommand> commands;
    private final DynamicScanner input;
    private boolean shouldRun;

    // EFFECTS: Creates a new command system.
    public CommandSystem(DynamicScanner input) {
        this.commands = new ArrayList<>();
        this.input = input;
        this.shouldRun = false;
    }

    // EFFECTS: Prompts the user for a command and returns it
    protected abstract String promptInput();

    // EFFECTS: Initializes the command system, with should run as true.
    //          Calls initCommands.
    protected void init() {
        this.setShouldRun(true);
        this.initCommands();
    }

    // REQUIRES: this.initCommands has not been called
    // MODIFIES: this
    // EFFECTS: Initializes commands for this command system
    protected abstract void initCommands();

    // REQUIRES: this.initBasicCommands has not been called
    // MODIFIES: this
    // EFFECTS: Initializes basic commands stop and help, and adds them
    protected void initBasicCommands(String helpDesc,
                                     String stopDesc, String stopKeyword) {
        this.addCommands(
                new ConsoleCommand(this::help, helpDesc, "help"),
                new ConsoleCommand(this::stop, stopDesc, stopKeyword)
        );
    }

    // MODIFIES: this
    // EFFECTS: Sets shouldRun to the desired value
    protected void setShouldRun(boolean shouldRun) {
        this.shouldRun = shouldRun;
    }

    // EFFECTS: Starts the console system processing input
    protected void run() {
        this.init();
        while (this.shouldRun) {
            String cmd = this.promptInput();
            this.processCommand(cmd);
        }
    }

    // EFFECTS: Returns the scanner object for this command system
    protected DynamicScanner getInput() {
        return this.input;
    }

    // EFFECTS: Prompts input with the given string, formats it,
    //          and returns it.
    protected String getInput(String prompt) {
        return this.getInputTrimOnly(prompt)
                .toLowerCase();
    }

    // EFFECTS: Repeates getIntInput with the given prompt until a valid
    //          output is returned, running the error function every time
    //          it does not produce a valid output.
    protected Integer forceGetIntInput(String prompt, Runnable errorFn) {
        Integer idx;
        while ((idx = this.getIntInput(prompt)) == null) {
            errorFn.run();
        }
        return idx;
    }

    // EFFECTS: Prompts input with the given string, formats it,
    //          and attempts to parse it to an int, otherwise returns
    //          null.
    protected Integer getIntInput(String prompt) {
        String str = this.getInput(prompt);
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // EFFECTS: Prompts input with the given string, and trims it.
    protected String getInputTrimOnly(String prompt) {
        System.out.print(prompt);
        return this.getInput()
                .nextLine()
                .trim();
    }

    // MODIFIES: this
    // EFFECTS: Adds all given commands to the command system
    protected void addCommands(ConsoleCommand... cmds) {
        this.commands.addAll(Arrays.asList(cmds));
    }

    // EFFECTS: Processes the given string with the set commands
    protected void processCommand(String cmd) {
        for (ConsoleCommand c : this.commands) {
            if (c.process(cmd)) {
                return;
            }
        }
        System.out.println("\tNo command matched the input \"" + cmd + "\".");
    }

    // EFFECTS: Prints a helpful blurb for each command
    protected void help() {
        String prefix = "\n\t- ";
        System.out.println("Below is a list of commands. "
                + "Type \"?\" before the command to see more info. "
                + "Ex. \"help\" -> \"?help\"."
                + prefix
                + commands.stream()
                .filter(ConsoleCommand::isActive)
                .map(ConsoleCommand::getDigest)
                .collect(Collectors.joining(prefix)));
    }

    // MODIFIES: this
    // EFFECTS: Sets shouldRun to false
    protected void stop() {
        this.setShouldRun(false);
    }

    // A provider for a scanner object which can have value set
    // across instances, as well as multiple internal scanners.
    public static class DynamicScanner {

        private final Stack<Scanner> scanners;

        // EFFECTS: Creates a new dynamic scanner with the given
        //          scanner input.
        public DynamicScanner(Scanner initial) {
            this.scanners = new Stack<>();
            this.scanners.push(initial);
        }

        // REQUIRES: this.hasNextLine is true
        // EFFECTS: Returns the next line from the highest priority
        //          internal scanner
        public String nextLine() {
            if (!this.hasNextLine()) {
                throw new RuntimeException("No next line in scanner!");
            }
            return this.scanners.peek().nextLine();
        }

        // MODIFIES: this
        // EFFECTS: Returns whether this scanner has a next line
        public boolean hasNextLine() {
            if (this.scanners.isEmpty()) {
                return false;
            }
            if (this.scanners.peek().hasNext()) {
                return true;
            }
            this.scanners.pop();
            return this.hasNextLine();
        }

        // REQUIRES: this.hasNextLine is true
        // EFFECTS: Returns the scanner object.
        public Scanner getScanner() {
            return this.scanners.peek();
        }

        // MODIFIES: this
        // EFFECTS: Sets the scanner for this object, making it
        //          the highest priority.
        public void addScanner(Scanner scanner) {
            this.scanners.push(scanner);
        }
    }

    // EFFECTS: Returns the given list in an indexed string representation
    protected static <T> String formatIndexed(List<T> in) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < in.size(); ++i) {
            sb.append(i)
                    .append(": ")
                    .append(in.get(i).toString());
            if (i + 1 != in.size()) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    // MODIFIES: this
    // EFFECTS: Prompts the user to select an article of clothing from the list,
    //          returning it if present. Otherwise, returns null.
    protected Clothing getClothingIndexed(List<Clothing> clothing) {
        System.out.println(formatIndexed(clothing));
        int idx = this.forceGetIntInput("\tEnter index of clothing: ",
                () -> System.out.println("\tInput was not a number!"));
        if (idx < 0 || idx >= clothing.size()) {
            System.out.println("\tIndex out of range. Exiting.");
        } else {
            return clothing.get(idx);
        }
        return null;
    }

    // EFFECTS: Prompts the user for a clothing address expression,
    //          returning it if there are no errors. Otherwise, prints
    //          the errors and returns null.
    protected ClothingAddress address(String expr) {
        try {
            return ClothingAddress.of(expr);
        } catch (ClothingAddressParseException e) {
            System.out.println("\t" + e.getMessage()
                    + " Occurred at \"" + e.getErrorState().getStateCaptured() + "\".");
            return null;
        }
    }
}
