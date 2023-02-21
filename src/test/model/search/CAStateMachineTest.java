package model.search;

import model.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static model.search.CAStateMachine.*;
import static org.junit.jupiter.api.Assertions.*;

class CAStateMachineTest {

    CAStateMachine sm;

    @BeforeEach
    void createStateMachine() {
        this.sm = new CAStateMachine();
    }

    @Test
    void testUnknownKey() {
        String in = "UNINDWID" + EQUALITY_STR
                + "DKSDKDS";
        assertThrows(NoSuchKeyException.class, () -> sm.processInput(in.toCharArray()));
    }

    @Test
    void testCaptureBrandSimple() throws ClothingAddressParseException {
        String in = BRAND_CAPTURE_STR + EQUALITY_STR;
        assertTrue(sm.processInput(in.toCharArray())
                instanceof CAStateMachine.StringListCaptureState);
    }

    @Test
    void testCaptureBrandWhitespaceBefore() throws ClothingAddressParseException {
        String in = "\t  \t " + BRAND_CAPTURE_STR
                + EQUALITY_STR;
        assertTrue(sm.processInput(in.toCharArray())
                instanceof CAStateMachine.StringListCaptureState);
    }

    @Test
    void testCaptureBrandWhitespaceBetween() throws ClothingAddressParseException {
        String in = BRAND_CAPTURE_STR
                + " \t  \t" + EQUALITY_STR;
        assertTrue(sm.processInput(in.toCharArray())
                instanceof CAStateMachine.StringListCaptureState);
    }

    @Test
    void testCaptureBrandSingleListItem() throws ClothingAddressParseException {
        String in = BRAND_CAPTURE_STR
                + EQUALITY_STR
                + "BRAND A"
                + LIST_END_STR;
        CAStateMachine.State out = sm.processInput(in.toCharArray());
        assertTrue(out instanceof CAStateMachine.CapturingState);
        assertEquals(1, out.getAddress().getBrands().size());
        assertEquals("BRAND A", out.getAddress().getBrands().get(0));
    }

    @Test
    void testCaptureBrandMultipleItems() throws ClothingAddressParseException {
        String in = BRAND_CAPTURE_STR
                + EQUALITY_STR
                + "BRAND A" + LIST_SEPARATOR_STR
                + "BRAND B" + LIST_SEPARATOR_STR
                + "BRAND C" + LIST_SEPARATOR_STR
                + "BRAND D" + LIST_END_STR;
        CAStateMachine.State out = sm.processInput(in.toCharArray());
        assertTrue(out instanceof CAStateMachine.CapturingState);
        assertEquals(4, out.getAddress().getBrands().size());
        assertTrue(out.getAddress().getBrands().containsAll(Arrays.asList("BRAND A",
                "BRAND B",
                "BRAND C",
                "BRAND D")));
    }

    @Test
    void testNoCapture() throws ClothingAddressParseException {
        assertTrue(sm.processInput("".toCharArray())
                instanceof CAStateMachine.CapturingState);
    }

    @Test
    void testCaptureBrandStyle() throws ClothingAddressParseException {
        String in = CAStateMachine.STYLE_CAPTURE_STR
                + EQUALITY_STR
                + "casual" + LIST_END_STR
                + BRAND_CAPTURE_STR + EQUALITY_STR
                + "adidas" + LIST_SEPARATOR_STR
                + "nike" + LIST_END_STR;
        ClothingAddress out = sm.processInput(in.toCharArray()).getAddress();
        assertEquals(2, out.getBrands().size());
        assertTrue(out.getBrands().containsAll(Arrays.asList("adidas", "nike")));
        assertEquals(1, out.getStyles().size());
        assertEquals("casual", out.getStyles().get(0));
    }

    @Test
    void testCaptureType() throws ClothingAddressParseException {
        String in = CAStateMachine.TYPE_CAPTURE_STR
                + EQUALITY_STR
                + "pants" + LIST_END_STR;
        ClothingAddress o = sm.processInput(in.toCharArray()).getAddress();
        assertEquals(1, o.getTypes().size());
        assertEquals("pants", o.getTypes().get(0));
    }

    @Test
    void testCaptureSize() throws ClothingAddressParseException {
        String in = CAStateMachine.SIZE_CAPTURE_STR
                + EQUALITY_STR
                + Size.XL + LIST_SEPARATOR_STR
                + Size.XXL.toString().toLowerCase()
                + LIST_END_STR;
        ClothingAddress o = sm.processInput(in.toCharArray()).getAddress();
        assertEquals(2, o.getSizes().size());
        assertTrue(o.getSizes().containsAll(Arrays.asList(Size.XL, Size.XXL)));
    }

    @Test
    void testCaptureSizeUnknown() {
        String in = CAStateMachine.SIZE_CAPTURE_STR
                + EQUALITY_STR
                + "djaisjdosajdsad" + LIST_END_STR;
        assertThrows(UnexpectedInputException.class,
                () -> sm.processInput(in.toCharArray()));
    }

    @Test
    void testCaptureIsDirtyTrue() throws ClothingAddressParseException {
        String in = IS_DIRTY_CAPTURE_STR
                + EQUALITY_STR + "  \t "
                + TRUE_STR;
        ClothingAddress o = sm.processInput(in.toCharArray()).getAddress();
        assertEquals(true, o.getIsDirty());
    }

    @Test
    void testCaptureIsDirtyFalse() throws ClothingAddressParseException {
        String in = IS_DIRTY_CAPTURE_STR
                + EQUALITY_STR + "  \t "
                + FALSE_STR;
        ClothingAddress o = sm.processInput(in.toCharArray()).getAddress();
        assertEquals(false, o.getIsDirty());
    }

    @Test
    void testCaptureIsDirtyInvalid() {
        String in = IS_DIRTY_CAPTURE_STR
                + EQUALITY_STR + "ab";
        UnexpectedInputException ex = assertThrows(UnexpectedInputException.class,
                () -> sm.processInput(in.toCharArray()));
        assertTrue(ex.getErrorState() instanceof CAStateMachine.BooleanCaptureState);
    }

    @Test
    void testStateCapturedEmpty() throws ClothingAddressParseException {
        String in = MATERIAL_CAPTURE_STR
                + EQUALITY_STR + "abcd" + LIST_END_STR;
        CAStateMachine.State out = sm.processInput(in.toCharArray());
        assertTrue(out.getStateCaptured().isEmpty());
    }

    @Test
    void testStateCapturedFull() throws ClothingAddressParseException {
        CAStateMachine.State out = sm
                .processInput(MATERIAL_CAPTURE_STR.toCharArray());
        assertEquals(MATERIAL_CAPTURE_STR, out.getStateCaptured());
        String input2 = EQUALITY_STR + "ABCD";
        CAStateMachine.State out2 = sm.processInput(input2.toCharArray());
        assertEquals("ABCD", out2.getStateCaptured());
    }

    @Test
    void testCaptureAll() throws ClothingAddressParseException {
        String in = "\t   \t\t"
                // BRANDS
                + BRAND_CAPTURE_STR + EQUALITY_STR + "adidas"
                + LIST_SEPARATOR_STR + "lululemon" + LIST_END_STR
                // IS DIRTY
                + IS_DIRTY_CAPTURE_STR + EQUALITY_STR + FALSE_STR + " "
                // STYLE
                + STYLE_CAPTURE_STR + EQUALITY_STR + "casual" + LIST_END_STR
                // TYPE
                + TYPE_CAPTURE_STR + EQUALITY_STR + "shirt" + LIST_END_STR
                // SIZE
                + SIZE_CAPTURE_STR + EQUALITY_STR + Size.XXL
                + LIST_SEPARATOR_STR + Size.S
                + LIST_END_STR
                // MATERIAL
                + MATERIAL_CAPTURE_STR + EQUALITY_STR + "cotton" + LIST_END_STR;
        ClothingAddress o = sm.processInput(in.toCharArray()).getAddress();
        // BRANDS
        assertEquals(2, o.getBrands().size());
        assertTrue(o.getBrands().containsAll(Arrays.asList("lululemon", "adidas")));
        // IS DIRTY
        assertFalse(o.getIsDirty());
        // STYLE
        assertEquals(1, o.getStyles().size());
        assertEquals("casual", o.getStyles().get(0));
        // TYPE
        assertEquals(1, o.getTypes().size());
        assertEquals("shirt", o.getTypes().get(0));
        // SIZE
        assertEquals(2, o.getSizes().size());
        assertTrue(o.getSizes().containsAll(Arrays.asList(Size.XXL, Size.S)));
        // MATERIAL
        assertEquals(1, o.getMaterials().size());
        assertEquals("cotton", o.getMaterials().get(0));
    }
}