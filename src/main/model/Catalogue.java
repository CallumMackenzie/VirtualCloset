package model;

import java.util.ArrayList;
import java.util.List;

// A catalogue of outfits having a list of outfits
public class Catalogue {

    private final List<Outfit> outfits;

    // EFFECTS: Creates a new catalogue with no outfits
    public Catalogue() {
        this.outfits = new ArrayList<>();
    }

    // EFFECTS: Returns all the outfits in this catalogue
    public List<Outfit> getOutfits() {
        return this.outfits;
    }

    // MODIFIES: this
    // EFFECTS: Adds the given outfit to the catalogue.
    public void addOutfit(Outfit outfit) {
        this.outfits.add(outfit);
    }
}
