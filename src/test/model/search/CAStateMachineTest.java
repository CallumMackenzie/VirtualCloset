package model.search;

import model.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CAStateMachineTest {

    CAStateMachine sm;

    @BeforeEach
    void createStateMachine() {
        this.sm = new CAStateMachine();
    }

    @Test
    void testUnknownKey() {
        String in = "UNINDWID" + CAStateMachine.EQUALITY_STR
                + "DKSDKDS";
        assertThrows(NoSuchKeyException.class, () -> sm.processInput(in.toCharArray()));
    }

    @Test
    void testCaptureBrandSimple() throws ClothingAddressParseException {
        String in = CAStateMachine.BRAND_CAPTURE_STR + CAStateMachine.EQUALITY_STR;
        assertTrue(sm.processInput(in.toCharArray())
                instanceof CAStateMachine.StringListCaptureState);
    }

    @Test
    void testCaptureBrandWhitespaceBefore() throws ClothingAddressParseException {
        String in = "\t  \t " + CAStateMachine.BRAND_CAPTURE_STR
                + CAStateMachine.EQUALITY_STR;
        assertTrue(sm.processInput(in.toCharArray())
                instanceof CAStateMachine.StringListCaptureState);
    }

    @Test
    void testCaptureBrandWhitespaceBetween() throws ClothingAddressParseException {
        String in = CAStateMachine.BRAND_CAPTURE_STR
                + " \t  \t" + CAStateMachine.EQUALITY_STR;
        assertTrue(sm.processInput(in.toCharArray())
                instanceof CAStateMachine.StringListCaptureState);
    }

    @Test
    void testCaptureBrandSingleListItem() throws ClothingAddressParseException {
        String in = CAStateMachine.BRAND_CAPTURE_STR
                + CAStateMachine.EQUALITY_STR
                + "BRAND A"
                + CAStateMachine.LIST_END_STR;
        CAStateMachine.State out = sm.processInput(in.toCharArray());
        assertTrue(out instanceof CAStateMachine.CapturingState);
        assertEquals(1, out.getAddress().getBrands().size());
        assertEquals("BRAND A", out.getAddress().getBrands().get(0));
    }

    @Test
    void testCaptureBrandMultipleItems() throws ClothingAddressParseException {
        String in = CAStateMachine.BRAND_CAPTURE_STR
                + CAStateMachine.EQUALITY_STR
                + "BRAND A" + CAStateMachine.LIST_SEPARATOR_STR
                + "BRAND B" + CAStateMachine.LIST_SEPARATOR_STR
                + "BRAND C" + CAStateMachine.LIST_SEPARATOR_STR
                + "BRAND D" + CAStateMachine.LIST_END_STR;
        CAStateMachine.State out = sm.processInput(in.toCharArray());
        assertTrue(out instanceof CAStateMachine.CapturingState);
        assertEquals(4, out.getAddress().getBrands().size());
        assertTrue(out.getAddress().getBrands().containsAll(List.of("BRAND A",
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
                + CAStateMachine.EQUALITY_STR
                + "casual"
                + CAStateMachine.LIST_END_STR
                + CAStateMachine.BRAND_CAPTURE_STR
                + CAStateMachine.EQUALITY_STR
                + "adidas"
                + CAStateMachine.LIST_SEPARATOR_STR
                + "nike"
                + CAStateMachine.LIST_END_STR;
        ClothingAddress out = sm.processInput(in.toCharArray()).getAddress();
        assertEquals(2, out.getBrands().size());
        assertTrue(out.getBrands().containsAll(List.of("adidas", "nike")));
        assertEquals(1, out.getStyles().size());
        assertEquals("casual", out.getStyles().get(0));
    }

    @Test
    void testCaptureType() throws ClothingAddressParseException {
        String in = CAStateMachine.TYPE_CAPTURE_STR
                + CAStateMachine.EQUALITY_STR
                + "pants"
                + CAStateMachine.LIST_END_STR;
        ClothingAddress o = sm.processInput(in.toCharArray()).getAddress();
        assertEquals(1, o.getTypes().size());
        assertEquals("pants", o.getTypes().get(0));
    }

    @Test
    void testCaptureSize() throws ClothingAddressParseException {
        String in = CAStateMachine.SIZE_CAPTURE_STR
                + CAStateMachine.EQUALITY_STR
                + Size.XL
                + CAStateMachine.LIST_SEPARATOR_STR
                + Size.XXL.toString().toLowerCase()
                + CAStateMachine.LIST_END_STR;
        ClothingAddress o = sm.processInput(in.toCharArray()).getAddress();
        assertEquals(2, o.getSizes().size());
        assertTrue(o.getSizes().containsAll(List.of(Size.XL, Size.XXL)));
    }

    @Test
    void testCaptureSizeNull() throws ClothingAddressParseException {
        String in = CAStateMachine.SIZE_CAPTURE_STR
                + CAStateMachine.EQUALITY_STR
                + "djaisjdosajdsad"
                + CAStateMachine.LIST_END_STR;
        ClothingAddress o = sm.processInput(in.toCharArray()).getAddress();
        assertEquals(1, o.getSizes().size());
        assertNull(o.getSizes().get(0));
    }
}