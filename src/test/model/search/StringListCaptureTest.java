package model.search;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class StringListCaptureTest {

    @Test
    void testConstructor() {
        StringListCapture sc = new StringListCapture("ABC",
                "DEF");
        assertEquals("ABC", sc.getListSeparatorString());
        assertEquals("DEF", sc.getListTerminatorString());
    }

    @Test
    void testIsListFinishedNoItems() {
        StringListCapture sc = new StringListCapture(",", ";");
        assertTrue(sc.isListFinished(';'));
        assertTrue(sc.getTokensCaptured().isEmpty());
    }

    @Test
    void testIsListFinished3ItemsSimpleKey() {
        StringListCapture sc = new StringListCapture("+", "x");
        assertFalse(sc.isListFinished('f')
                & sc.isListFinished('+')
                & sc.isListFinished('t')
                & sc.isListFinished('+')
                & sc.isListFinished('i'));
        assertTrue(sc.isListFinished('x'));
        assertEquals(3, sc.getTokensCaptured().size());
        assertTrue(sc.getTokensCaptured().containsAll(Arrays.asList("f", "t", "i")));
    }

    @Test
    void testIsListFinishedOneItemComplexKey() {
        StringListCapture sc = new StringListCapture("+", "fin");
        assertFalse(sc.isListFinished('f')
                & sc.isListFinished('i')
                & sc.isListFinished('t')
                & sc.isListFinished('f')
                & sc.isListFinished('i'));
        assertTrue(sc.isListFinished('n'));
        assertEquals(1, sc.getTokensCaptured().size());
        assertEquals("fit", sc.getTokensCaptured().get(0));
    }

    @Test
    void testIsListFinishedWhitespace() {
        StringListCapture sc = new StringListCapture("&", ".");
        assertTrue(" \t ab & pq & rs  & a  ".chars()
                .noneMatch(x -> sc.isListFinished((char) x)));
        assertTrue(sc.isListFinished('.'));
        assertEquals(4, sc.getTokensCaptured().size());
        assertTrue(sc.getTokensCaptured().containsAll(Arrays.asList("ab", "pq", "rs", "a")));
    }

    @Test
    void testIsListFinishedRepeatingChars() {
        StringListCapture sc = new StringListCapture(",", "D;");
        assertTrue("BRAND D, BRAND D, BRAND DD".chars()
                .noneMatch(x -> sc.isListFinished((char) x)));
        assertFalse(sc.isListFinished('D'));
        assertTrue(sc.isListFinished(';'));
    }
}