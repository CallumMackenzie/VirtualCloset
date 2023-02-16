package model;

import model.search.ClothingAddress;

import java.util.*;
import java.util.stream.Collectors;

// A closet having a list of clothing and various categorizations
// of said clothing
public class Closet {

    private final List<Clothing> clothing;
    private final String name;
    private final HashMap<String, List<Clothing>> brandMap;
    private final Map<String, List<Clothing>> typeMap;
    private final Map<String, List<Clothing>> styleMap;

    // Effects: Constructs a new closet with no clothing
    public Closet(String name) {
        this.clothing = new ArrayList<>();
        this.brandMap = new HashMap<>();
        this.typeMap = new HashMap<>();
        this.styleMap = new HashMap<>();
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
        countMapMatches(this.brandMap, address.getBrands(), matchMap);
        countMapMatches(this.styleMap, address.getStyles(), matchMap);
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

    // EFFECTS: Returns the styles present in this closet
    public Set<String> getStyles() {
        return this.styleMap.keySet();
    }

    // Modifies: this
    // Effects: Adds the given clothing to this closet
    public void addClothing(Clothing clothing) {
        this.clothing.add(clothing);

        congregateByKey(this.brandMap, List.of(clothing.getBrand()), clothing);
        congregateByKey(this.typeMap, clothing.getTypes(), clothing);
        congregateByKey(this.styleMap, clothing.getStyles(), clothing);
    }

    // MODIFIES: matchCountMap
    // EFFECTS: Increments the value of the entry under the key of a given
    //          piece of clothing for every time it appears the list of clothing
    //          for each given map key.
    private static void countMapMatches(Map<String, List<Clothing>> refMap,
                                        List<String> mapKeys,
                                        Map<Clothing, Integer> matchCountMap) {
        for (String brand : mapKeys) {
            if (refMap.containsKey(brand)) {
                refMap.get(brand).forEach(c -> {
                    if (matchCountMap.containsKey(c)) {
                        matchCountMap.put(c, matchCountMap.get(c) + 1);
                    } else {
                        matchCountMap.put(c, 1);
                    }
                });
            }
        }
    }

    // MODIFIES: categoryMap
    // EFFECTS: Places element in the lists associated with each key in keys,
    //          and creates a new list first if it is not already present in
    //          the map.
    private static <K, V> void congregateByKey(Map<K, List<V>> categoryMap,
                                               Iterable<K> keys,
                                               V element) {
        keys.forEach(e -> {
            if (!categoryMap.containsKey(e)) {
                categoryMap.put(e, new ArrayList<>());
            }
            categoryMap.get(e).add(element);
        });
    }
}
