package model.search;

import model.Size;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
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
        assertTrue(ca.getMaterials().isEmpty());
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
        assertTrue(ca.getBrands().containsAll(Arrays.asList("nike", "adidas")));
    }

    @Test
    void ofMultipleParamsTest() throws ClothingAddressParseException {
        ClothingAddress ca = ClothingAddress.of(
                SIZE_CAPTURE_STR + EQUALITY_STR + Size.XXL + LIST_END_STR
                        + TYPE_CAPTURE_STR + EQUALITY_STR + "pants" + LIST_END_STR
                        + STYLE_CAPTURE_STR + EQUALITY_STR + "casual" + LIST_SEPARATOR_STR
                        + "formal" + LIST_END_STR
                        + MATERIAL_CAPTURE_STR + EQUALITY_STR + "silk" + LIST_END_STR
        );
        assertEquals(1, ca.getSizes().size());
        assertEquals(Size.XXL, ca.getSizes().get(0));
        assertEquals(1, ca.getTypes().size());
        assertEquals("pants", ca.getTypes().get(0));
        assertEquals(2, ca.getStyles().size());
        assertTrue(ca.getStyles().containsAll(Arrays.asList("casual", "formal")));
        assertEquals(1, ca.getMaterials().size());
        assertEquals("silk", ca.getMaterials().get(0));
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