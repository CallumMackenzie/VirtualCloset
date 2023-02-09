package model;

import java.util.*;
import java.util.stream.Collectors;

// TODO
public final class ClothingAddress {

    private List<String> brands;
    private List<Size> sizes;
    private List<String> styles;
    private List<String> types;

    // TODO
    public ClothingAddress() {
    }

    // TODO
    public static ClothingAddress of(String expr) {
        // TODO
         Scanner s = new Scanner(expr);
        return null;
    }

    // Effects: Returns the brands this address matches
    public List<String> getBrands() {
        return brands;
    }

    // Modifies: this
    // Effects: Sets the brands this address matches
    public void setBrands(List<String> brands) {
        this.brands = brands;
    }

    // Effects: Returns the sizes this address matches
    public List<Size> getSizes() {
        return sizes;
    }

    // Modifies: this
    // Effects: Sets the sizes this address matches
    public void setSizes(List<Size> sizes) {
        this.sizes = sizes;
    }

    // Effects: Returns the styles this address matches
    public List<String> getStyles() {
        return styles;
    }

    // Modifies: this
    // Effects: Sets the styles this address matches
    public void setStyles(List<String> styles) {
        this.styles = styles;
    }

    // Effects: Returns the types this address matches
    public List<String> getTypes() {
        return types;
    }

    // Modifies: this
    // Effects: Sets the types this address matches
    public void setTypes(List<String> types) {
        this.types = types;
    }
}
