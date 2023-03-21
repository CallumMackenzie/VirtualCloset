package ui.console;

import model.Account;
import model.AccountManager;
import model.Closet;

import java.io.IOException;
import java.util.Scanner;
import java.util.stream.Collectors;

// Virtual Closet console application
public final class VirtualClosetConsoleApp extends CommandSystem {

    private static final String DEFAULT_INPUT_PROMPT = "Enter a command (ex. \"help\"): ";

    private AccountManager accountManager;

    // EFFECTS: Runs the Virtual Closet console application
    public VirtualClosetConsoleApp() {
        super(new DynamicScanner(new Scanner(System.in)));
        this.run();
        this.getInput().getScanner().close();
    }

    // MODIFIES: this
    // EFFECTS: Sets up the scanner, account manager, and marks the application
    //          to run.
    @Override
    protected void init() {
        this.accountManager = new AccountManager();
        this.initCommands();
        this.setShouldRun(true);
        this.load();
        System.out.println("Welcome to Virtual Closet!");
    }

    // EFFECTS: Prompts the user for a command and returns it
    @Override
    protected String promptInput() {
        if (this.accountManager.hasActiveAccount()) {
            return this.getInput("Enter a command "
                    + this.accountManager.getActiveAccount().getName()
                    + " (ex. \"help\"): ");
        } else {
            return this.getInput(DEFAULT_INPUT_PROMPT);
        }
    }

    // MODIFIES: this
    // EFFECTS: Marks the application to be closed when ready,
    //          and prints a message for the user to see.
    @Override
    protected void stop() {
        super.stop();
        this.save();
        System.out.println("Closing application ...");
    }

    // REQUIRES: this.accountManager has an active account
    // MODIFIES: this
    // EFFECTS: creates a new closet with the given name unless
    //          a closet with the name is already present
    private void createCloset() {
        String closetName = this.getInput("\tEnter closet name to create: ");
        Account active = this.accountManager.getActiveAccount();
        if (active.addCloset(closetName)) {
            System.out.println("\tAdded closet \"" + closetName + "\".");
        } else {
            System.out.println("\tCloset with name \"" + closetName + "\" already exists!");
        }
    }

    // REQUIRES: this.accountManager has an active account
    // MODIFIES: this
    // EFFECTS: Removes the closet with the given name and informs
    //          the user if it was removed successfully.
    private void removeCloset() {
        String closetName = this.getInput("\tEnter a closet name to remove: ");
        String verify = this.getInput("\tEnter the closet name again to confirm: ");
        Account active = this.accountManager.getActiveAccount();
        if (!closetName.equalsIgnoreCase(verify)) {
            System.out.println("\tNames did not match, not removing closet.");
        } else if (active.removeCloset(closetName)) {
            System.out.println("\tRemoved closet \"" + closetName + "\".");
        } else {
            System.out.println("\tCloset with name \"" + closetName + "\" does not exist!");
        }
    }

    // MODIFIES: this
    // EFFECTS: Retrieves an account name from the user, then creates
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

    // MODIFIES: this
    // EFFECTS: Attempts to remove the account which the user specifies
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

    // EFFECTS: Prints a list of all accounts in the account manager
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

    // MODIFIES: this
    // EFFECTS: Sets the active account to the name of the one input by user,
    //          or informs them if the account does not exist.
    private void setActiveAccount() {
        String accountName = this.getInput("\tEnter account name: ");
        if (this.accountManager.setActiveAccount(accountName)) {
            System.out.println("\tActive account set to \"" + accountName + "\".");
        } else {
            System.out.println("\tCould not set active account to \"" + accountName + "\".");
        }
    }

    // REQUIRES: this.accountManager has an active account
    // EFFECTS: Lists the closets of the given active account
    private void listClosets() {
        Account a = this.accountManager.getActiveAccount();
        System.out.println("\tClosets: "
                + a.getClosets().stream()
                .map(Closet::getName)
                .collect(Collectors.joining(", ")));
    }

    // REQUIRES: this.accountManager has an active account
    // MODIFIES: this
    // EFFECTS: Prompts the user for a new account name,
    //          and renames the active account if no other
    //          account presently has the same name.
    private void renameActive() {
        String name = this.getInput("\tEnter new account name: ");
        String nameBefore = this.accountManager.getActiveAccount().getName();
        if (this.accountManager.setActiveAccountName(name)) {
            System.out.println("\tAccount \"" + nameBefore
                    + "\" renamed to \"" + name + "\".");
        } else {
            System.out.println("\tCould not rename account. Does another "
                    + "account have the same name?");
        }
    }

    // REQUIRES: this.accountManager must have an active account
    // MODIFIES: this
    // EFFECTS: Enters closet mode for the given closet name provided
    //          by the user
    private void openCloset() {
        String closetName = this.getInput("\tEnter closet name: ");
        Account active = this.accountManager.getActiveAccount();
        if (active.hasCloset(closetName)) {
            // This assertion is guaranteed by the check to active.hasCloset; this is to suppress the warning
            // and ensure the correct behavior of active.hasCloset
            assert active.getCloset(closetName).isPresent() : "Active account did not have the required closet!";
            new ClosetModeConsole(this.getInput(),
                    active.getCloset(closetName).get());
        } else {
            System.out.println("\tCloset \"" + closetName + "\" does not exist.");
        }
    }

    // EFFECTS: Saves data to disk
    private void save() {
        System.out.println("Saving to disk.");
        try {
            this.accountManager.saveState();
            System.out.println("Saved.");
        } catch (IOException e) {
            System.out.println("Failed to save data to disk.");
        }
    }

    // MODIFIES: this
    // EFFECTS: Loads data from disk
    private void load() {
        System.out.println("Loading data from disk.");
        try {
            this.accountManager.loadState();
            System.out.println("Loaded.");
        } catch (IOException e) {
            System.out.println("Failed to load data from disk.");
        }
    }

    // REQUIRES: this.accountManager must have an active acccount
    // MODIFIES: this
    // EFFECTS: Enters catalogue mode
    private void openCatalogue() {
        Account active = this.accountManager.getActiveAccount();
        new CatalogueModeConsole(this.getInput(), active);
    }

    // REQUIRES: this.initCommands has not been called already
    // MODIFIES: this
    // EFFECTS: Initializes the list of commands with the given
    //          class state.
    @Override
    protected void initCommands() {
        this.initBasicCommands(
                "Displays general information about how to use Virtual Closet.",
                "Exits the application.", "quit");
        this.initAccountCommands();
        this.initActiveAccountCommands();
        this.initClosetCommands();
        this.initCatalogueCommands();
    }

    // REQUIRES: this.initActiveAccountCommands has not been called already
    // MODIFIES: this
    // EFFECTS: Sets up commands for active accounts such as renaming,
    //          setting active, removing active, etc
    private void initActiveAccountCommands() {
        this.addCommands(new ConsoleCommand(this::setActiveAccount, () ->
                        !this.accountManager.getAccounts().isEmpty(),
                        "No accounts!",
                        "Sets the active account for which to perform closet commands for.",
                        "set active"),
                new ConsoleCommand(this.accountManager::removeActiveAccount,
                        this.accountManager::hasActiveAccount,
                        "No active account!",
                        "Sets there to be no active account.",
                        "remove active"),
                new ConsoleCommand(this::renameActive,
                        this.accountManager::hasActiveAccount,
                        "No active account!",
                        "Renames the active account if another account does not "
                                + "presently have the same name.",
                        "rename active"));
    }

    // REQUIRES: this.initAccountCommands has not been called already
    // MODIFIES: this
    // EFFECTS: Sets up commands for accounts such as listing, creating,
    //          and removing
    private void initAccountCommands() {
        this.addCommands(new ConsoleCommand(this::listAccounts,
                        "Lists the names of all accounts in the account manager.",
                        "accounts"),
                new ConsoleCommand(this::createAccount,
                        "Creates a new account based on the info provided.",
                        "new account"),
                new ConsoleCommand(this::removeAccount,
                        () -> !this.accountManager.getAccounts().isEmpty(),
                        "No accounts!",
                        "Removes an account based on the info provided.",
                        "remove account"),
                new ConsoleCommand(this::save,
                        "Saves application state to file.",
                        "save"),
                new ConsoleCommand(this::load,
                        "Loads application state from file",
                        "load"));
    }

    // REQUIRES: this.initClosetCommands has not been called already
    // MODIFIES: this
    // EFFECTS: Sets up closet commands for creating, removing,
    //          opening, etc. closets
    private void initClosetCommands() {
        this.addCommands(new ConsoleCommand(this::createCloset,
                        this.accountManager::hasActiveAccount,
                        "No active account!",
                        "Creates a new closet for the current active account with the given name.",
                        "new closet"),
                new ConsoleCommand(this::removeCloset,
                        this.accountManager::hasActiveAccount,
                        "No active account!",
                        "Removes the closet with the given name.",
                        "remove closet"),
                new ConsoleCommand(this::listClosets,
                        () -> this.accountManager.getActiveAccount() == null
                                || this.accountManager.getActiveAccount()
                                .getClosets().isEmpty(),
                        "No active account, or no closets!",
                        "Lists the closets for the given active account.",
                        "closets"),
                new ConsoleCommand(this::openCloset,
                        this.accountManager::hasActiveAccount,
                        "No active account!",
                        "Enters closet mode for the given closet.",
                        "open closet"));
    }

    // REQUIRES: this.initCatalogueCommands has not been called already
    // MODIFIES: this
    // EFFECTS: Sets up catalogue commands
    private void initCatalogueCommands() {
        this.addCommands(new ConsoleCommand(this::openCatalogue,
                this.accountManager::hasActiveAccount,
                "No active account!",
                "Opens the catalogue for the active user.",
                "catalogue"));
    }
}
