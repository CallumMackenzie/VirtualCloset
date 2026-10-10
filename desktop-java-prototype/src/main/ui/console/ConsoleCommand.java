package ui.console;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;

// A command for the console interface with a set of strings
// to activate the command, a condition, a command response,
// and a message if the command condition is not met.
public final class ConsoleCommand {

    private final Set<String> validCommands;
    private final Runnable responseFn;
    private final BooleanSupplier activeCondition;
    private final String inactiveMessage;
    private final String description;

    // EFFECTS: Constructs a new console command from the given command
    //          keys, active condition, response function, description,
    //          and inactive message.
    public ConsoleCommand(Runnable responseFn,
                          BooleanSupplier activeCondition,
                          String inactiveMessage,
                          String description,
                          String... validCommands) {
        this.validCommands = new HashSet<>();
        this.validCommands.addAll(Arrays.asList(validCommands));
        this.responseFn = responseFn;
        this.description = description;
        this.inactiveMessage = inactiveMessage;
        this.activeCondition = activeCondition;
    }

    // EFFECTS: Constructs a new console command from the given command
    //          keys, response function, and description.
    public ConsoleCommand(Runnable responseFn,
                          String description,
                          String... validCommands) {
        this(responseFn, () -> true, "", description, validCommands);
    }

    // EFFECTS: Executes the response function if the given command
    //          is in the valid command set. If the given command starts
    //          with a question mark, it provides the description for this
    //          command if it matches the given. Returns true if this command
    //          consumed the input.
    public boolean process(String cmd, int cmdIdx) {
        boolean run = true;
        if (cmd.startsWith("?")) {
            cmd = cmd.substring(1);
            run = false;
        }
        if (this.matchesCommand(cmd, cmdIdx)) {
            if (run) {
                if (!this.isActive()) {
                    System.out.println(this.inactiveMessage);
                } else {
                    this.responseFn.run();
                }
            } else {
                System.out.println(this.description);
            }
            return true;
        }
        return false;
    }

    // EFFECTS: Returns whether this command is active
    public boolean isActive() {
        return this.activeCondition.getAsBoolean();
    }

    // EFFECTS: Returns whether the given command activates this console command
    public boolean matchesCommand(String cmd, int index) {
        Integer cmdIndex;
        try {
            cmdIndex = Integer.parseInt(cmd);
        } catch (NumberFormatException e) {
            cmdIndex = null;
        }
        return (cmdIndex != null && cmdIndex == index)
                || this.validCommands.contains(cmd);
    }

    // EFFECTS: Returns a string digest summarizing the command.
    public String getDigest() {
        return String.join(", ", this.validCommands);
    }

}
