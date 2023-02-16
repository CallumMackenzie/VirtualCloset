package model;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// TODO
public class Clothing {

    private boolean dirty;
    private final Collection<String> types;
    private final List<String> styles;
    private String brand;
    private Size size;
    private String material;
    private Image image;

    // Effects: Constructs a new piece of clothing
    public Clothing(Collection<String> types,
                    Size size,
                    String brand,
                    String material,
                    List<String> styles,
                    boolean dirty,
                    Image image) {
        this.types = types.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toCollection(ArrayList::new));
        this.dirty = dirty;
        this.styles = styles;
        this.brand = brand;
        this.size = size;
        this.image = image;
        this.material = material;
    }

    // Effects: Returns whether this clothing is dirty
    public boolean isDirty() {
        return this.dirty;
    }

    // Modifies: this
    // Effects: Sets whether the clothing is dirty or not
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    // Effects: Returns the list of styles for this clothing
    public List<String> getStyles() {
        return this.styles;
    }

    // Effects: Returns the brand this clothing is from
    public String getBrand() {
        return this.brand;
    }

    // Modifies: this
    // Effects: Sets the brand of this clothing
    public void setBrand(String brand) {
        this.brand = brand;
    }

    // Effects: Returns this size of this clothing
    public Size getSize() {
        return this.size;
    }

    // Modifies: this
    // Effects: Sets the size of this clothing
    void setSize(Size size) {
        this.size = size;
    }

    // Effects: Returns the image for this clothing
    public Image getImage() {
        return this.image;
    }

    // Modifies: this
    // Effects: Sets the image for this clothing
    public void setImage(Image image) {
        this.image = image;
    }

    // Effects: Returns the material for this clothing
    public String getMaterial() {
        return this.material;
    }

    // Modifies: this
    // Effects: Sets the material for this clothing
    void setMaterial(String material) {
        this.material = material;
    }

    // Effects: Returns the types of this clothing
    public Collection<String> getTypes() {
        return types;
    }

    // Effects: Returns a string representation of this object
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

    // EFFECTS: Compares equality with the other given object.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Clothing clothing = (Clothing) o;
        return dirty == clothing.dirty
                && types.equals(clothing.types)
                && styles.equals(clothing.styles)
                && brand.equals(clothing.brand)
                && size == clothing.size
                && material.equals(clothing.material)
                && Objects.equals(image, clothing.image);
    }

    // EFFECTS: Produces a hash code for this piece of clothing
    @Override
    public int hashCode() {
        return Objects.hash(dirty, types, styles, brand, size, material, image);
    }
}
