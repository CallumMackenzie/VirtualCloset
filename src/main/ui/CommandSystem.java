package ui;

import java.util.*;
import java.util.stream.Collectors;

// A command system with a list of commands to process
// input with
public abstract class CommandSystem {

    // A provider for a scanner object which can have value set
    // across instances, as well as multiple internal scanners.
    public static class DynamicScanner {

        private Stack<Scanner> scanners;

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

    // EFFECTS: Initializes the command system. Ensure shouldRun is set
    //          to true, or the command system will immediately exit.
    protected abstract void init();

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
        this.commands.forEach(c -> c.process(cmd));
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
}
