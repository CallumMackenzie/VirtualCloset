package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// A closet having a list of clothing
public class Closet {

    private final List<Clothing> clothing;
    private final String name;
    private final Map<Brand, List<Clothing>> brandMap;

    // Effects: Constructs a new closet with no clothing
    public Closet(String name) {
        this.clothing = new ArrayList<>();
        this.brandMap = new HashMap<>();
        this.name = name;
    }

    // Effects: Returns the name of this closet
    public String getName() {
        return this.name;
    }

    // Effects: Returns the clothing in this closet
    public List<Clothing> getClothing() {
        return this.clothing;
    }

    // Modifies: this
    // Effects: Adds the given clothing to this closet
    public void addClothing(Clothing clothing) {
        this.clothing.add(clothing);
    }

}
