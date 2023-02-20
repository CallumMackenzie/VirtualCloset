package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

// A user account with a closet, clothing catalogue, and name
public class Account {

    private final Catalogue catalogue;
    private final List<Closet> closets;
    private String name;

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
    public boolean setName(String name, Collection<Account> allAccounts) {
        for (Account a : allAccounts) {
            if (a != this && a.getName().equals(name)) {
                return false;
            }
        }
        this.name = name;
        return true;
    }

    // Modifies: this
    // Effects: Adds a new closet with the given name to the
    //          list of closets if the name is not already taken.
    //          Returns true if it was added, false otherwise.
    public boolean addCloset(String name) {
        if (this.closets.stream()
                .anyMatch(n -> n.getName().equalsIgnoreCase(name))) {
            return false;
        }
        this.closets.add(new Closet(name));
        return true;
    }

    // Modifies: this
    // Effects: Removes the closet with the given name if it is
    //          present. Returns true if it was removed, false if
    //          it was not present.
    public boolean removeCloset(String name) {
        int lengthBefore = this.closets.size();
        this.closets.removeIf(c -> c.getName().equalsIgnoreCase(name));
        return lengthBefore != this.closets.size();
    }

    // Effects: Returns true if there is a closet matching the given
    //          name in this account, false otherwise.
    public boolean hasCloset(String name) {
        return this.closets.stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(name));
    }

    // Effects: Returns the named closet if present, Optional.empty() otherwise
    public Optional<Closet> getCloset(String name) {
        return this.closets.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    // Effects: Returns the catalogue for this account
    public Catalogue getCatalogue() {
        return this.catalogue;
    }

    // Effects: Returns the closets in this account
    public List<Closet> getClosets() {
        return this.closets;
    }

}
