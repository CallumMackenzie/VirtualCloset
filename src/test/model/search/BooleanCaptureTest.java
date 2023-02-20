package model.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.IntPredicate;

import static org.junit.jupiter.api.Assertions.*;

class BooleanCaptureTest {

    BooleanCapture bc1;
    BooleanCapture bc2;

    @BeforeEach
    void setUp() {
        this.bc1 = new BooleanCapture("true",
                "false");
        this.bc2 = new BooleanCapture("yes",
                "no");
    }

    @Test
    void testProcessPastFound() throws UnexpectedInputException {
        assertTrue(this.bc1.foundBoolean("true"));
        assertTrue(this.bc1.foundBoolean("xyz"));
        assertTrue(this.bc2.foundBoolean("nope"));
    }

    @Test
    void testConstructor() {
        assertEquals("true", this.bc1.getTrueKey());
        assertEquals("false", this.bc1.getFalseKey());
        assertEquals("yes", this.bc2.getTrueKey());
        assertEquals("no", this.bc2.getFalseKey());
    }

    @Test
    void testConsumeWhitespace() throws UnexpectedInputException {
        assertFalse(bc1.foundBoolean("\t  tru"));
        assertTrue(bc1.foundBoolean('e'));
        assertTrue(bc1.getBoolCaptured());
    }

    @Test
    void foundBoolean() throws UnexpectedInputException {
        assertFalse(bc1.foundBoolean("fals"));
        assertTrue(bc1.foundBoolean('e'));
        assertFalse(bc1.getBoolCaptured());

        assertFalse(bc2.foundBoolean("ye"));
        assertTrue(bc2.foundBoolean('s'));
        assertTrue(bc2.getBoolCaptured());
    }

    @Test
    void testIncorrectInput() {
        assertThrows(UnexpectedInputException.class,
                () -> bc1.foundBoolean('z'));
        assertThrows(UnexpectedInputException.class,
                () -> bc2.foundBoolean('t'));
    }
}