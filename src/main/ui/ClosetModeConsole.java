package ui;

import model.Closet;

import java.util.Scanner;

// TODO
public final class ClosetModeConsole extends CommandSystem {

    private final Closet closet;

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

    private void exit() {
        this.setShouldRun(false);
        System.out.println("\tExiting closet mode for \""
                + this.closet.getName() + "\".");
    }

    private void initCommands() {
        this.addCommands(new ConsoleCommand(this::exit,
                "Exits closet mode for \"" + this.closet.getName() + "\".",
                "exit"));
    }

}
