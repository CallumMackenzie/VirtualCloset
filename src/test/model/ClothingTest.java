package model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static model.Clothing.*;
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
                false);
    }

    Clothing copyCl() {
        return new Clothing(
                cl.getTypes(),
                cl.getSize(),
                cl.getBrand(),
                cl.getMaterial(),
                cl.getStyles(),
                cl.getColors(),
                cl.isDirty()
        );
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
    }

    @Test
    void testSetters() {
        this.cl.setBrand("Nike");
        this.cl.setDirty(true);
        this.cl.setSize(Size.S);
        this.cl.setMaterial("Silk");
        assertEquals("Nike", this.cl.getBrand());
        assertTrue(this.cl.isDirty());
        assertEquals(Size.S, this.cl.getSize());
        assertEquals(1, this.cl.getColors().size());
        assertEquals("orange", this.cl.getColors().get(0));
        assertEquals("Silk", this.cl.getMaterial());
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

    @Test
    void testCompareToSameObj() {
        assertEquals(0, this.cl.compareTo(this.cl));
        assertEquals(0, this.cl.compareTo(copyCl()));
    }

    <T extends Comparable<T>>
    void testCompareToDiffOneParam(T newValue,
                                   Supplier<T> getter,
                                   Consumer<Clothing> setter) {
        Clothing same = copyCl();
        setter.accept(same);
        assertEquals(getter.get().compareTo(newValue), cl.compareTo(same));
    }

    <T extends Comparable<T>>
    void testCompareToComparableListSize(List<T> newValue,
                                         Function<Clothing, List<T>> getter) {
        Clothing same = copyCl();
        List<T> clList = getter.apply(cl);
        List<T> sameList = getter.apply(same);
        sameList.clear();
        sameList.addAll(newValue);
        // Compare size first
        assertEquals(Integer.compare(clList.size(), sameList.size()),
                cl.compareTo(same));
    }

    @Test
    void testCompareToDiffOneParam() {
        testCompareToDiffOneParam("uniqlo", cl::getBrand, x -> x.setBrand("uniqlo"));
        testCompareToDiffOneParam(Size.XS, cl::getSize, c -> c.setSize(Size.XS));
        testCompareToDiffOneParam("abcd", cl::getMaterial, x -> x.setMaterial("abcd"));
        testCompareToComparableListSize(Arrays.asList("A", "C"), Clothing::getTypes);
        testCompareToComparableListSize(Arrays.asList("P", "Q", "R"), Clothing::getStyles);
        testCompareToComparableListSize(new ArrayList<>(), Clothing::getColors);
        testCompareToDiffOneParam(true, cl::isDirty, x -> x.setDirty(true));
    }

    @Test
    void testToJson() {
        JSONObject jso = cl.toJson();
        assertEquals(cl.getBrand(), jso.getString(JSON_BRAND_KEY));
        assertEquals(cl.getSize(), jso.getEnum(Size.class, JSON_SIZE_KEY));
        assertEquals(cl.isDirty(), jso.getBoolean(JSON_DIRTY_KEY));
        assertEquals(cl.getMaterial(), jso.getString(JSON_MATERIAL_KEY));

        JSONArray cArr = jso.getJSONArray(JSON_COLORS_KEY);
        assertNotNull(cArr);
        assertEquals(cl.getColors().size(), cArr.length());
        assertTrue(cArr.toList().containsAll(cl.getColors()));

        cArr = jso.getJSONArray(JSON_STYLES_KEY);
        assertNotNull(cArr);
        assertEquals(cl.getStyles().size(), cArr.length());
        assertTrue(cArr.toList().containsAll(cl.getStyles()));

        cArr = jso.getJSONArray(JSON_TYPES_KEY);
        assertNotNull(cArr);
        assertEquals(cl.getTypes().size(), cArr.length());
        assertTrue(cArr.toList().containsAll(cl.getTypes()));
    }

}
