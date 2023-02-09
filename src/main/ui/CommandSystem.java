package ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

// A command system with a list of commands to process
// input with
public abstract class CommandSystem {

    // Subscribers to outermost user input events.
    private final List<ConsoleCommand> commands;
    private final Scanner input;
    private boolean shouldRun;

    // Effects: Creates a new command system.
    public CommandSystem(Scanner input) {
        this.commands = new ArrayList<>();
        this.input = input;
        this.shouldRun = false;
    }

    // Effects: Prompts the user for a command and returns it
    protected abstract String promptInput();

    // Effects: Initializes the command system. Ensure shouldRun is set
    //          to true, or the command system will immediately exit.
    protected abstract void init();

    // Modifies: this
    // Effects: Sets shouldRun to the desired value
    protected void setShouldRun(boolean shouldRun) {
        this.shouldRun = shouldRun;
    }

    // Effects: Starts the console system processing input
    protected void run() {
        this.init();
        while (this.shouldRun) {
            String cmd = this.promptInput();
            this.processCommand(cmd);
        }
    }

    // Effects: Returns the scanner object for this command system
    protected Scanner getInput() {
        return this.input;
    }

    // Effects: Prompts input with the given string, formats it,
    //          and returns it.
    protected String getInput(String prompt) {
        System.out.print(prompt);
        return this.input.nextLine()
                .toLowerCase()
                .trim();
    }

    // Effects: Prompts input with the given string, and trims it.
    protected String getInputTrimOnly(String prompt) {
        System.out.print(prompt);
        return this.input.nextLine()
                .trim();
    }

    // Modifies: this
    // Effects: Adds all given commands to the command system
    protected void addCommands(ConsoleCommand... cmds) {
        this.commands.addAll(List.of(cmds));
    }

    // Effects: Processes the given string with the set commands
    protected void processCommand(String cmd) {
        this.commands.forEach(c -> c.process(cmd));
    }

    // Effects: Prints a helpful blurb for each command
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
