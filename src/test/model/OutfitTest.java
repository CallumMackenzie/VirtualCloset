package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class OutfitTest {

    Clothing shirt;
    Clothing pants;
    Outfit outfit;

    @BeforeEach
    void initOutfit() {
        this.shirt = new Clothing(Collections.singletonList("shirt"),
                Size.XL,
                "adidas",
                "polyester",
                Collections.singletonList("casual"),
                Collections.singletonList("orange"),
                false,
                null);
        this.pants = new Clothing(Collections.singletonList("sweatpants"),
                Size.XL,
                "under armor",
                "cotton",
                Collections.singletonList("casual"),
                Collections.singletonList("gray"),
                false,
                null);
        this.outfit = new Outfit("a", Arrays.asList(shirt, pants));
    }

    @Test
    void testConstructor() {
        assertEquals(2, this.outfit.getClothing().size());
        assertTrue(this.outfit.getClothing().containsAll(Arrays.asList(
                shirt, pants
        )));
        assertEquals("a", this.outfit.getName());
    }

    @Test
    void testSetName() {
        this.outfit.setName("b");
        assertEquals("b", this.outfit.getName());
    }

    @Test
    void testAddClothing() throws InterruptedException {
        Instant before = this.outfit.getInstantLastModified();
        Thread.sleep(10);
        Clothing sweater = new Clothing(
                Collections.singletonList("sweater"),
                Size.XL,
                "nike",
                "cotton",
                Collections.singletonList("casual"),
                Collections.singletonList("blue"),
                false,
                null);
        this.outfit.addClothing(sweater);
        assertEquals(3, this.outfit.getClothing().size());
        assertTrue(this.outfit.getClothing().contains(sweater));
        assertNotEquals(before.getNano(),
                this.outfit.getInstantLastModified().getNano());
    }

    @Test
    void testRemoveClothing() throws InterruptedException {
        Instant before = this.outfit.getInstantLastModified();
        Thread.sleep(10);
        assertTrue(this.outfit.removeClothing(this.pants));
        assertEquals(1, this.outfit.getClothing().size());
        Instant mod = this.outfit.getInstantLastModified();
        assertNotEquals(before.getNano(), mod.getNano());

        assertFalse(this.outfit.removeClothing(this.pants));
        assertEquals(mod, this.outfit.getInstantLastModified());
    }

}