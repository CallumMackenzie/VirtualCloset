package ui;

import model.Account;
import model.AccountManager;
import model.Closet;

import java.util.Scanner;
import java.util.stream.Collectors;

// Virtual Closet console application
public final class VirtualClosetConsoleApp extends CommandSystem {

    private static final String DEFAULT_INPUT_PROMPT = "Enter a command (ex. \"help\"): ";

    private AccountManager accountManager;

    // Effects: Runs the Virtual Closet console application
    public VirtualClosetConsoleApp() {
        super(new Scanner(System.in));
        this.run();
        this.getInput().close();
    }

    // Modifies: this
    // Effects: Sets up the scanner, account manager, and marks the application
    //          to run.
    @Override
    protected void init() {
        this.accountManager = new AccountManager();
        this.initCommands();
        this.setShouldRun(true);
        System.out.println("Welcome to Virtual Closet!");
    }

    // Effects: Prompts the user for a command and returns it
    @Override
    protected String promptInput() {
        String prompt = this.accountManager.getActiveAccount()
                .map(Account::getName)
                .map(name -> "Enter a command "
                        + name
                        + " (ex. \"help\"): ")
                .orElse(DEFAULT_INPUT_PROMPT);
        return this.getInput(prompt);
    }

    // Modifies: this
    // Effects: Marks the application to be closed when ready,
    //          and prints a message for the user to see.
    private void exit() {
        this.setShouldRun(false);
        System.out.println("Closing application ...");
    }

    // Requires: this.accountManager has an active account
    // Modifies: this
    // Effects: creates a new closet with the given name unless
    //          a closet with the name is already present
    private void createCloset() {
        String closetName = this.getInput("\tEnter closet name to create: ");
        Account active = this.accountManager.getActiveAccount().get();
        if (active.addCloset(closetName)) {
            System.out.println("\tAdded closet \"" + closetName + "\".");
        } else {
            System.out.println("\tCloset with name \"" + closetName + "\" already exists!");
        }
    }

    // Requires: this.accountManager has an active account
    // Modifies: this
    // Effects: Removes the closet with the given name and informs
    //          the user if it was removed successfully.
    private void removeCloset() {
        String closetName = this.getInput("\tEnter a closet name to remove: ");
        String verify = this.getInput("\tEnter the closet name again to confirm: ");
        Account active = this.accountManager.getActiveAccount().get();
        if (!closetName.equalsIgnoreCase(verify)) {
            System.out.println("\tNames did not match, not removing closet.");
        } else if (active.removeCloset(closetName)) {
            System.out.println("\tRemoved closet \"" + closetName + "\".");
        } else {
            System.out.println("\tCloset with name \"" + closetName + "\" does not exist!");
        }
    }

    // Modifies: this
    // Effects: Retrieves an account name from the user, then creates
    //          and stores an account with the given name. If the account
    //          name is already taken, notifies user.
    private void createAccount() {
        String accountName = this.getInput("\tEnter account name: ");
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

    // Modifies: this
    // Effects: Attempts to remove the account which the user specifies
    //          if it exists, and prints whether it was successful.
    private void removeAccount() {
        String accountName = this.getInput("\tEnter account name to delete: ");
        String verified = this.getInput("\tAre you sure? All account data will be deleted.\n"
                + "\tEnter again to confirm: ");

        if (!accountName.equalsIgnoreCase(verified)) {
            System.out.println("\tNames did not match, not deleting account.");
        } else if (this.accountManager.removeAccount(accountName)) {
            System.out.println("\tRemoved account \"" + accountName + "\".");
        } else {
            System.out.println("\tAccount \"" + accountName + "\" does not exist.");
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

    // Modifies: this
    // Effects: Sets the active account to the name of the one input by user,
    //          or informs them if the account does not exist.
    private void setActiveAccount() {
        String accountName = this.getInput("\tEnter account name: ");
        if (this.accountManager.setActiveAccount(accountName)) {
            System.out.println("\tActive account set to \"" + accountName + "\".");
        } else {
            System.out.println("\tCould not set active account to \"" + accountName + "\".");
        }
    }

    // Requires: this.accountManager has an active account
    // Effects: Lists the closets of the given active account
    private void listClosets() {
        Account a = this.accountManager.getActiveAccount().get();
        System.out.println("\tClosets: "
                + a.getClosets().stream()
                .map(Closet::getName)
                .collect(Collectors.joining(", ")));
    }

    // Requires: this.accountManager has an active account
    // Modifies: this
    // Effects: Prompts the user for a new account name,
    //          and renames the active account if no other
    //          account presently has the same name.
    private void renameActive() {
        String name = this.getInput("\tEnter new account name: ");
        String nameBefore = this.accountManager.getActiveAccount()
                .get().getName();
        if (this.accountManager.setActiveAccountName(name)) {
            System.out.println("\tAccount \"" + nameBefore
                    + "\" renamed to \"" + name + "\".");
        } else {
            System.out.println("\tCould not rename account. Does another "
                    + "account have the same name?");
        }
    }

    // Requires: this.accountManager must have an active account
    // Modifies: this
    // Effects: Enters closet mode for the given closet name provided
    //          by the user
    private void openCloset() {
        String closetName = this.getInput("\tEnter closet name: ");
        Account active = this.accountManager.getActiveAccount().get();
        if (active.hasCloset(closetName)) {
            new ClosetModeConsole(this.getInput(), active.getCloset(closetName).get());
        } else {
            System.out.println("\tCloset \"" + closetName + "\" does not exist.");
        }
    }

    // Modifies: this
    // Effects: Initializes the list of commands with the given
    //          class state.
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    private void initCommands() {
        this.addCommands(
                new ConsoleCommand(this::exit,
                        "Exits the application.",
                        "quit", "q"),
                new ConsoleCommand(this::help,
                        "Displays general information about how to use Virtual Closet.",
                        "help"),
                new ConsoleCommand(this::listAccounts,
                        "Lists the names of all accounts in the account manager.",
                        "list accounts"),
                new ConsoleCommand(this::createAccount,
                        "Creates a new account based on the info provided.",
                        "create account"),
                new ConsoleCommand(this::removeAccount,
                        () -> !this.accountManager.getAccounts().isEmpty(),
                        "No accounts!",
                        "Removes an account based on the info provided.",
                        "remove account"),
                new ConsoleCommand(this::setActiveAccount,
                        () -> !this.accountManager.getAccounts().isEmpty(),
                        "No accounts!",
                        "Sets the active account for which to perform closet commands for.",
                        "set active account", "set active"),
                new ConsoleCommand(this.accountManager::removeActiveAccount,
                        this.accountManager::hasActiveAccount,
                        "No active account!",
                        "Sets there to be no active account.",
                        "remove active"),
                new ConsoleCommand(this::createCloset,
                        this.accountManager::hasActiveAccount,
                        "No active account!",
                        "Creates a new closet for the current active account with the "
                                + "given name.",
                        "create closet"),
                new ConsoleCommand(this::removeCloset,
                        this.accountManager::hasActiveAccount,
                        "No active account!",
                        "Removes the closet with the given name.",
                        "remove closet"),
                new ConsoleCommand(this::listClosets,
                        () -> this.accountManager.hasActiveAccount()
                                && !this.accountManager.getActiveAccount()
                                .get().getClosets().isEmpty(),
                        "No active account, or no closets!",
                        "Lists the closets for the given active account.",
                        "list closets"),
                new ConsoleCommand(this::renameActive,
                        this.accountManager::hasActiveAccount,
                        "No active account!",
                        "Renames the active account if another account does not "
                                + "presently have the same name.",
                        "rename active"),
                new ConsoleCommand(this::openCloset,
                        this.accountManager::hasActiveAccount,
                        "No active account!",
                        "Enters closet mode for the given closet.",
                        "open closet")
                // TODO: Enter catalogue mode
        );
    }
}
