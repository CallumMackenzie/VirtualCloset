package ui;

import model.Account;
import model.AccountManager;

import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

// Virtual Closet console application
public class VirtualClosetConsoleApp {

    private static final String DEFAULT_INPUT_PROMPT = "Enter a command (ex. \"help\"): ";

    private final ConsoleCommand[] commands = {
            new ConsoleCommand(this::exit,
                    "Exits the application.",
                    "quit", "q", "exit", "stop"),
            new ConsoleCommand(this::help,
                    "Displays general information about how to use Virtual Closet.",
                    "help", "h"),
            new ConsoleCommand(this::listAccounts,
                    "Lists the names of all accounts in the account manager.",
                    "list accounts"),
            new ConsoleCommand(this::createAccount,
                    "Creates a new account based on the info provided.",
                    "create account"),
            new ConsoleCommand(this::setActiveAccount,
                    "Sets the active account for which to perform closet commands for.",
                    "set active account", "set active")
    };

    private AccountManager accountManager;
    private boolean shouldRun;
    private Scanner input;
    private String inputPrompt;

    // Effects: Runs the Virtual Closet console application
    public VirtualClosetConsoleApp() {
        this.runVirtualCloset();
    }

    // Modifies: this
    // Effects: Processes user input
    private void runVirtualCloset() {
        this.init();
        System.out.println("Welcome to Virtual Closet!");

        while (this.shouldRun) {
            System.out.print(inputPrompt);
            String cmd = this.input.nextLine()
                    .toLowerCase()
                    .trim();
            for (ConsoleCommand command : commands) {
                command.process(cmd);
            }
        }
        this.input.close();
    }

    // Modifies: this
    // Effects: Sets up the scanner, account manager, and marks the application
    //          to run.
    private void init() {
        this.input = new Scanner(System.in);
        this.accountManager = new AccountManager();
        this.shouldRun = true;
        this.inputPrompt = DEFAULT_INPUT_PROMPT;
    }

    // Modifies: this
    // Effects: Marks the application to be closed when ready,
    //          and prints a message for the user to see.
    private void exit() {
        this.shouldRun = false;
        System.out.println("Closing application ...");
    }

    // Modifies: this
    // Effects: Retrieves an account name from the user, then creates
    //          and stores an account with the given name. If the account
    //          name is already taken, notifies user.
    private void createAccount() {
        System.out.print("\tEnter account name: ");
        String accountName = input.nextLine().trim();
        Account a = new Account(accountName);
        if (!this.accountManager.addAccount(a)) {
            System.out.println("\tAccount with name \""
                    + accountName
                    + "\" already exists!");
        } else {
            if (!this.accountManager.setActiveAccount(accountName)) {
                System.out.println("Fatal error");
            } else {
                System.out.println("\tAccount \"" + accountName + "\" created and set as active!");
            }
        }
    }

    // Effects: Prints a list of all accounts in the account manager
    private void listAccounts() {
        if (this.accountManager.getAccounts().size() == 0) {
            System.out.println("\tThere are no accounts.");
        } else {
            String accountListString = this.accountManager.getAccounts()
                    .stream()
                    .map(Account::getName)
                    .collect(Collectors.joining(", "));
            System.out.println("\tAccounts: " + accountListString + ".");
        }
    }

    // Effects: Prints a helpful blurb and a summary of each command
    private void help() {
        String prefix = "\n\t- ";
        System.out.println("Below is a list of commands. "
                + "Type \"?\" before the command to see more info. Ex. \"?help\"."
                + prefix
                + Arrays.stream(commands)
                .map(ConsoleCommand::getDigest)
                .collect(Collectors.joining(prefix)));
    }

    // Modifies: this
    // Effects: Sets the active account to the name of the one input by user,
    //          or informs them if the account does not exist.
    private void setActiveAccount() {
        System.out.print("\tEnter account name: ");
        String accountName = input.nextLine().trim();
        if (this.accountManager.setActiveAccount(accountName)) {
            System.out.println("\tActive account set to \"" + accountName + "\".");
        } else {
            System.out.println("\tCould not set active account to \"" + accountName + "\".");
        }
    }
}
