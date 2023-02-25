package model;

import model.search.ClothingAddress;
import model.search.ClothingAddressParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static model.search.CAStateMachineBuilder.*;
import static org.junit.jupiter.api.Assertions.*;

public class ClosetTest {

    private Closet closet1;
    private Clothing shirt1;

    @BeforeEach
    void setup() {
        this.closet1 = new Closet("C1");
        this.shirt1 = new Clothing(Collections.singletonList("shirt"),
                Size.XL,
                "Adidas",
                "Silk",
                new ArrayList<>(),
                Collections.singletonList(Color.BLUE),
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

        Clothing c2 = new Clothing(Collections.singletonList("shirt"),
                Size.S,
                "Nike",
                "Cotton",
                new ArrayList<>(),
                Collections.singletonList(Color.RED),
                true, null);
        this.closet1.addClothing(c2);

        assertEquals(this.closet1.getClothing().size(), 2);
        assertEquals(this.closet1.getClothing().get(1), c2);
    }

    @Test
    void testRemoveClothing() {
        this.closet1.addClothing(shirt1);
        this.closet1.removeClothing(shirt1);
        assertFalse(this.closet1.getClothing().contains(shirt1));
    }

    @Test
    void testFindClothing() throws ClothingAddressParseException {
        this.closet1.addClothing(shirt1);
        List<Clothing> result = this.closet1.findClothing(ClothingAddress.of(
                BRAND_CAPTURE_STR + EQUALITY_STR + this.shirt1.getBrand()
                        + LIST_END_STR
        ));
        assertEquals(1, result.size(), result.toString());
        assertEquals(this.shirt1, result.get(0), result.get(0).toString());
    }

    @Test
    void testFindClothingLimited() {
        this.closet1.addClothing(shirt1);
        ClothingAddress c1 = new ClothingAddress();
        c1.getBrands().add(shirt1.getBrand());
        c1.setMatchCount(0);
        List<Clothing> result = this.closet1.findClothing(c1);
        assertTrue(result.isEmpty());

        c1.setMatchCount(1);
        result = this.closet1.findClothing(c1);
        assertEquals(1, result.size());
        assertEquals(shirt1, result.get(0));
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
