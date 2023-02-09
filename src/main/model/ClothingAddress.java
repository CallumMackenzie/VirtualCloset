package model;

import java.util.*;
import java.util.stream.Collectors;

// TODO
public final class ClothingAddress {

    private Optional<List<String>> brands;
    private Optional<List<Size>> sizes;
    private Optional<List<String>> styles;
    private Optional<List<String>> types;

    // "b:Adidas, n:"
    // TODO
    public ClothingAddress() {
        this.brands = Optional.empty();
        this.sizes = Optional.empty();
        this.types = Optional.empty();
        this.styles = Optional.empty();
    }

    // TODO
    public static ClothingAddress of(String expr) {
        // TODO
        return null;
    }

    // Effects: Returns the brands this address matches
    public Optional<List<String>> getBrands() {
        return brands;
    }

    // Modifies: this
    // Effects: Sets the brands this address matches
    public void setBrands(Optional<List<String>> brands) {
        this.brands = brands;
    }

    // Effects: Returns the sizes this address matches
    public Optional<List<Size>> getSizes() {
        return sizes;
    }

    // Modifies: this
    // Effects: Sets the sizes this address matches
    public void setSizes(Optional<List<Size>> sizes) {
        this.sizes = sizes;
    }

    // Effects: Returns the styles this address matches
    public Optional<List<String>> getStyles() {
        return styles;
    }

    // Modifies: this
    // Effects: Sets the styles this address matches
    public void setStyles(Optional<List<String>> styles) {
        this.styles = styles;
    }

    // Effects: Returns the types this address matches
    public Optional<List<String>> getTypes() {
        return types;
    }

    // Modifies: this
    // Effects: Sets the types this address matches
    public void setTypes(Optional<List<String>> types) {
        this.types = types;
    }
}
