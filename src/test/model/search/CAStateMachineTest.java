package model.search;

import model.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class CAStateMachineTest {

    CAStateMachine sm;

    @BeforeEach
    void createStateMachine() {
        this.sm = CAStateMachineBuilder.buildDefault();
    }

    @Test
    void testUnknownKey() {
        String in = "UNINDWID" + sm.equalitySymbol + "DKSDKDS";
        assertThrows(NoSuchKeyException.class, () -> sm.processInput(in.toCharArray()));
    }

    @Test
    void testCaptureBrandSimple() throws ClothingAddressParseException {
        String in = sm.brandKey + sm.equalitySymbol;
        assertTrue(sm.processInput(in.toCharArray())
                instanceof CAStateMachine.StringListCaptureState);
    }

    @Test
    void testCaptureBrandWhitespaceBefore() throws ClothingAddressParseException {
        String in = "\t  \t " + sm.brandKey + sm.equalitySymbol;
        assertTrue(sm.processInput(in.toCharArray())
                instanceof CAStateMachine.StringListCaptureState);
    }

    @Test
    void testCaptureBrandWhitespaceBetween() throws ClothingAddressParseException {
        String in = sm.brandKey + " \t  \t" + sm.equalitySymbol;
        assertTrue(sm.processInput(in.toCharArray())
                instanceof CAStateMachine.StringListCaptureState);
    }

    @Test
    void testCaptureBrandSingleListItem() throws ClothingAddressParseException {
        String in = sm.brandKey + sm.equalitySymbol + "BRAND A" + sm.listEndSymbol;
        CAStateMachine.State out = sm.processInput(in.toCharArray());
        assertTrue(out instanceof CAStateMachine.CapturingState);
        assertEquals(1, out.getAddress().getBrands().size());
        assertEquals("BRAND A", out.getAddress().getBrands().get(0));
    }

    @Test
    void testCaptureBrandMultipleItems() throws ClothingAddressParseException {
        String in = sm.brandKey
                + sm.equalitySymbol
                + "BRAND A" + sm.listSeparatorSymbol
                + "BRAND B" + sm.listSeparatorSymbol
                + "BRAND C" + sm.listSeparatorSymbol
                + "BRAND D" + sm.listEndSymbol;
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
        String in = sm.styleKey
                + sm.equalitySymbol
                + "casual" + sm.listEndSymbol
                + sm.brandKey + sm.equalitySymbol
                + "adidas" + sm.listSeparatorSymbol
                + "nike" + sm.listEndSymbol;
        ClothingAddress out = sm.processInput(in.toCharArray()).getAddress();
        assertEquals(2, out.getBrands().size());
        assertTrue(out.getBrands().containsAll(Arrays.asList("adidas", "nike")));
        assertEquals(1, out.getStyles().size());
        assertEquals("casual", out.getStyles().get(0));
    }

    @Test
    void testCaptureType() throws ClothingAddressParseException {
        String in = sm.typeKey
                + sm.equalitySymbol
                + "pants" + sm.listEndSymbol;
        ClothingAddress o = sm.processInput(in.toCharArray()).getAddress();
        assertEquals(1, o.getTypes().size());
        assertEquals("pants", o.getTypes().get(0));
    }

    @Test
    void testCaptureSize() throws ClothingAddressParseException {
        String in = sm.sizeKey
                + sm.equalitySymbol
                + Size.XL + sm.listSeparatorSymbol
                + Size.XXL.toString().toLowerCase()
                + sm.listEndSymbol;
        ClothingAddress o = sm.processInput(in.toCharArray()).getAddress();
        assertEquals(2, o.getSizes().size());
        assertTrue(o.getSizes().containsAll(Arrays.asList(Size.XL, Size.XXL)));
    }

    @Test
    void testCaptureSizeUnknown() {
        String in = sm.sizeKey + sm.equalitySymbol
                + "djaisjdosajdsad" + sm.listEndSymbol;
        assertThrows(UnexpectedInputException.class,
                () -> sm.processInput(in.toCharArray()));
    }

    @Test
    void testCaptureIsDirtyTrue() throws ClothingAddressParseException {
        String in = sm.isDirtyKey + sm.equalitySymbol + "  \t "
                + sm.trueSymbol;
        ClothingAddress o = sm.processInput(in.toCharArray()).getAddress();
        assertEquals(true, o.getIsDirty());
    }

    @Test
    void testCaptureIsDirtyFalse() throws ClothingAddressParseException {
        String in = sm.isDirtyKey + sm.equalitySymbol + "  \t "
                + sm.falseSymbol;
        ClothingAddress o = sm.processInput(in.toCharArray()).getAddress();
        assertEquals(false, o.getIsDirty());
    }

    @Test
    void testCaptureIsDirtyInvalid() {
        String in = sm.isDirtyKey + sm.equalitySymbol + "ab";
        UnexpectedInputException ex = assertThrows(UnexpectedInputException.class,
                () -> sm.processInput(in.toCharArray()));
        assertTrue(ex.getErrorState() instanceof CAStateMachine.BooleanCaptureState);
    }

    @Test
    void testStateCapturedEmpty() throws ClothingAddressParseException {
        String in = sm.materialKey + sm.equalitySymbol + "abcd" + sm.listEndSymbol;
        CAStateMachine.State out = sm.processInput(in.toCharArray());
        assertTrue(out.getStateCaptured().isEmpty());
    }

    @Test
    void testStateCapturedFull() throws ClothingAddressParseException {
        CAStateMachine.State out = sm
                .processInput(sm.materialKey.toCharArray());
        assertEquals(sm.materialKey, out.getStateCaptured());
        String input2 = sm.equalitySymbol + "ABCD";
        CAStateMachine.State out2 = sm.processInput(input2.toCharArray());
        assertEquals("ABCD", out2.getStateCaptured());
    }

    void testCaptureAllWith(CAStateMachine sm) {
        String in = "\t   \t\t"
                // BRANDS
                + sm.brandKey + sm.equalitySymbol + "adidas"
                + sm.listSeparatorSymbol + "lululemon" + sm.listEndSymbol
                // IS DIRTY
                + sm.isDirtyKey + sm.equalitySymbol + sm.falseSymbol + " "
                // STYLE
                + sm.styleKey + sm.equalitySymbol + "casual" + sm.listEndSymbol
                // TYPE
                + sm.typeKey + sm.equalitySymbol + "shirt" + sm.listEndSymbol
                // SIZE
                + sm.sizeKey + sm.equalitySymbol + Size.XXL
                + sm.listSeparatorSymbol + Size.S
                + sm.listEndSymbol
                // MATERIAL
                + sm.materialKey + sm.equalitySymbol + "cotton" + sm.listEndSymbol;
        ClothingAddress o = null;
        try {
            o = sm.processInput(in.toCharArray()).getAddress();
        } catch (ClothingAddressParseException e) {
            fail("Unexpected exception: " + e);
        }
        assertNotNull(o);
        // BRANDS
        assertEquals(2, o.getBrands().size());
        assertTrue(o.getBrands().containsAll(Arrays.asList("lululemon", "adidas")),
                o.getBrands().toString());
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
        assertTrue(o.getSizes().containsAll(Arrays.asList(Size.XXL, Size.S)),
                o.getSizes().toString());
        // MATERIAL
        assertEquals(1, o.getMaterials().size());
        assertEquals("cotton", o.getMaterials().get(0));
    }

    @Test
    void testCaptureAll() {
        this.testCaptureAllWith(sm);
    }
}