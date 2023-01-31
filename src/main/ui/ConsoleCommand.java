package ui;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

// A command for the console interface with a set of strings
// to activate the command, and a command response.
public final class ConsoleCommand {

    private final Set<String> validCommands;
    private final Runnable responseFn;
    private final String description;

    // Effects: Constructs a new console command from the given command
    //          keys, response function, and description.
    public ConsoleCommand(Runnable responseFn,
                          String description,
                          String... validCommands) {
        this.validCommands = Set.of(validCommands);
        this.responseFn = responseFn;
        this.description = description;
    }

    // Effects: Executes the response function if the given command
    //          is in the valid command set. If the given command starts
    //          with a question mark, it provides the description for this
    //          command if it matches the given.
    public void process(String cmd) {
        boolean run = true;
        if (cmd.startsWith("?")) {
            cmd = cmd.substring(1);
            run = false;
        }
        if (this.matchesCommand(cmd)) {
            if (run) {
                this.responseFn.run();
            } else {
                System.out.println(this.description);
            }
        }
    }

    // Effects: Returns whether the given command activates this console command
    public boolean matchesCommand(String cmd) {
        return this.validCommands.contains(cmd);
    }

    // Effects: Returns a string digest summarizing the command.
    public String getDigest() {
        return String.join(", ", this.validCommands);
    }

    // Effects: Returns the description for this command.
    public String getDescription() {
        return this.description;
    }

}
