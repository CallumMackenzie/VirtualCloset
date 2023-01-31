package model;

// A named clothing material
public class Material {
    private final String name;

    // Effects: Constructs a new brand with the given name
    public Material(String name) {
        this.name = name;
    }

    // Effects: Returns the name of this brand
    public String getName() {
        return this.name;
    }
}
