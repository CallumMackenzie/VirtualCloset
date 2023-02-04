package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Manages account data in memory, has an active account and a list of
// all accounts.
public class AccountManager implements Serializable {

    private Account activeAccount;
    private final List<Account> accounts;

    // Effects: Constructs a new account manager without any
    //          accounts or an active one.
    public AccountManager() {
        this.accounts = new ArrayList<>();
    }

    // Effects: Returns whether there is an active account or not
    public boolean hasActiveAccount() {
        return this.activeAccount != null;
    }

    // Effects: Returns account wrapped in optional if present,
    //          otherwise returns Optional.empty().
    public Optional<Account> getActiveAccount() {
        if (this.hasActiveAccount()) {
            return Optional.of(this.activeAccount);
        }
        return Optional.empty();
    }

    // Effects: Returns true if the given account name is
    //          currently taken.
    private boolean accountExists(String accountName) {
        return this.getAccount(accountName) != null;
    }

    // Effects: Finds an account by name. If it is not found,
    //          returns null.
    public Account getAccount(String name) {
        for (Account a : this.getAccounts()) {
            if (a.getName().equalsIgnoreCase(name)) {
                return a;
            }
        }
        return null;
    }

    // Modifies: this
    // Effects: If the given account name is found in the list of
    //          accounts managed, it will set that to be the active account
    //          and return true. Otherwise, it will return false.
    public boolean setActiveAccount(String accountName) {
        Account ac = this.getAccount(accountName);
        if (ac != null) {
            this.activeAccount = ac;
        }
        return ac != null;
    }

    // Modifies: this
    // Effects: Removes the active account if present, otherwise
    //          does nothing
    public void removeActiveAccount() {
        this.activeAccount = null;
    }

    // Modifies: this
    // Effects: Sets the current account name if no other account
    //          has the same name and returns true, otherwise it
    //          does not set and returns false.
    public boolean setActiveAccountName(String name) {
        if (!this.hasActiveAccount()) {
            return false;
        }
        return this.activeAccount.setName(name, this.accounts);
    }

    // Modifies: this
    // Effects: Adds the given account to the list of accounts
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

    // Modifies: this
    // Effects: Removes the given account if it is tracked,
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

    // Effects: Returns the accounts this system manages
    public List<Account> getAccounts() {
        return this.accounts;
    }

}
