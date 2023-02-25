package model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

// An outfit with a list of clothing and a date last modified
public class Outfit {

    private final List<Clothing> clothing;
    private Instant lastModified;

    // EFFECTS: Creates a new article of clothing which has now
    //          as the last modified time.
    public Outfit(List<Clothing> clothing) {
        this.clothing = new ArrayList<>(clothing);
        this.lastModified = Instant.now();
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
}
