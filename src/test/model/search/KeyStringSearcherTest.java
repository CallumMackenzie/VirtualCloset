package model.search;

import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class KeyStringSearcherTest {

    @Test
    void testConstructor() {
        KeyStringSearcher searcher = new KeyStringSearcher("123", s -> {
        });
        assertEquals("123", searcher.getKey());
        searcher = new KeyStringSearcher("4", s -> {
        });
        assertEquals("4", searcher.getKey());
    }

    @Test
    void testIsMatching() {
        KeyStringSearcher s = new KeyStringSearcher("1234", z -> {
        });
        assertFalse(s.isMatching());
        s.tryFindKey('1');
        assertTrue(s.isMatching());
        s.tryFindKey('b');
        assertFalse(s.isMatching());
    }

    @Test
    void testReset() {
        KeyStringSearcher searcher = new KeyStringSearcher("ABC", s -> {
        });
        searcher.tryFindKey('A');
        assertTrue(searcher.isMatching());
        searcher.reset();
        assertFalse(searcher.isMatching());

        searcher.tryFindKey('A');
        searcher.tryFindKey('B');
        searcher.reset();

        assertNotEquals(KeyStringSearcher.MatchState.MATCH,
                searcher.tryFindKey('C'));
    }

    @Test
    void testTryFindKeySimple() {
        KeyStringSearcher s = new KeyStringSearcher("A", z -> {
        });
        assertTrue(s.tryFindKey('Z').wasNoMatch());
        assertTrue(s.tryFindKey('A').wasMatch());
        assertTrue(s.tryFindKey('B').wasMatch());
    }

    @Test
    void testTryFindKeyLen2() {
        Stack<String> capture = new Stack<>();
        KeyStringSearcher s = new KeyStringSearcher("AB", capture::push);

        assertTrue(s.tryFindKey('B').wasNoMatch());
        assertTrue(s.tryFindKey('A').wasPartialMatch());
        assertTrue(capture.isEmpty());
        assertTrue(s.tryFindKey('C').wasNoMatch());
        assertFalse(capture.isEmpty());
        assertEquals(1, capture.size());
        assertEquals("A", capture.pop());
        assertTrue(s.tryFindKey('A').wasPartialMatch());
        assertTrue(s.tryFindKey('B').wasMatch());
        assertTrue(capture.isEmpty());
    }

    @Test
    void testTryFindKeyLen3() {
        Stack<String> capture = new Stack<>();
        KeyStringSearcher searcher = new KeyStringSearcher("QQQ", capture::push);

        assertTrue(searcher.tryFindKey('Q').wasPartialMatch());
        assertTrue(searcher.tryFindKey('S').wasNoMatch());
        assertEquals(1, capture.size());

        assertTrue(searcher.tryFindKey('Q').wasPartialMatch());
        assertTrue(searcher.tryFindKey('Q').wasPartialMatch());
        assertTrue(searcher.tryFindKey('Q').wasMatch());
    }

    @Test
    void testTryFindKeyBrokenByStartChar() {
        Stack<String> capture = new Stack<>();
        KeyStringSearcher s = new KeyStringSearcher("A;", capture::push);
        assertTrue(s.tryFindKey('A').wasPartialMatch());
        assertTrue(s.tryFindKey('A').wasPartialMatch());
        assertEquals(1, capture.size());
        assertEquals("A", capture.pop());
    }
}