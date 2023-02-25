package model.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntegerCaptureTest {

    IntegerCapture ic;

    @BeforeEach
    void setup() {
        this.ic = new IntegerCapture("ABC");
    }

    @Test
    void testFoundIntegerSimple() {
        assertFalse(this.ic.foundInteger(' ')
                & this.ic.foundInteger('1')
                & this.ic.foundInteger('2')
                & this.ic.foundInteger('A')
                & this.ic.foundInteger('B'));
        assertTrue(this.ic.foundInteger('C'));
        assertEquals(12, this.ic.getIntegerCaptured());
    }

    @Test
    void testIntegerThrows() {
        assertThrows(NumberFormatException.class,
                () -> this.ic.foundInteger('a'));
    }
}