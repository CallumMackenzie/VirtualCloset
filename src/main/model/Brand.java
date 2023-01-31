package model;

// A named brand
public class Brand {

    private final String name;

    // Effects: Constructs a new brand with the given name
    public Brand(String name) {
        this.name = name;
    }

    // Effects: Returns the name of this brand
    public String getName() {
        return this.name;
    }
}
