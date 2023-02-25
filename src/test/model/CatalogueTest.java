package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

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
    void testAddOutfit() {
        Outfit o = new Outfit("a", new ArrayList<>());
        this.catalogue.addOutfit(o);
        assertEquals(1, this.catalogue.getOutfits().size());
        assertEquals(o, this.catalogue.getOutfits().get(0));
    }

}