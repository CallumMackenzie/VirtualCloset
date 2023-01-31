package model;

import java.awt.Image;
import java.util.List;

// TODO
public abstract class Clothing {

    private boolean dirty;
    private List<Style> styles;
    private Brand brand;
    private Size size;
    private Material material;
    private Image image;

    // Effects: Constructs a new piece of clothing
    public Clothing(Size size,
                    Brand brand,
                    Material material,
                    List<Style> styles,
                    boolean dirty,
                    Image image) {
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
    public List<Style> getStyles() {
        return this.styles;
    }

    // Modifies: this
    // Effects: Sets styles to the given styles
    public void setStyles(List<Style> styles) {
        this.styles = styles;
    }

    // Effects: Returns the brand this clothing is from
    public Brand getBrand() {
        return this.brand;
    }

    // Modifies: this
    // Effects: Sets the brand of this clothing
    public void setBrand(Brand brand) {
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
    public Material getMaterial() {
        return this.material;
    }

    // Modifies: this
    // Effects: Sets the material for this clothing
    void setMaterial(Material material) {
        this.material = material;
    }
}
