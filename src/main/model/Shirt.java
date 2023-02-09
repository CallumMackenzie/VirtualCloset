package model;

import java.awt.*;
import java.util.List;

// A shirt
public class Shirt extends Clothing {
    public Shirt(Size size,
                 String brand,
                 String material,
                 List<String> styles,
                 boolean dirty,
                 Image image) {
        super(List.of("Shirt"), size, brand, material, styles, dirty, image);
    }
}
