package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CatalogueTest {

    Catalogue catalogue;

    @BeforeEach
    void createCatalogue() {
        this.catalogue = new Catalogue();
    }

    @Test
    void testConstructor() {
        assertTrue(this.catalogue.getOutfits().isEmpty());
    }

    @Test
    void testGetByName() {
        assertTrue(this.catalogue.getOutfitsByName("A").isEmpty());

        Outfit a = new Outfit("A", new ArrayList<>());
        this.catalogue.addOutfit(a);
        Outfit b = new Outfit("B", new ArrayList<>());
        this.catalogue.addOutfit(b);
        List<Outfit> os = this.catalogue.getOutfitsByName("a");
        assertEquals(1, os.size());
        assertEquals(a, os.get(0));
        os = this.catalogue.getOutfitsByName("B");
        assertEquals(1, os.size());
        assertEquals(b, os.get(0));
    }

    @Test
    void testRemoveAllWithName() {
        this.catalogue.addOutfit(new Outfit("b", new ArrayList<>()));
        this.catalogue.addOutfit(new Outfit("C", new ArrayList<>()));
        this.catalogue.addOutfit(new Outfit("B", new ArrayList<>()));
        this.catalogue.removeAllWithName("b");
        assertEquals(1, this.catalogue.getOutfits().size());
        assertEquals("C", this.catalogue.getOutfits().get(0).getName());
    }

    @Test
    void testAddOutfit() {
        Outfit o = new Outfit("a", new ArrayList<>());
        this.catalogue.addOutfit(o);
        assertEquals(1, this.catalogue.getOutfits().size());
        assertEquals(o, this.catalogue.getOutfits().get(0));
    }

}