package model;

import model.search.ClothingAddress;

import java.util.*;
import java.util.stream.Collectors;

// A closet having a list of clothing and various categorizations
// of said clothing
public class Closet {

    private final List<Clothing> clothing;
    private final String name;
    private final Map<String, List<Clothing>> styleMap;
    private final HashMap<String, List<Clothing>> brandMap;
    private final Map<String, List<Clothing>> typeMap;
    private final Map<Size, List<Clothing>> sizeMap;
    private final Map<Boolean, List<Clothing>> dirtyMap;

    // Effects: Constructs a new closet with no clothing
    public Closet(String name) {
        this.clothing = new ArrayList<>();
        this.name = name;

        this.styleMap = new HashMap<>();
        this.brandMap = new HashMap<>();
        this.typeMap = new HashMap<>();
        this.sizeMap = new HashMap<>();
        this.dirtyMap = new HashMap<>();
    }

    // EFFECTS: Searches the clothing in this closet for the pieces matching
    //          the given clothing address most closely, and returns them.
    public List<Clothing> findClothing(ClothingAddress address) {
        // TODO
        Map<Clothing, Integer> matchMap = new HashMap<>();
        countMapMatches(this.styleMap, address.getStyles(), matchMap);
        countMapMatches(this.brandMap, address.getBrands(), matchMap);
        countMapMatches(this.typeMap, address.getTypes(), matchMap);
        countMapMatches(this.sizeMap, address.getSizes(), matchMap);
        if (address.getIsDirty() != null) {
            countMapMatches(this.dirtyMap, List.of(address.getIsDirty()), matchMap);
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

    // EFFECTS: Returns the styles present in this closet
    public Set<String> getStyles() {
        return this.styleMap.keySet();
    }

    // MODIFIES: this
    // EFFECTS: Removes the clothing from this closet if it is currently tracked.
    public void removeClothing(Clothing clothing) {
        this.clothing.remove(clothing);

        removeByKey(this.styleMap, clothing.getStyles(), clothing);
        removeByKey(this.brandMap, List.of(clothing.getBrand()), clothing);
        removeByKey(this.typeMap, clothing.getTypes(), clothing);
        removeByKey(this.sizeMap, List.of(clothing.getSize()), clothing);
        removeByKey(this.dirtyMap, List.of(clothing.isDirty()), clothing);
    }

    // Modifies: this
    // Effects: Adds the given clothing to this closet
    public void addClothing(Clothing clothing) {
        this.clothing.add(clothing);

        congregateByKey(this.styleMap, clothing.getStyles(), clothing);
        congregateByKey(this.brandMap, List.of(clothing.getBrand()), clothing);
        congregateByKey(this.typeMap, clothing.getTypes(), clothing);
        congregateByKey(this.sizeMap, List.of(clothing.getSize()), clothing);
        congregateByKey(this.dirtyMap, List.of(clothing.isDirty()), clothing);
    }

    // MODIFIES: matchCountMap
    // EFFECTS: Increments the value of the entry under the key of a given
    //          piece of clothing for every time it appears the list of clothing
    //          for each given map key.
    private static <T> void countMapMatches(Map<T, List<Clothing>> refMap,
                                            List<T> mapKeys,
                                            Map<Clothing, Integer> matchCountMap) {
        for (T t : mapKeys) {
            if (refMap.containsKey(t)) {
                refMap.get(t).forEach(c -> {
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

    // MODIFIES: categoryMap
    // EFFECTS: Removes the element provided from each key in the category map
    //          if present.
    private static <K, V> void removeByKey(Map<K, List<V>> categoryMap,
                                           Iterable<K> keys,
                                           V element) {
        keys.forEach(e -> {
            if (categoryMap.containsKey(e)) {
                categoryMap.get(e).remove(element);
            }
        });
    }
}
