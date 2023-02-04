package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClosetTest {

    private Closet closet1;

    @BeforeEach
    void createCloset() {
        this.closet1 = new Closet("C1");
    }

    @Test
    void testConstructor() {
        assertTrue(this.closet1.getClothing().isEmpty());
    }

    @Test
    void testAddClothing() {
        Clothing c = new Shirt(Size.XL,
                new Brand("Adidas"),
                new Material("Silk"),
                new ArrayList<>(),
                true,
                null);
        this.closet1.addClothing(c);

        assertEquals(this.closet1.getClothing().size(), 1);
        assertEquals(this.closet1.getClothing().get(0), c);

        Clothing c2 = new Shirt(Size.S,
                new Brand("Nike"),
                new Material("Cotton"),
                new ArrayList<>(),
                true, null);
        this.closet1.addClothing(c2);

        assertEquals(this.closet1.getClothing().size(), 2);
        assertEquals(this.closet1.getClothing().get(1), c2);
    }

}
