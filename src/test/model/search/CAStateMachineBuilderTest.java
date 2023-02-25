package model.search;

import org.junit.jupiter.api.Test;

import static model.search.CAStateMachineBuilder.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CAStateMachineBuilderTest {

    @Test
    void testBuildDefault() {
        CAStateMachine def = buildDefault();
        assertEquals(STYLE_CAPTURE_STR, def.styleKey);
        assertEquals(BRAND_CAPTURE_STR, def.brandKey);
        assertEquals(TYPE_CAPTURE_STR, def.typeKey);
        assertEquals(SIZE_CAPTURE_STR, def.sizeKey);
        assertEquals(IS_DIRTY_CAPTURE_STR, def.isDirtyKey);
        assertEquals(MATERIAL_CAPTURE_STR, def.materialKey);
        assertEquals(TRUE_STR, def.trueSymbol);
        assertEquals(FALSE_STR, def.falseSymbol);
        assertEquals(EQUALITY_STR, def.equalitySymbol);
        assertEquals(LIST_SEPARATOR_STR, def.listSeparatorSymbol);
        assertEquals(LIST_END_STR, def.listEndSymbol);
    }

    @Test
    void testBuild() {
        CAStateMachine b = new CAStateMachineBuilder()
                .brandKey("a")
                .equalitySymbol(":")
                .build();
        assertEquals("a", b.brandKey);
        assertEquals(":", b.equalitySymbol);
    }

    @Test
    void testStyleKey() {
        String newVal = "abc";
        CAStateMachine sm = new CAStateMachineBuilder()
                .styleKey(newVal)
                .build();
        assertEquals(newVal, sm.styleKey);
    }

    @Test
    void testBrandKey() {
        String newVal = "def";
        CAStateMachine sm = new CAStateMachineBuilder()
                .brandKey(newVal)
                .build();
        assertEquals(newVal, sm.brandKey);
    }

    @Test
    void testTypeKey() {
        String newVal = "124";
        CAStateMachine sm = new CAStateMachineBuilder()
                .typeKey(newVal)
                .build();
        assertEquals(newVal, sm.typeKey);
    }

    @Test
    void testSizeKey() {
        String newVal = "32132";
        CAStateMachine sm = new CAStateMachineBuilder()
                .sizeKey(newVal)
                .build();
        assertEquals(newVal, sm.sizeKey);
    }

    @Test
    void testIsDirtyKey() {
        String newVal = "dsad";
        CAStateMachine sm = new CAStateMachineBuilder()
                .isDirtyKey(newVal)
                .build();
        assertEquals(newVal, sm.isDirtyKey);
    }

    @Test
    void testMaterialKey() {
        String newVal = "asdsadxy";
        CAStateMachine sm = new CAStateMachineBuilder()
                .materialKey(newVal)
                .build();
        assertEquals(newVal, sm.materialKey);
    }

    @Test
    void testTrueSymbol() {
        String newVal = "x1231321y";
        CAStateMachine sm = new CAStateMachineBuilder()
                .trueSymbol(newVal)
                .build();
        assertEquals(newVal, sm.trueSymbol);
    }

    @Test
    void testFalseSymbol() {
        String newVal = "x12y";
        CAStateMachine sm = new CAStateMachineBuilder()
                .falseSymbol(newVal)
                .build();
        assertEquals(newVal, sm.falseSymbol);
    }

    @Test
    void testEqualitySymbol() {
        String newVal = "xadsdy";
        CAStateMachine sm = new CAStateMachineBuilder()
                .equalitySymbol(newVal)
                .build();
        assertEquals(newVal, sm.equalitySymbol);
    }

    @Test
    void testListSeparatorSymbol() {
        String newVal = "aasaxy";
        CAStateMachine sm = new CAStateMachineBuilder()
                .listSeparatorSymbol(newVal)
                .build();
        assertEquals(newVal, sm.listSeparatorSymbol);
    }

    @Test
    void testListEndSymbol() {
        String newVal = "xadsdy";
        CAStateMachine sm = new CAStateMachineBuilder()
                .listEndSymbol(newVal)
                .build();
        assertEquals(newVal, sm.listEndSymbol);
    }
}