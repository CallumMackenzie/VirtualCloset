package model;

import model.search.ClothingAddress;
import org.json.JSONObject;
import persistance.JsonBuilder;
import persistance.Savable;

import java.util.*;
import java.util.stream.Collectors;

// A closet having a list of clothing and various categorizations
// of said clothing
public class Closet implements Savable {

    private final List<Clothing> clothing;
    private final String name;
    private final Map<String, List<Clothing>> styleMap;
    private final Map<String, List<Clothing>> brandMap;
    private final Map<String, List<Clothing>> typeMap;
    private final Map<Size, List<Clothing>> sizeMap;
    private final Map<Boolean, List<Clothing>> dirtyMap;
    private final Map<String, List<Clothing>> materialsMap;
    private final Map<String, List<Clothing>> colorMap;

    // EFFECTS: Constructs a new closet with no clothing
    public Closet(String name) {
        this.clothing = new ArrayList<>();
        this.name = name;

        this.styleMap = new HashMap<>();
        this.brandMap = new HashMap<>();
        this.typeMap = new HashMap<>();
        this.sizeMap = new HashMap<>();
        this.dirtyMap = new HashMap<>();
        this.materialsMap = new HashMap<>();
        this.colorMap = new HashMap<>();
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

    // REQUIRES: element is not in the list contained in any key in categoryMap
    // MODIFIES: categoryMap
    // EFFECTS: Places element in the lists associated with each key in keys,
    //          and creates a new list first if it is not already present in
    //          the map.
    private static <K, V extends Comparable<V>>
    void congregateByKey(Map<K, List<V>> categoryMap,
                         Iterable<K> keys,
                         V element) {
        keys.forEach(e -> {
            if (!categoryMap.containsKey(e)) {
                categoryMap.put(e, new ArrayList<>());
            }
            List<V> values = categoryMap.get(e);
            int idx = Collections.binarySearch(values, element);
            if (idx >= 0 && idx < values.size()) {
                // Value is in list
                return;
            }
            int insertionPoint = -idx - 1;
            values.add(insertionPoint, element);
        });
    }

    // MODIFIES: categoryMap
    // EFFECTS: Removes the element provided from each key in the category map
    //          if present.
    private static <K, V extends Comparable<V>>
    void removeByKey(Map<K, List<V>> categoryMap,
                     Iterable<K> keys,
                     V element) {
        keys.forEach(e -> {
            if (categoryMap.containsKey(e)) {
                List<V> values = categoryMap.get(e);
                int idx = Collections.binarySearch(values, element);
                if (idx >= 0 && idx < values.size()) {
                    values.remove(idx);
                }
                if (values.isEmpty()) {
                    categoryMap.remove(e);
                }
            }
        });
    }

    // EFFECTS: Searches the clothing in this closet for the pieces matching
    //          the given clothing address most closely, and returns them.
    public List<Clothing> findClothing(ClothingAddress address) {
        Map<Clothing, Integer> matchMap = new HashMap<>();

        countMapMatches(this.styleMap, address.getStyles(), matchMap);
        countMapMatches(this.brandMap, address.getBrands(), matchMap);
        countMapMatches(this.typeMap, address.getTypes(), matchMap);
        countMapMatches(this.sizeMap, address.getSizes(), matchMap);
        if (address.getIsDirty() != null) {
            countMapMatches(this.dirtyMap, Collections.singletonList(address.getIsDirty()), matchMap);
        }
        countMapMatches(this.materialsMap, address.getMaterials(), matchMap);
        countMapMatches(this.colorMap, address.getColors(), matchMap);

        return matchMap.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .skip(Math.max(0, matchMap.size() - address.getMatchCount()))
                .collect(Collectors.toList());
    }

    // EFFECTS: Returns the name of this closet
    public String getName() {
        return this.name;
    }

    // EFFECTS: Returns the clothing in this closet
    public List<Clothing> getClothing() {
        return this.clothing;
    }

    // EFFECTS: Returns the types of clothing present in this closet
    public Set<String> getTypes() {
        return this.typeMap.keySet();
    }

    // EFFECTS: Returns the brands present in this closet
    public Set<String> getBrands() {
        return this.brandMap.keySet();
    }

    // EFFECTS: Returns the styles present in this closet
    public Set<String> getStyles() {
        return this.styleMap.keySet();
    }

    // EFFECTS: Returns the sizes present in this closet
    public Set<Size> getSizes() {
        return this.sizeMap.keySet();
    }

    // EFFECTS: Returns the colors present in this closet
    public Set<String> getColors() {
        return this.colorMap.keySet();
    }

    // MODIFIES: this
    // EFFECTS: Removes the clothing from this closet if it is currently tracked.
    public void removeClothing(Clothing clothing) {
        this.clothing.remove(clothing);

        removeByKey(this.styleMap, clothing.getStyles(), clothing);
        removeByKey(this.brandMap, Collections.singletonList(clothing.getBrand()), clothing);
        removeByKey(this.typeMap, clothing.getTypes(), clothing);
        removeByKey(this.sizeMap, Collections.singletonList(clothing.getSize()), clothing);
        removeByKey(this.dirtyMap, Collections.singletonList(clothing.isDirty()), clothing);
        removeByKey(this.materialsMap, Collections.singletonList(clothing.getMaterial()), clothing);
        removeByKey(this.colorMap, clothing.getColors(), clothing);
    }

    // MODIFIES: this
    // EFFECTS: Adds the given clothing to this closet
    public void addClothing(Clothing clothing) {
        int searchIdx = Collections.binarySearch(this.clothing, clothing);
        if (searchIdx >= 0 && searchIdx < this.clothing.size()) {
            // In list already
            return;
        }
        int insertIdx = -searchIdx - 1;
        this.clothing.add(insertIdx, clothing);

        congregateByKey(this.styleMap, clothing.getStyles(), clothing);
        congregateByKey(this.brandMap, Collections.singletonList(clothing.getBrand()), clothing);
        congregateByKey(this.typeMap, clothing.getTypes(), clothing);
        congregateByKey(this.sizeMap, Collections.singletonList(clothing.getSize()), clothing);
        congregateByKey(this.dirtyMap, Collections.singletonList(clothing.isDirty()), clothing);
        congregateByKey(this.materialsMap, Collections.singletonList(clothing.getMaterial()), clothing);
        congregateByKey(this.colorMap, clothing.getColors(), clothing);
    }

    // EFFECTS: Returns a JSON representation of this object
    @Override
    public JSONObject toJson() {
        return new JsonBuilder()
                .savable("clothing", this.clothing)
                .put("name", this.name);
    }
}
