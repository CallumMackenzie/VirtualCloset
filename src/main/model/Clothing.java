package model;

import java.awt.Image;
import java.util.List;

// TODO
public interface Clothing {
    boolean isDirty();
    void setDirty(boolean dirty);

    List<Style> getStyles();
    void setStyles(List<Style> styles);

    Brand getBrand();
    void setBrand(Brand brand);

    Size getSize();
    void setSize(Size size);

    Image getImage();
    void setImage(Image image);

    Material getMaterial();
    void setMaterial(Material material);
}
