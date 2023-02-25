package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

// An article of clothing with a list of types, styles, colors, and a brand, size, material,
// image, and whether it is dirty or not.
public class Clothing {

    private final List<String> styles;
    private final List<String> types;
    private String brand;
    private Size size;
    private boolean dirty;
    private String material;

    private final List<Color> colors;
    private Image image;

    // EFFECTS: Constructs a new piece of clothing
    public Clothing(Collection<String> types,
                    Size size,
                    String brand,
                    String material,
                    List<String> styles,
                    List<Color> colors,
                    boolean dirty,
                    Image image) {
        this.types = types.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toCollection(ArrayList::new));
        this.dirty = dirty;
        this.colors = colors;
        this.styles = styles;
        this.brand = brand;
        this.size = size;
        this.image = image;
        this.material = material;
    }

    // EFFECTS: Returns whether this clothing is dirty
    public boolean isDirty() {
        return this.dirty;
    }

    // MODIFIES: this
    // EFFECTS: Sets whether the clothing is dirty or not
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    // EFFECTS: Returns the list of styles for this clothing
    public List<String> getStyles() {
        return this.styles;
    }

    // EFFECTS: Returns the brand this clothing is from
    public String getBrand() {
        return this.brand;
    }

    // MODIFIES: this
    // EFFECTS: Sets the brand of this clothing
    public void setBrand(String brand) {
        this.brand = brand;
    }

    // EFFECTS: Returns this size of this clothing
    public Size getSize() {
        return this.size;
    }

    // MODIFIES: this
    // EFFECTS: Sets the size of this clothing
    public void setSize(Size size) {
        this.size = size;
    }

    // EFFECTS: Returns the image for this clothing
    public Image getImage() {
        return this.image;
    }

    // MODIFIES: this
    // EFFECTS: Sets the image for this clothing
    public void setImage(Image image) {
        this.image = image;
    }

    // EFFECTS: Returns the material for this clothing
    public String getMaterial() {
        return this.material;
    }

    // MODIFIES: this
    // EFFECTS: Sets the material for this clothing
    public void setMaterial(String material) {
        this.material = material;
    }

    // EFFECTS: Returns the types of this clothing
    public List<String> getTypes() {
        return types;
    }

    // EFFECTS: Returns the colors for this piece of clothing.
    public List<Color> getColors() {
        return this.colors;
    }

    // EFFECTS: Returns a string representation of this object
    @Override
    public String toString() {
        return "[" + String.join(", ", this.types) + "] {"
                + "\n\tbrand: " + this.brand
                + "\n\tsize: " + this.size
                + "\n\tmaterial: " + this.material
                + "\n\tstyles: " + String.join(", ", this.styles)
                + "\n\tdirty: " + this.dirty
                + "\n}";
    }
}
