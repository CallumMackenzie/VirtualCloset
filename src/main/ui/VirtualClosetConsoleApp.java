package ui;

import model.Account;
import model.AccountManager;
import model.Closet;

import java.util.Arrays;
import java.util.List;
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
        System.out.println("Welcome to Virtual Closet!");
    }

    // EFFECTS: Prompts the user for a command and returns it
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

    // MODIFIES: this
    // EFFECTS: Marks the application to be closed when ready,
    //          and prints a message for the user to see.
    private void exit() {
        this.setShouldRun(false);
        System.out.println("Closing application ...");
    }

    // REQUIRES: this.accountManager has an active account
    // MODIFIES: this
    // EFFECTS: creates a new closet with the given name unless
    //          a closet with the name is already present
    private void createCloset() {
        assert this.accountManager.getActiveAccount().isPresent() : "No active account present!";
        String closetName = this.getInput("\tEnter closet name to create: ");
        Account active = this.accountManager.getActiveAccount().get();
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
        assert this.accountManager.getActiveAccount().isPresent() : "No active account present!";
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
        assert this.accountManager.getActiveAccount().isPresent() : "No active account present!";
        Account a = this.accountManager.getActiveAccount().get();
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
        assert this.accountManager.getActiveAccount().isPresent() : "No active account present!";
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

    // REQUIRES: this.accountManager must have an active account
    // MODIFIES: this
    // EFFECTS: Enters closet mode for the given closet name provided
    //          by the user
    private void openCloset() {
        assert this.accountManager.getActiveAccount().isPresent() : "No active account present!";

        String closetName = this.getInput("\tEnter closet name: ");
        Account active = this.accountManager.getActiveAccount().get();
        if (active.hasCloset(closetName)) {
            // This assertion is guaranteed by the check to active.hasCloset; this is to suppress the warning
            // and ensure the correct behavior of active.hasCloset
            assert active.getCloset(closetName).isPresent() : "Active account did not have the required closet!";
            ClosetModeConsole c = new ClosetModeConsole(this.getInput(),
                    active.getCloset(closetName).get());
        } else {
            System.out.println("\tCloset \"" + closetName + "\" does not exist.");
        }
    }

    // MODIFIES: this
    // EFFECTS: Initializes the list of commands with the given
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
                        () -> this.accountManager.getActiveAccount()
                                .map(Account::getClosets)
                                .map(l -> !l.isEmpty())
                                .orElse(false),
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
                        "open closet"),
                // TODO: Enter catalogue mode

                // TODO: For debugging, remove later
                new ConsoleCommand(() -> {
                    String cmds = String.join("\n",
                            Arrays.asList("create account",
                                    "Callum",
                                    "create closet",
                                    "c1",
                                    "open closet",
                                    "c1",
                                    "new", "pants", "xl", "adidas", "cotton", "casual, sweatpants", "no",
                                    "new", "shirt", "l", "uniqlo", "synthetic", "casual, smooth, oversize", "no",
                                    "new", "pants", "xl", "under armor", "cotton", "casual, sweatpants", "yes",
                                    "new", "pants", "xl", "uniqlo", "cotton", "semicasual, cargos", "no",
                                    "new", "shirt", "l", "under armor", "polyester", "gym, workout, casual", "no",
                                    "new", "shorts", "xl", "youngla", "cotton", "gym, workout", "no",
                                    "new", "sweater", "xl", "ubc", "cotton blend", "casual, outdoor", "no",
                                    "help")) + "\n";
                    this.getInput().addScanner(new Scanner(cmds));
                }, "", "dbg")
        );
    }
}
