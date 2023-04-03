package model.search;

import org.junit.jupiter.api.Test;

import static model.search.MatchState.*;
import static org.junit.jupiter.api.Assertions.*;

class KeyStringSearcherTest {

    @Test
    void testConstructor() {
        KeyStringSearcher searcher = new KeyStringSearcher("123");
        assertEquals("123", searcher.getKey());
        searcher = new KeyStringSearcher("4");
        assertEquals("4", searcher.getKey());
        assertEquals("", searcher.getPartialMatch());
        assertEquals(-1, searcher.getCurrentKeyIndex());
    }

    @Test
    void testMatchStateMatch() {
        assertTrue(MATCH.wasMatch());
        assertFalse(MATCH.wasPartialMatch());
        assertFalse(MATCH.wasNoMatch());
        assertFalse(MATCH.wasMatchRestarted());
        assertFalse(MATCH.wasMatchBroken());
        assertFalse(MATCH.matchError());
    }

    @Test
    void testMatchStatePartialMatchBroken() {
        assertFalse(MATCH_BROKEN.wasMatch());
        assertFalse(MATCH_BROKEN.wasPartialMatch());
        assertFalse(MATCH_BROKEN.wasNoMatch());
        assertFalse(MATCH_BROKEN.wasMatchRestarted());
        assertTrue(MATCH_BROKEN.wasMatchBroken());
        assertTrue(MATCH_BROKEN.matchError());
    }

    @Test
    void testMatchStatePartialMatch() {
        assertFalse(PARTIAL_MATCH.wasMatch());
        assertTrue(PARTIAL_MATCH.wasPartialMatch());
        assertFalse(PARTIAL_MATCH.wasNoMatch());
        assertFalse(PARTIAL_MATCH.wasMatchRestarted());
        assertFalse(PARTIAL_MATCH.wasMatchBroken());
        assertFalse(PARTIAL_MATCH.matchError());
    }

    @Test
    void testMatchStateNoMatch() {
        assertFalse(NO_MATCH.wasMatch());
        assertFalse(NO_MATCH.wasPartialMatch());
        assertTrue(NO_MATCH.wasNoMatch());
        assertFalse(NO_MATCH.wasMatchRestarted());
        assertFalse(NO_MATCH.wasMatchBroken());
        assertTrue(NO_MATCH.matchError());
    }

    @Test
    void testMatchStatePartialMatchRestarted() {
        assertFalse(MATCH_RESTARTED.wasMatch());
        assertFalse(MATCH_RESTARTED.wasPartialMatch());
        assertFalse(MATCH_RESTARTED.wasNoMatch());
        assertTrue(MATCH_RESTARTED.wasMatchRestarted());
        assertFalse(MATCH_RESTARTED.wasMatchBroken());
        assertTrue(MATCH_RESTARTED.matchError());
    }

    @Test
    void testIsMatching() {
        KeyStringSearcher s = new KeyStringSearcher("1234");
        s.tryFindKey('1');
        assertEquals(0, s.getCurrentKeyIndex());
        assertTrue(s.isMatching());
        s.tryFindKey('b');
        assertEquals(-1, s.getCurrentKeyIndex());
        assertFalse(s.isMatching());
        s.tryFindKey('1');
        s.tryFindKey('2');
        s.tryFindKey('3');
        assertEquals(2, s.getCurrentKeyIndex());
        assertTrue(s.isMatching());
    }

    @Test
    void testReset() {
        KeyStringSearcher searcher = new KeyStringSearcher("ABC");
        searcher.reset();
        assertFalse(searcher.isMatching());
        assertEquals(-1, searcher.getCurrentKeyIndex());

        assertTrue(searcher.tryFindKey('A').wasPartialMatch());
        assertTrue(searcher.tryFindKey('A').wasMatchRestarted());
        assertTrue(searcher.isMatching());
        assertEquals(0, searcher.getCurrentKeyIndex());
        assertEquals("A", searcher.getPartialMatch());
        searcher.reset();
        assertEquals(-1, searcher.getCurrentKeyIndex());
        assertFalse(searcher.isMatching());
        assertEquals("", searcher.getPartialMatch());

        searcher.tryFindKey('A');
        searcher.tryFindKey('B');
        assertTrue(searcher.isMatching());
        searcher.reset();
        assertFalse(searcher.isMatching());

        assertNotEquals(MATCH,
                searcher.tryFindKey('C'));
    }

    @Test
    void testTryFindKeySimple() {
        KeyStringSearcher s = new KeyStringSearcher("A");
        assertTrue(s.tryFindKey('Z').wasNoMatch());
        assertTrue(s.tryFindKey('A').wasMatch());
        assertTrue(s.tryFindKey('B').wasMatch());
    }

    @Test
    void testTryFindKeyLen2() {
        KeyStringSearcher s = new KeyStringSearcher("AB");

        assertTrue(s.tryFindKey('B').wasNoMatch());
        assertTrue(s.tryFindKey('A').wasPartialMatch());
        assertTrue(s.tryFindKey('C').wasMatchBroken());
        assertTrue(s.tryFindKey('C').wasNoMatch());
        assertTrue(s.tryFindKey('A').wasPartialMatch());
        assertTrue(s.tryFindKey('A').wasMatchRestarted());
        assertTrue(s.tryFindKey('P').wasMatchBroken());

        assertTrue(s.tryFindKey('A').wasPartialMatch());
        assertTrue(s.tryFindKey('B').wasMatch());
    }

    @Test
    void testTryFindKeyLen3() {
        KeyStringSearcher searcher = new KeyStringSearcher("QQQ");

        assertTrue(searcher.tryFindKey('Q').wasPartialMatch());
        assertTrue(searcher.tryFindKey('S').wasMatchBroken());

        assertTrue(searcher.tryFindKey('Q').wasPartialMatch());
        assertTrue(searcher.tryFindKey('Q').wasPartialMatch());
        assertTrue(searcher.tryFindKey('Q').wasMatch());
    }

    @Test
    void testTryFindKeyBrokenByStartChar() {
        KeyStringSearcher s = new KeyStringSearcher("A;");
        assertTrue(s.tryFindKey('A').wasPartialMatch());
        assertEquals("A", s.getPartialMatch());
        assertTrue(s.tryFindKey('A').wasMatchRestarted());
        assertEquals("A", s.getPartialMatch());
        assertTrue(s.tryFindKey('A').wasMatchRestarted());
        assertEquals("A", s.getPartialMatch());
        assertTrue(s.tryFindKey('D').wasMatchBroken());
        assertEquals("A", s.getPartialMatch());
    }

    @Test
    void testTryFindKeyLong() {
        KeyStringSearcher s = new KeyStringSearcher("ABDJSDJAND");
        assertTrue(s.tryFindKey('B').wasNoMatch());
        assertTrue(s.tryFindKey('A').wasPartialMatch());
        assertEquals("A", s.getPartialMatch());
        assertTrue(s.tryFindKey('B').wasPartialMatch());
        assertEquals("AB", s.getPartialMatch());
        assertTrue(s.tryFindKey('D').wasPartialMatch());
        assertEquals("ABD", s.getPartialMatch());
        assertTrue(s.tryFindKey('D').wasMatchBroken());
        assertEquals("ABD", s.getPartialMatch());
        assertTrue(s.tryFindKey('D').wasNoMatch());
        for (char c : "ABDJSDJAN".toCharArray()) {
            assertTrue(s.tryFindKey(c).wasPartialMatch());
        }
        assertTrue(s.tryFindKey('Z').wasMatchBroken());
        assertEquals("ABDJSDJAN", s.getPartialMatch());

        for (char c : "ABDJSDJAN".toCharArray()) {
            assertTrue(s.tryFindKey(c).wasPartialMatch());
        }
        assertTrue(s.tryFindKey('A').wasMatchRestarted());
        assertEquals("ABDJSDJAN", s.getPartialMatch());
    }
}