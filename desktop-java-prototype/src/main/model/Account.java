package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistance.JsonBuilder;
import persistance.Savable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// A user account with a closet, clothing catalogue, and name
public class Account implements Savable<Void> {

    public static final String JSON_CATALOGUE_KEY = "catalogue";
    public static final String JSON_CLOSETS_KEY = "closets";
    public static final String JSON_NAME_KEY = "name";
    public static final String JSON_ALL_CLOTHING_KEY = "allClothing";

    private final Catalogue catalogue;
    private final List<Closet> closets;
    private String name;

    // EFFECTS: Constructs a new account with the given name
    public Account(String name) {
        this(name, new Catalogue(), new ArrayList<>());
    }

    // REQUIRES: closets is mutable
    // EFFECTS: Creates an account with the given catalogue, closets,
    //          and name.
    private Account(String name, Catalogue c, List<Closet> closets) {
        this.name = name;
        this.catalogue = c;
        this.closets = closets;
    }

    // REQUIRES: jso was created by this.toJson
    // EFFECTS: Returns an instance of this object reconstructed from JSON
    public static Account fromJson(JSONObject jso) {
        JSONArray allClothingJsa = jso.getJSONArray(JSON_ALL_CLOTHING_KEY);
        List<Clothing> allClothing = new ArrayList<>(allClothingJsa.length());
        for (int i = 0; i < allClothingJsa.length(); ++i) {
            JSONObject clothingJs = allClothingJsa.getJSONObject(i);
            allClothing.add(Clothing.fromJson(clothingJs));
        }
        Catalogue c = Catalogue.fromJson(jso.getJSONObject(JSON_CATALOGUE_KEY),
                allClothing);
        String name = jso.getString(JSON_NAME_KEY);
        JSONArray closetsJs = jso.getJSONArray(JSON_CLOSETS_KEY);
        List<Closet> closets = new ArrayList<>(closetsJs.length());
        for (int i = 0; i < closetsJs.length(); ++i) {
            JSONObject closetJs = closetsJs.getJSONObject(i);
            closets.add(Closet.fromJson(closetJs, allClothing));
        }
        return new Account(name, c, closets);
    }

    // EFFECTS: Returns the name of this account
    public String getName() {
        return this.name;
    }

    // REQUIRES: allAccounts is not null
    // MODIFIES: this
    // EFFECTS: Sets the name of this account and returns true, if no account in
    //          allAccounts that is not this one has the same name. If one does,
    //          the method returns false.
    public boolean setName(String name, Collection<Account> allAccounts) {
        for (Account a : allAccounts) {
            if (a != this && a.getName().equals(name)) {
                EventLog.getInstance().logEvent(new Event(
                        "Account: Could not set name "
                                + this.name + " to " + name + "."
                ));
                return false;
            }
        }
        this.name = name;
        EventLog.getInstance().logEvent(new Event(
                "Account: Set name (" + this.name + ") to " + name + "."
        ));
        return true;
    }

    // MODIFIES: this
    // EFFECTS: Adds a new closet with the given name to the
    //          list of closets if the name is not already taken.
    //          Returns true if it was added, false otherwise.
    public boolean addCloset(String name) {
        if (this.closets.stream()
                .anyMatch(n -> n.getName().equalsIgnoreCase(name))) {
            EventLog.getInstance().logEvent(new Event(
                    "Account: Could not add closet "
                            + name + " to " + this.name + "."
            ));
            return false;
        }
        this.closets.add(new Closet(name));
        EventLog.getInstance().logEvent(new Event(
                "Account: Added closet " + name + " to " + this.name + "."
        ));
        return true;
    }

    // MODIFIES: this
    // EFFECTS: Removes the closet with the given name if it is
    //          present. Returns true if it was removed, false if
    //          it was not present.
    public boolean removeCloset(String name) {
        EventLog.getInstance().logEvent(new Event(
                "Account: Attempting to remove Closet "
                        + name + " from " + this.name + "."
        ));
        int lengthBefore = this.closets.size();
        this.closets.removeIf(c -> c.getName().equalsIgnoreCase(name));
        return lengthBefore != this.closets.size();
    }

    // EFFECTS: Returns true if there is a closet matching the given
    //          name in this account, false otherwise.
    public boolean hasCloset(String name) {
        return this.closets.stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(name));
    }

    // EFFECTS: Returns the named closet if present, Optional.empty() otherwise
    public Optional<Closet> getCloset(String name) {
        return this.closets.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    // EFFECTS: Compares the equality of this vs object
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Account)) {
            return false;
        }
        return ((Account) obj).getName().equals(this.getName());
    }

    // EFFECTS: Returns the catalogue for this account
    public Catalogue getCatalogue() {
        return this.catalogue;
    }

    // EFFECTS: Returns the closets in this account
    public List<Closet> getClosets() {
        return this.closets;
    }

    // EFFECTS: Returns a JSON representation of this object
    @Override
    public JSONObject toJson(Void unused) {
        List<Clothing> allClothing = new ArrayList<>(
                this.closets.stream().map(c -> c.getClothing().size())
                        .reduce(0, Integer::sum));
        for (Closet closet : this.closets) {
            allClothing.addAll(closet.getClothing());
        }
        allClothing = allClothing.stream().sorted().collect(Collectors.toList());
        return new JsonBuilder()
                .savable(JSON_CATALOGUE_KEY, this.catalogue, allClothing)
                .savable(JSON_CLOSETS_KEY, this.closets, allClothing)
                .savable(JSON_ALL_CLOTHING_KEY, allClothing, null)
                .put(JSON_NAME_KEY, this.name);
    }
}
