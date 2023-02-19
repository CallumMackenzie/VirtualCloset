package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ClothingTest {

    Clothing cl;

    @BeforeEach
    void createClothing() {
        this.cl = new Clothing(List.of("pants"),
                Size.XL,
                "Adidas",
                "Polyester",
                List.of("Casual"),
                List.of(Color.ORANGE),
                false,
                null);
    }

    @Test
    void testConstructor() {
        assertEquals(this.cl.getTypes().size(), 1);
        assertEquals(this.cl.getTypes().toArray(String[]::new)[0], "pants");
        assertEquals(this.cl.getSize(), Size.XL);
        assertEquals(this.cl.getBrand(), "Adidas");
        assertEquals(this.cl.getMaterial(), "Polyester");
        assertEquals(this.cl.getStyles().size(), 1);
        assertEquals(this.cl.getStyles().toArray(String[]::new)[0], "Casual");
        assertFalse(this.cl.isDirty());
        assertNull(this.cl.getImage());
    }

    @Test
    void testToString() {
        assertEquals(this.cl.toString(),
                "[pants] {" +
                        "\n\tbrand: Adidas" +
                        "\n\tsize: XL" +
                        "\n\tmaterial: Polyester" +
                        "\n\tstyles: Casual" +
                        "\n\tdirty: false" +
                        "\n}");
    }

}
