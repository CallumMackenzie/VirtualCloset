package model.search;

import model.Size;

import java.util.*;

// TODO
public final class ClothingAddress {

    private List<String> brands;
    private List<Size> sizes;
    private List<String> styles;
    private List<String> types;
    private SearchMode searchMode;

    // TODO
    public ClothingAddress() {
        this.brands = new ArrayList<>();
        this.sizes = new ArrayList<>();
        this.styles = new ArrayList<>();
        this.types = new ArrayList<>();
        this.searchMode = SearchMode.EXACT;
    }

    // TODO
    public static ClothingAddress of(String expr) {
        CAStateMachine parser = new CAStateMachine();
        CAStateMachine.State out = parser.processInput(expr.toCharArray());
        return out.getAddress();
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

    // Effects: Returns the search mode for this address
    public SearchMode getSearchMode() {
        return this.searchMode;
    }

    // Modifies: this
    // Effects: Sets the search mode for this address
    public void setSearchMode(SearchMode searchMode) {
        this.searchMode = searchMode;
    }

    public enum SearchMode {
        EXACT,
        SORTED_ALL_SIMILAR;
    }

}
