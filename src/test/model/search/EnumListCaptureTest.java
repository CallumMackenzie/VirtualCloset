package model.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EnumListCaptureTest {

    EnumListCapture<TestEnum> strict;
    EnumListCapture<TestEnum> loose;

    @BeforeEach
    void constructListCapture() {
        this.strict = new EnumListCapture<>(true,
                TestEnum.class,
                ",", ";");
        this.loose = new EnumListCapture<>(false,
                TestEnum.class,
                "+", ".");
    }

    @Test
    void testConstructor() {
        assertTrue(this.strict.getTokensCaptured().isEmpty());
        assertTrue(this.loose.getTokensCaptured().isEmpty());
        assertEquals(",", this.strict.getListSeparatorString());
        assertEquals("+", this.loose.getListSeparatorString());
        assertEquals(";", this.strict.getListTerminatorString());
        assertEquals(".", this.loose.getListTerminatorString());
    }

    @Test
    void testIsListFinishedEmpty() {
        assertTrue(this.strict.isListFinished(';'));
        assertTrue(this.strict.getTokensCaptured().isEmpty());

        assertTrue(this.loose.isListFinished('.'));
        assertTrue(this.loose.getTokensCaptured().isEmpty());
    }

    @Test
    void testIsListFinishedMultiItemStrict() {
        assertTrue("VALUE_A, Value_a, VALUE_A, valueA".chars()
                .noneMatch(x -> this.strict.isListFinished((char) x)));
        assertTrue(this.strict.isListFinished(';'));
        assertEquals(4, this.strict.getTokensCaptured().size());
        assertTrue(this.strict.getTokensCaptured().containsAll(List.of(
                TestEnum.VALUE_A, TestEnum.valueA, TestEnum.Value_a
        )));
        assertEquals(2, this.strict.getTokensCaptured()
                .stream().filter(x -> x == TestEnum.VALUE_A).count());
    }

    @Test
    void testIsListFinishedMultiItemLoose() {
        assertTrue("VALUE_A + value_a + value a + valuea".chars()
                .noneMatch(x -> this.loose.isListFinished((char) x)));
        assertTrue(this.loose.isListFinished('.'));
        assertEquals(4, this.loose.getTokensCaptured().size());
        assertTrue(this.loose.getTokensCaptured().containsAll(List.of(
                TestEnum.VALUE_A, TestEnum.valueA
        )));
        assertEquals(3, this.loose.getTokensCaptured()
                .stream().filter(x -> x == TestEnum.VALUE_A).count());
    }

    @Test
    void testStringToEnumLoose() {
        assertEquals(TestEnum.VALUE_A,
                EnumListCapture.stringToEnumLoose(TestEnum.class, "VALUE_A"));
        assertEquals(TestEnum.VALUE_A,
                EnumListCapture.stringToEnumLoose(TestEnum.class, "VALUE A"));
        assertEquals(TestEnum.VALUE_A,
                EnumListCapture.stringToEnumLoose(TestEnum.class, "VALue_A"));
        assertEquals(TestEnum.valueA,
                EnumListCapture.stringToEnumLoose(TestEnum.class, "valueA"));
        assertNull(EnumListCapture.stringToEnumLoose(TestEnum.class, "x"));
    }

    @Test
    void testStringToEnumStrict() {
        assertEquals(TestEnum.VALUE_A,
                EnumListCapture.stringToEnumStrict(TestEnum.class, "VALUE_A"));
        assertNull(EnumListCapture.stringToEnumStrict(TestEnum.class, "VALUE A"));
        assertNull(EnumListCapture.stringToEnumStrict(TestEnum.class, "VALue A"));
        assertEquals(TestEnum.valueA,
                EnumListCapture.stringToEnumStrict(TestEnum.class, "valueA"));
        assertNull(EnumListCapture.stringToEnumStrict(TestEnum.class, "x"));
    }

    enum TestEnum {
        VALUE_A,
        Value_a,
        valueA
    }

}