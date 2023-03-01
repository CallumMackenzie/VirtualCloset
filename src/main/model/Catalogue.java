package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistance.JsonBuilder;
import persistance.Savable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// A catalogue of outfits having a list of outfits
public class Catalogue implements Savable<List<Clothing>> {

    public static final String JSON_OUTFITS_KEY = "outfits";

    private final List<Outfit> outfits;

    // EFFECTS: Creates a new catalogue with no outfits
    public Catalogue() {
        this(new ArrayList<>());
    }

    // REQUIRES: outfits must be a mutable list
    // EFFECTS: Creates a new catalogue with the given outfits
    public Catalogue(List<Outfit> outfits) {
        this.outfits = outfits;
    }

    // EFFECTS: Returns all the outfits in this catalogue
    public List<Outfit> getOutfits() {
        return this.outfits;
    }

    // EFFECTS: Returns the list of outfits matching the given name
    public List<Outfit> getOutfitsByName(String name) {
        return this.getOutfits().stream()
                .filter(o -> o.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    // MODIFIES: this
    // EFFECTS: Removes outfits matching the given name
    public void removeAllWithName(String name) {
        this.outfits.removeIf(x -> x.getName().equalsIgnoreCase(name));
    }

    // MODIFIES: this
    // EFFECTS: Adds the given outfit to the catalogue.
    public void addOutfit(Outfit outfit) {
        this.outfits.add(outfit);
    }

    // REQUIRES: allClothing is sorted
    // EFFECTS: Returns a JSON representation of this object
    @Override
    public JSONObject toJson(List<Clothing> allClothing) {
        return new JsonBuilder()
                .savable(JSON_OUTFITS_KEY, this.outfits, allClothing);
    }

    // REQUIRES: allClothing is sorted, jso was created with this.toJson
    // EFFECTS: Returns an instance of this class from the given JSON
    public static Catalogue fromJson(JSONObject jso, List<Clothing> allClothing) {
        JSONArray jsa = jso.getJSONArray(JSON_OUTFITS_KEY);
        List<Outfit> outfits = new ArrayList<>(jsa.length());
        for (int i = 0; i < jsa.length(); ++i) {
            JSONObject outfitJsonObj = jsa.getJSONObject(i);
            outfits.add(Outfit.fromJson(outfitJsonObj, allClothing));
        }
        return new Catalogue(outfits);
    }
}
