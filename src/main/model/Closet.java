package model;

import model.search.ClothingAddress;

import java.util.*;
import java.util.stream.Collectors;

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
    public List<Clothing> findClothing(ClothingAddress address) {
        switch (address.getSearchMode()) {
            case EXACT:
                return this.findClothingExact(address);
            case SORTED_ALL_SIMILAR:
                return null;
            default:
                break;
        }
        throw new RuntimeException("Unreachable statement");
    }

    // REQUIRES: address must have searchMode of SearchMode.EXACT
    // EFFECTS: Returns exact matches in this closet for this given address
    public List<Clothing> findClothingExact(ClothingAddress address) {
        // TODO
        Map<Clothing, Integer> matchMap = new HashMap<>();
        for (String brand : address.getBrands()) {
            this.brandMap.get(brand).forEach(c -> {
                if (matchMap.containsKey(c)) {
                    matchMap.put(c, matchMap.get(c) + 1);
                } else {
                    matchMap.put(c, 1);
                }
            });
        }
        return matchMap.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
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
