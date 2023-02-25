package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class ClothingTest {

    Clothing cl;

    @BeforeEach
    void createClothing() {
        this.cl = new Clothing(Collections.singletonList("pants"),
                Size.XL,
                "Adidas",
                "Polyester",
                Collections.singletonList("Casual"),
                Collections.singletonList("orange"),
                false,
                null);
    }

    @Test
    void testConstructor() {
        assertEquals(1, this.cl.getTypes().size());
        assertEquals("pants", this.cl.getTypes().stream()
                .findFirst().orElse(null));
        assertEquals(Size.XL, this.cl.getSize());
        assertEquals("Adidas", this.cl.getBrand());
        assertEquals("Polyester", this.cl.getMaterial());
        assertEquals(1, this.cl.getStyles().size());
        assertEquals("Casual", this.cl.getStyles().stream()
                .findFirst().orElse(null));
        assertFalse(this.cl.isDirty());
        assertNull(this.cl.getImage());
    }

    @Test
    void testSetters() {
        this.cl.setBrand("Nike");
        this.cl.setDirty(true);
        this.cl.setSize(Size.S);
        this.cl.setMaterial("Silk");
        this.cl.setImage(new BufferedImage(1, 1,
                BufferedImage.TYPE_BYTE_GRAY));
        assertEquals("Nike", this.cl.getBrand());
        assertTrue(this.cl.isDirty());
        assertEquals(Size.S, this.cl.getSize());
        assertEquals(1, this.cl.getColors().size());
        assertEquals("orange", this.cl.getColors().get(0));
        assertEquals("Silk", this.cl.getMaterial());
        assertNotNull(this.cl.getImage());
    }

    @Test
    void testToString() {
        assertEquals(this.cl.toString(),
                "[pants] {"
                        + "\n\tbrand: Adidas"
                        + "\n\tsize: XL"
                        + "\n\tmaterial: Polyester"
                        + "\n\tcolors: orange"
                        + "\n\tstyles: Casual"
                        + "\n\tdirty: false"
                        + "\n}");
    }

}
