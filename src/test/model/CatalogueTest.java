package model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static model.Catalogue.JSON_OUTFITS_KEY;
import static org.junit.jupiter.api.Assertions.*;

class CatalogueTest {

    Catalogue catalogue;
    Clothing c1, c2;

    @BeforeEach
    void createCatalogue() {
        this.catalogue = new Catalogue();
        this.c1 = new Clothing(Collections.singletonList("pants"),
                Size.XL,
                "Adidas",
                "Polyester",
                Collections.singletonList("Casual"),
                Collections.singletonList("orange"),
                false);
        this.c2 = new Clothing(Collections.singletonList("shirt"),
                Size.S,
                "Nike",
                "Polyester",
                Collections.singletonList("Casual"),
                Collections.singletonList("yellow"),
                false);
    }

    @Test
    void testConstructor() {
        assertTrue(this.catalogue.getOutfits().isEmpty());
        List<Outfit> lo = new ArrayList<>();
        lo.add(new Outfit("adnlksd", new ArrayList<>()));
        Catalogue c2 = new Catalogue(lo);
        assertEquals(lo, c2.getOutfits());
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

    @Test
    void testToJson() {
        JSONObject jso = this.catalogue.toJson(new ArrayList<>());
        assertNotNull(jso.getJSONArray(JSON_OUTFITS_KEY));
        assertEquals(0, jso.getJSONArray(JSON_OUTFITS_KEY).length());

        this.catalogue.addOutfit(new Outfit("o", Arrays.asList(
                c1, c2
        )));
        List<Clothing> cs = Stream.of(c1, c2)
                .sorted().collect(Collectors.toList());
        jso = this.catalogue.toJson(cs);
        JSONArray jsa = jso.getJSONArray(JSON_OUTFITS_KEY);
        assertEquals(1, jsa.length());
        assertEquals("o",
                jsa.getJSONObject(0).getString(Outfit.JSON_NAME_KEY));
    }

    @Test
    void testFromJson() {
        List<Clothing> cs = Stream.of(c1, c2)
                .sorted().collect(Collectors.toList());
        this.catalogue.addOutfit(new Outfit("o1", Arrays.asList(c1, c2)));
        this.catalogue.addOutfit(new Outfit("o2", Collections.singletonList(c2)));
        JSONObject j = this.catalogue.toJson(cs);
        Catalogue c2 = Catalogue.fromJson(j, cs);
        assertEquals(this.catalogue.getOutfits().size(), c2.getOutfits().size());
        assertEquals(1, c2.getOutfitsByName("o1").size());
        assertEquals(1, c2.getOutfitsByName("o2").size());
        assertTrue(Objects.requireNonNull(c2.getOutfitsByName("o1")
                        .stream().findFirst().orElse(null))
                .getClothing().containsAll(cs));
    }

}