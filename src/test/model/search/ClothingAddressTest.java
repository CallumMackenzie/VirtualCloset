package model.search;

import model.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

import java.util.List;

import static model.search.CAStateMachine.*;
import static org.junit.jupiter.api.Assertions.*;

class ClothingAddressTest {

    @Test
    void testDefaultConstructor() {
        ClothingAddress ca = new ClothingAddress();
        assertTrue(ca.getSizes().isEmpty());
        assertTrue(ca.getBrands().isEmpty());
        assertTrue(ca.getStyles().isEmpty());
        assertTrue(ca.getTypes().isEmpty());
        assertNull(ca.getIsDirty());
    }

    @Test
    void testSetters() {
        ClothingAddress ca = new ClothingAddress();
        ca.setBrands(List.of("A"));
        ca.setSizes(List.of(Size.XL));
        ca.setTypes(List.of("C"));
        ca.setStyles(List.of("D"));
        ca.setIsDirty(true);
        assertEquals(1, ca.getBrands().size());
        assertEquals(1, ca.getSizes().size());
        assertEquals(1, ca.getTypes().size());
        assertEquals(1, ca.getStyles().size());
        assertEquals("A", ca.getBrands().get(0));
        assertEquals(Size.XL, ca.getSizes().get(0));
        assertEquals("C", ca.getTypes().get(0));
        assertEquals("D", ca.getStyles().get(0));
        assertTrue(ca.getIsDirty());
    }

    @Test
    void ofBrandsOnlyTest() throws ClothingAddressParseException {
        ClothingAddress ca = ClothingAddress.of(
                CAStateMachine.BRAND_CAPTURE_STR
                        + EQUALITY_STR
                        + "adidas"
                        + LIST_SEPARATOR_STR
                        + "nike"
                        + LIST_END_STR
        );
        assertEquals(2, ca.getBrands().size());
        assertTrue(ca.getBrands().containsAll(List.of("nike", "adidas")));
    }

    @Test
    void ofMultipleParamsTest() throws ClothingAddressParseException {
        ClothingAddress ca = ClothingAddress.of(
                SIZE_CAPTURE_STR + EQUALITY_STR + Size.XXL + LIST_END_STR
                        + TYPE_CAPTURE_STR + EQUALITY_STR + "pants" + LIST_END_STR
                        + STYLE_CAPTURE_STR + EQUALITY_STR + "casual" + LIST_SEPARATOR_STR
                        + "formal" + LIST_END_STR
        );
        assertEquals(1, ca.getSizes().size());
        assertEquals(Size.XXL, ca.getSizes().get(0));
        assertEquals(1, ca.getTypes().size());
        assertEquals("pants", ca.getTypes().get(0));
        assertEquals(2, ca.getStyles().size());
        assertTrue(ca.getStyles().containsAll(List.of("casual", "formal")));
    }

    @Test
    void testOfIncorrectEndState() {
        assertThrows(IncorrectEndStateException.class,
                () -> ClothingAddress.of(BRAND_CAPTURE_STR + EQUALITY_STR
                        + "adidas" + LIST_SEPARATOR_STR));
        assertThrows(IncorrectEndStateException.class,
                () -> ClothingAddress.of(TYPE_CAPTURE_STR + EQUALITY_STR));
        assertThrows(IncorrectEndStateException.class,
                () -> ClothingAddress.of(STYLE_CAPTURE_STR + EQUALITY_STR
                        + "casual"));
        assertThrows(IncorrectEndStateException.class,
                () -> ClothingAddress.of(SIZE_CAPTURE_STR + EQUALITY_STR
                        + Size.XXL + LIST_SEPARATOR_STR + Size.S));
        assertThrows(IncorrectEndStateException.class,
                () -> ClothingAddress.of("a"));
        assertThrows(IncorrectEndStateException.class,
                () -> ClothingAddress.of(IS_DIRTY_CAPTURE_STR + EQUALITY_STR
                        + "y"));
        assertThrows(IncorrectEndStateException.class,
                () -> ClothingAddress.of(IS_DIRTY_CAPTURE_STR + EQUALITY_STR));
    }
}