package model;

import java.awt.*;
import java.util.List;

// A shirt
public class Shirt extends Clothing {
    public Shirt(Size size,
                 Brand brand,
                 Material material,
                 List<Style> styles,
                 boolean dirty,
                 Image image) {
        super(size, brand, material, styles, dirty, image);
    }
}
