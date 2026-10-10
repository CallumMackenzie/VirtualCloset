package model;

import model.search.ClothingAddress;
import model.search.ClothingAddressParseException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static model.search.CAStateMachineBuilder.*;
import static org.junit.jupiter.api.Assertions.*;

public class ClosetTest {

    private Closet closet1;
    private Clothing shirt1, pants1;

    @BeforeEach
    void setup() {
        this.closet1 = new Closet("C1");
        this.shirt1 = new Clothing(Collections.singletonList("shirt"),
                Size.XL,
                "Adidas",
                "Silk",
                Collections.singletonList("casual"),
                Collections.singletonList("blue"),
                true);
        this.pants1 = new Clothing(Collections.singletonList("pants"),
                Size.L,
                "Nike",
                "Cotton",
                Collections.singletonList("casual"),
                Arrays.asList("orange", "yellow"),
                false);
    }

    @Test
    void testConstructor() {
        assertEquals("C1", this.closet1.getName());
        assertTrue(this.closet1.getClothing().isEmpty());
        assertTrue(this.closet1.getSizes().isEmpty());
        assertTrue(this.closet1.getBrands().isEmpty());
        assertTrue(this.closet1.getTypes().isEmpty());
        assertTrue(this.closet1.getStyles().isEmpty());
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
                Collections.singletonList("red"),
                true);
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
    void testFindClothingDirty() {
        this.closet1.addClothing(shirt1);
        ClothingAddress c1 = new ClothingAddress();
        c1.setIsDirty(shirt1.isDirty());
        List<Clothing> result = this.closet1.findClothing(c1);
        assertEquals(1, result.size());
        assertEquals(shirt1, result.get(0));
    }

    @Test
    void testFindClothingNoneMatch() {
        this.closet1.addClothing(shirt1);
        ClothingAddress c = new ClothingAddress();
        c.getBrands().add("abcd");
        assertTrue(this.closet1.findClothing(c).isEmpty());
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
    void testFindClothingByColor() {
        this.closet1.addClothing(shirt1);
        ClothingAddress c1 = new ClothingAddress();
        c1.getColors().addAll(shirt1.getColors());
        List<Clothing> result = this.closet1.findClothing(c1);
        assertEquals(1, result.size());
        assertEquals(shirt1, result.get(0));
    }

    @Test
    void testFindClothingNoParams() {
        this.closet1.addClothing(shirt1);
        List<Clothing> res = this.closet1.findClothing(new ClothingAddress());
        assertTrue(res.isEmpty());
    }

    @Test
    void testFindClothingMultiParamMatch() {
        this.closet1.addClothing(shirt1);
        this.closet1.addClothing(pants1);
        ClothingAddress address = new ClothingAddress();
        address.getSizes().add(shirt1.getSize());
        address.getColors().addAll(pants1.getColors());
        List<Clothing> clothing = this.closet1.findClothing(address);
        assertEquals(2, clothing.size());
        assertEquals(pants1, clothing.get(1));
        assertEquals(shirt1, clothing.get(0));
    }

    @Test
    void testAddClothingInListAlready() {
        this.closet1.addClothing(shirt1);
        this.closet1.addClothing(shirt1);
        assertEquals(1, this.closet1.getClothing().size());
    }

    @Test
    void testGetSizes() {
        this.closet1.addClothing(shirt1);
        assertTrue(this.closet1.getSizes().contains(shirt1.getSize()));
        assertEquals(1, this.closet1.getSizes().size());
        this.closet1.removeClothing(shirt1);
        assertTrue(this.closet1.getSizes().isEmpty());
    }

    @Test
    void testGetTypes() {
        this.closet1.addClothing(shirt1);
        assertTrue(this.closet1.getTypes()
                .containsAll(shirt1.getTypes()));
        assertEquals(1, this.closet1.getTypes().size());
        this.closet1.removeClothing(shirt1);
        assertTrue(this.closet1.getTypes().isEmpty());
    }

    @Test
    void testGetBrands() {
        this.closet1.addClothing(shirt1);
        assertTrue(this.closet1.getBrands().contains(shirt1.getBrand()));
        this.closet1.removeClothing(shirt1);
        assertTrue(this.closet1.getBrands().isEmpty());
    }

    @Test
    void testGetStyles() {
        this.closet1.addClothing(shirt1);
        assertEquals(1, this.closet1.getStyles().size());
        assertTrue(this.closet1.getStyles().containsAll(shirt1.getStyles()));
        this.closet1.removeClothing(shirt1);
        assertTrue(this.closet1.getStyles().isEmpty());
    }

    @Test
    void testGetColors() {
        this.closet1.addClothing(shirt1);
        assertEquals(1, this.closet1.getColors().size());
        assertTrue(this.closet1.getColors()
                .containsAll(shirt1.getColors()));
        this.closet1.removeClothing(shirt1);
        assertTrue(this.closet1.getColors().isEmpty());
    }

    @Test
    void testAddClothingSorts() {
        Clothing other = new Clothing(Collections.singletonList("shirt"),
                Size.S,
                "Adidas",
                "Silk",
                Collections.singletonList("casual"),
                Collections.singletonList("blue"),
                true);
        this.closet1.addClothing(shirt1);
        this.closet1.addClothing(other);
        this.closet1.addClothing(shirt1);
        this.closet1.addClothing(shirt1);
        assertEquals(2, this.closet1.getSizes().size());
        assertTrue(this.closet1.getSizes().containsAll(Arrays.asList(
                Size.XL, Size.S
        )));
        assertEquals(2, this.closet1.getClothing().size());
        assertEquals(1, this.closet1.getClothing().indexOf(shirt1));
        Clothing other2 = new Clothing(Collections.singletonList("shirt"),
                Size.M,
                "Adidas",
                "Silk",
                Collections.singletonList("casual"),
                Collections.singletonList("blue"),
                true);
        this.closet1.addClothing(other2);
        assertEquals(3, this.closet1.getClothing().size());
        assertEquals(1, this.closet1.getClothing().indexOf(other2));
    }

    @Test
    void testToJson() {
        this.closet1.addClothing(shirt1);
        List<Clothing> allClothing = Collections.singletonList(shirt1);
        JSONObject jso = this.closet1.toJson(allClothing);
        assertEquals(this.closet1.getName(), jso.getString(Closet.JSON_NAME_KEY));
        JSONArray jsa = jso.getJSONArray(Closet.JSON_CLOTHING_KEY);
        assertNotNull(jsa);
        assertEquals(1, jsa.length());
        assertEquals(0, jsa.getInt(0));
    }

    @Test
    void testFromJson() {
        this.closet1.addClothing(shirt1);
        List<Clothing> allClothing = Collections.singletonList(shirt1);
        JSONObject jso = this.closet1.toJson(allClothing);
        Closet c = Closet.fromJson(jso, allClothing);
        assertEquals(this.closet1.getName(), c.getName());
        assertEquals(this.closet1.getClothing(), c.getClothing());
    }

}
