package model.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void testProcessPastFound() throws UnexpectedBoolInputException {
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
    void testConsumeWhitespace() throws UnexpectedBoolInputException {
        assertFalse(bc1.foundBoolean("\t  tru"));
        assertTrue(bc1.foundBoolean('e'));
        assertTrue(bc1.getBoolCaptured());
    }

    @Test
    void testFoundBoolean() throws UnexpectedBoolInputException {
        assertFalse(bc1.foundBoolean("fals"));
        assertTrue(bc1.foundBoolean('e'));
        assertFalse(bc1.getBoolCaptured());

        assertFalse(bc2.foundBoolean("ye"));
        assertTrue(bc2.foundBoolean('s'));
        assertTrue(bc2.getBoolCaptured());
    }

    @Test
    void testFoundBooleanFull() throws UnexpectedBoolInputException {
        assertTrue(bc1.foundBoolean("false"));
        assertTrue(bc1.foundBoolean("adksd"));

        assertTrue(bc2.foundBoolean("yes"));
        assertTrue(bc2.foundBoolean("daksjd"));
    }

    @Test
    void testIncorrectInput() {
        assertThrows(UnexpectedBoolInputException.class,
                () -> bc1.foundBoolean('z'));
        assertThrows(UnexpectedBoolInputException.class,
                () -> bc2.foundBoolean('t'));
    }

    @Test
    void testIncorrectInputPartway() {
        assertThrows(UnexpectedBoolInputException.class,
                () -> bc1.foundBoolean("tre"));
        assertThrows(UnexpectedBoolInputException.class,
                () -> bc2.foundBoolean("neads"));
    }
}