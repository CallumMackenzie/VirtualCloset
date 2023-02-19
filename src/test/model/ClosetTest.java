package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClosetTest {

    private Closet closet1;
    private Clothing shirt1;

    @BeforeEach
    void setup() {
        this.closet1 = new Closet("C1");
        this.shirt1 = new Clothing(List.of("shirt"),
                Size.XL,
                "Adidas",
                "Silk",
                new ArrayList<>(),
                List.of(Color.BLUE),
                true,
                null);
    }

    @Test
    void testConstructor() {
        assertTrue(this.closet1.getClothing().isEmpty());
    }

    @Test
    void testAddClothing() {
        this.closet1.addClothing(shirt1);

        assertEquals(this.closet1.getClothing().size(), 1);
        assertEquals(this.closet1.getClothing().get(0), shirt1);

        Clothing c2 = new Clothing(List.of("shirt"),
                Size.S,
                "Nike",
                "Cotton",
                new ArrayList<>(),
                List.of(Color.RED),
                true, null);
        this.closet1.addClothing(c2);

        assertEquals(this.closet1.getClothing().size(), 2);
        assertEquals(this.closet1.getClothing().get(1), c2);
    }

    @Test
    void testGetTypes() {
        this.closet1.addClothing(shirt1);
        assertTrue(this.closet1.getTypes().contains("shirt"));
        assertEquals(this.closet1.getTypes().size(), 1);
    }

    @Test
    void testGetBrands() {
        this.closet1.addClothing(shirt1);
        assertTrue(this.closet1.getBrands().stream()
                .anyMatch(b -> b.equalsIgnoreCase("Adidas")));
    }

    @Test
    void testGetStyles() {
        this.closet1.addClothing(shirt1);
        assertEquals(0, this.closet1.getStyles().size());
    }

}
