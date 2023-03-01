package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistance.JsonBuilder;
import persistance.Savable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// An outfit with a list of clothing, a name, and a date last modified
public class Outfit implements Savable<List<Clothing>> {

    public static final String JSON_CLOTHING_KEY = "clothing";
    public static final String JSON_NAME_KEY = "name";
    public static final String JSON_LAST_MODIFIED_KEY = "modified";

    private final List<Clothing> clothing;
    private String name;
    private Instant lastModified;

    // EFFECTS: Creates a new article of clothing which has now
    //          as the last modified time.
    public Outfit(String name, List<Clothing> clothing) {
        this.clothing = new ArrayList<>(clothing);
        this.name = name;
        this.lastModified = Instant.now();
    }

    // EFFECTS: Returns the name of this outfit
    public String getName() {
        return this.name;
    }

    // MODIFIES: this
    // EFFECTS: Sets the name of this outfit
    public void setName(String name) {
        this.name = name;
    }

    // MODIFIES: this
    // EFFECTS: Adds the given clothing to the outfit, setting
    //          now as the last time modified.
    public void addClothing(Clothing clothing) {
        this.clothing.add(clothing);
        this.lastModified = Instant.now();
    }

    // EFFECTS: Returns the clothing in this outfit
    public List<Clothing> getClothing() {
        return this.clothing;
    }

    // MODIFIES: this
    // EFFECTS: Removes the given clothing from this outfit, returning
    //          true if successful, false otherwise. Sets instant modified
    //          if a modification was made.
    public boolean removeClothing(Clothing clothing) {
        if (this.clothing.remove(clothing)) {
            this.lastModified = Instant.now();
            return true;
        }
        return false;
    }


    // EFFECTS: Returns the date last modified for this clothing.
    public Instant getInstantLastModified() {
        return this.lastModified;
    }

    // REQUIRES: allClothing is sorted
    // EFFECTS: Returns a JSON representation of this object
    @Override
    public JSONObject toJson(List<Clothing> allClothing) {
        JSONArray clothingIndexesJSON = new JSONArray();
        this.clothing.stream()
                .mapToInt(c -> Collections.binarySearch(allClothing, c))
                .forEach(clothingIndexesJSON::put);
        return new JsonBuilder()
                .put(JSON_CLOTHING_KEY, clothingIndexesJSON)
                .put(JSON_NAME_KEY, this.name)
                .put(JSON_LAST_MODIFIED_KEY, this.lastModified.getNano());
    }
}
