package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistance.JsonBuilder;
import persistance.JsonReader;
import persistance.JsonWriter;
import persistance.Savable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Manages account data in memory, has an active account and a list of
// all accounts.
public class AccountManager implements Savable<Void> {

    public static final String FILE_SAVE_PATH = "./data/data.json";
    public static final String JSON_ACCOUNTS_KEY = "accounts";

    private final List<Account> accounts;
    private Account activeAccount;
    private String fileSavePath;

    // EFFECTS: Constructs a new account manager without any
    //          accounts or an active one.
    public AccountManager() {
        this(new ArrayList<>());
    }

    // REQUIRES: accounts is internally mutable.
    // EFFECTS: Constructs a new account manager for the given accounts
    //          with a default file save path.
    public AccountManager(List<Account> accounts) {
        this.accounts = accounts;
        this.fileSavePath = FILE_SAVE_PATH;
    }

    // EFFECTS: Returns an instance of this object from the given JSON
    public static AccountManager fromJson(JSONObject jso) {
        JSONArray jsa = jso.getJSONArray(JSON_ACCOUNTS_KEY);
        List<Account> accounts = new ArrayList<>(jsa.length());
        for (int i = 0; i < jsa.length(); ++i) {
            JSONObject accountJs = jsa.getJSONObject(i);
            accounts.add(Account.fromJson(accountJs));
        }
        return new AccountManager(accounts);
    }

    // EFFECTS: Returns whether there is an active account or not
    public boolean hasActiveAccount() {
        return this.activeAccount != null;
    }

    // EFFECTS: Returns the file save path for this account manager
    public String getFileSavePath() {
        return this.fileSavePath;
    }

    // MODIFIES: this
    // EFFECTS: Sets the file save path for this account manager.
    public void setFileSavePath(String fileSavePath) {
        this.fileSavePath = fileSavePath;
    }

    // EFFECTS: Returns account wrapped in optional if present,
    //          otherwise returns null.
    public Account getActiveAccount() {
        if (this.hasActiveAccount()) {
            return this.activeAccount;
        }
        return null;
    }

    // EFFECTS: Returns true if the given account name is
    //          currently taken.
    private boolean accountExists(String accountName) {
        return this.getAccount(accountName) != null;
    }

    // EFFECTS: Finds an account by name. If it is not found,
    //          returns null.
    public Account getAccount(String name) {
        for (Account a : this.getAccounts()) {
            if (a.getName().equalsIgnoreCase(name)) {
                return a;
            }
        }
        return null;
    }

    // MODIFIES: this
    // EFFECTS: If the given account name is found in the list of
    //          accounts managed, it will set that to be the active account
    //          and return true. Otherwise, it will return false.
    public boolean setActiveAccount(String accountName) {
        Account ac = this.getAccount(accountName);
        if (ac != null) {
            this.activeAccount = ac;
        }
        return ac != null;
    }

    // MODIFIES: this
    // EFFECTS: Removes the active account if present, otherwise
    //          does nothing
    public void removeActiveAccount() {
        this.activeAccount = null;
    }

    // MODIFIES: this
    // EFFECTS: Sets the current account name if no other account
    //          has the same name and returns true, otherwise it
    //          does not set and returns false.
    public boolean setActiveAccountName(String name) {
        if (!this.hasActiveAccount()) {
            return false;
        }
        return this.activeAccount.setName(name, this.accounts);
    }

    // MODIFIES: this
    // EFFECTS: Adds the given account to the list of accounts
    //          managed and returns true if the given account
    //          name is not already taken. Otherwise, returns false
    //          and does not add account.
    public boolean addAccount(Account account) {
        if (this.accountExists(account.getName())) {
            return false;
        } else {
            this.accounts.add(account);
            return true;
        }
    }

    // MODIFIES: this
    // EFFECTS: Removes the given account if it is tracked,
    //          returns whether it was removed or not.
    public boolean removeAccount(String accountName) {
        if (this.accountExists(accountName)) {
            this.accounts.removeIf(account ->
                    account.getName().equalsIgnoreCase(accountName));
            if (this.hasActiveAccount()
                    && this.activeAccount.getName()
                    .equalsIgnoreCase(accountName)) {
                this.removeActiveAccount();
            }
            return true;
        }
        return false;
    }

    // EFFECTS: Returns the accounts this system manages
    public List<Account> getAccounts() {
        return this.accounts;
    }

    // EFFECTS: Saves state to file.
    public void saveState() throws IOException {
        JsonWriter jsw = new JsonWriter(this.fileSavePath);
        jsw.write(this, null);
    }

    // MODIFIES: this
    // EFFECTS: Loads the state from file.
    public void loadState() throws IOException {
        JsonReader jsr = new JsonReader(this.fileSavePath);
        JSONObject o = jsr.readFileJson();
        this.accounts.clear();
        AccountManager copy = AccountManager.fromJson(o);
        this.accounts.addAll(copy.accounts);
    }

    // EFFECTS: Returns a JSON representation of this object
    @Override
    public JSONObject toJson(Void unused) {
        return new JsonBuilder()
                .savable(JSON_ACCOUNTS_KEY, this.accounts, null);
    }
}
