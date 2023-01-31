package model;

import java.util.ArrayList;
import java.util.List;

// A user account with a closet, clothing catalogue, and name
public class Account {

    private String name;
    private final Catalogue catalogue;
    private final List<Closet> closets;

    // Effects: Constructs a new account with the given name
    public Account(String name) {
        this.name = name;
        this.catalogue = new Catalogue();
        this.closets = new ArrayList<>();
    }

    // Effects: Returns the name of this account
    public String getName() {
        return this.name;
    }

    // Requires: allAccounts is not null
    // Modifies: this
    // Effects: Sets the name of this account and returns true, if no account in
    //          allAccounts that is not this one has the same name. If one does,
    //          the method returns false.
    public boolean setName(String name, List<Account> allAccounts) {
        for (Account a : allAccounts) {
            if (a != this && a.getName().equals(name)) {
                return false;
            }
        }
        this.name = name;
        return true;
    }

    // Effects: Returns the catalogue for this account
    public Catalogue getCatalogue() {
        return this.catalogue;
    }

    // Effects: Returns the closets in this account
    public List<Closet> getClosets() {
        return  this.closets;
    }

}
