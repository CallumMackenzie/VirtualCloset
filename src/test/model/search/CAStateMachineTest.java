package model.search;

import model.search.CAStateMachine;
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
                instanceof CAStateMachine.BrandCaptureState);
    }

    @Test
    void testCaptureBrandWhitespaceBefore() {
        String in = "\t  \t " + CAStateMachine.BRAND_CAPTURE_STR
                + CAStateMachine.EQUALITY_STR;
        assertTrue(sm.processInput(in.toCharArray())
                instanceof CAStateMachine.BrandCaptureState);
    }

    @Test
    void testCaptureBrandWhitespaceBetween() {
        String in = CAStateMachine.BRAND_CAPTURE_STR
                + " \t  \t" + CAStateMachine.EQUALITY_STR;
        assertTrue(sm.processInput(in.toCharArray())
                instanceof CAStateMachine.BrandCaptureState);
    }

    @Test
    void testCaptureBrandSingleListItem() {
        String in = CAStateMachine.BRAND_CAPTURE_STR
                + CAStateMachine.EQUALITY_STR
                + "BRAND A"
                + CAStateMachine.LIST_END_STR;
        CAStateMachine.State out = sm.processInput(in.toCharArray());
        assertTrue(out instanceof CAStateMachine.CapturingState);
        assertEquals(1, out.address.getBrands().size());
        assertEquals("BRAND A", out.address.getBrands().get(0));
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
        assertEquals(4, out.address.getBrands().size());
        assertTrue(out.address.getBrands().containsAll(List.of("BRAND A",
                "BRAND B",
                "BRAND C",
                "BRAND D")));
    }

    @Test
    void testNoCapture() {
        assertTrue(sm.processInput("".toCharArray())
                instanceof CAStateMachine.CapturingState);
    }
}