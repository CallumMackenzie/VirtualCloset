package model.search;

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
    void testCaptureBrandSimple() {
        String in = CAStateMachine.BRAND_CAPTURE_STR + CAStateMachine.EQUALITY_STR;
        assertTrue(sm.processInput(in.toCharArray())
                instanceof CAStateMachine.StringListCaptureState);
    }

    @Test
    void testCaptureBrandWhitespaceBefore() {
        String in = "\t  \t " + CAStateMachine.BRAND_CAPTURE_STR
                + CAStateMachine.EQUALITY_STR;
        assertTrue(sm.processInput(in.toCharArray())
                instanceof CAStateMachine.StringListCaptureState);
    }

    @Test
    void testCaptureBrandWhitespaceBetween() {
        String in = CAStateMachine.BRAND_CAPTURE_STR
                + " \t  \t" + CAStateMachine.EQUALITY_STR;
        assertTrue(sm.processInput(in.toCharArray())
                instanceof CAStateMachine.StringListCaptureState);
    }

    @Test
    void testCaptureBrandSingleListItem() {
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
    void testCaptureBrandMultipleItems() {
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
    void testNoCapture() {
        assertTrue(sm.processInput("".toCharArray())
                instanceof CAStateMachine.CapturingState);
    }

    @Test
    void testCaptureBrandStyle() {
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
}