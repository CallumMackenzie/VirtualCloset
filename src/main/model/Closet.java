package model;

import java.util.*;

// A closet having a list of clothing
public class Closet {

    private final List<Clothing> clothing;
    private final String name;
    private final HashMap<String, List<Clothing>> brandMap;
    private final Map<String, List<Clothing>> typeMap;

    // Effects: Constructs a new closet with no clothing
    public Closet(String name) {
        this.clothing = new ArrayList<>();
        this.brandMap = new HashMap<>();
        this.typeMap = new HashMap<>();
        this.name = name;
    }

    // TODO
    public Optional<List<Clothing>> findClothing(ClothingAddress address) {
        // TODO
        return null;
    }

    // Effects: Returns the name of this closet
    public String getName() {
        return this.name;
    }

    // Effects: Returns the clothing in this closet
    public List<Clothing> getClothing() {
        return this.clothing;
    }

    // Effects: Returns the types of clothing present in this closet
    public Set<String> getTypes() {
        return this.typeMap.keySet();
    }

    // Effects: Returns the brands present in this closet
    public Set<String> getBrands() {
        return this.brandMap.keySet();
    }

    // Modifies: this
    // Effects: Adds the given clothing to this closet
    public void addClothing(Clothing clothing) {
        this.clothing.add(clothing);

        // Add to brand map
        if (!this.brandMap.containsKey(clothing.getBrand())) {
            this.brandMap.put(clothing.getBrand(), new ArrayList<>());
        }
        this.brandMap.get(clothing.getBrand()).add(clothing);

        // Add to type map
        clothing.getTypes().forEach(t -> {
            if (!this.typeMap.containsKey(t)) {
                this.typeMap.put(t, new ArrayList<>());
            }
            this.typeMap.get(t).add(clothing);
        });
    }

}
