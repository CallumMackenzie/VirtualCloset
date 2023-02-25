package model.search;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static model.search.StringListCapture.reclaimListCapture;
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
        assertTrue(sc.apply(" \t ab & pq & rs  & a  ")
                .stream().noneMatch(Boolean::booleanValue));
        assertTrue(sc.isListFinished('.'));
        assertEquals(4, sc.getTokensCaptured().size());
        assertTrue(sc.getTokensCaptured().containsAll(Arrays.asList("ab", "pq", "rs", "a")),
                sc.getTokensCaptured().toString());
    }

    @Test
    void testIsListFinishedRepeatingChars() {
        StringListCapture sc = new StringListCapture(",", "D;");
        assertTrue(sc.apply("BRAND D, BRAND D, BRAND DD")
                .stream().noneMatch(Boolean::booleanValue));
        assertFalse(sc.isListFinished('D'));
        assertTrue(sc.isListFinished(';'));
    }

//    @Test
//    void testMutliItemComplexKeys() {
//        testSeparatorEndSymbols("21313njdasn", "asdknsad");
//        testSeparatorEndSymbols(";dspo[21]", "lkkn2133;");
//        testSeparatorEndSymbols("xl123", "321xladsjkd");
//    }

    @Test
    void testSimilarKeys() {
        testSeparatorEndSymbols("similarx", "similary");
        testSeparatorEndSymbols("abcd3", "abcd4");
        testSeparatorEndSymbols("123_4_56", "123_1_56");
        testSeparatorEndSymbols("123556", "123456");
    }

    void testSeparatorEndSymbols(String ls, String le) {
        StringListCapture slc = new StringListCapture(ls, le);
        String in = "a" + ls + "b" + ls + "c" + ls + "s" + le;
        assertTrue(slc.apply(in).stream()
                .anyMatch(Boolean::booleanValue));
        assertEquals(4, slc.getTokensCaptured().size(),
                slc.getTokensCaptured().toString());
        assertTrue(slc.getTokensCaptured().containsAll(Arrays.asList(
                "a", "b", "c", "s"
        )), slc.getTokensCaptured().toString());

        slc = new StringListCapture(ls, le);
        in = "abc" + ls + "def" + ls + "ghi" + ls + "jkl" + le;
        assertTrue(slc.apply(in).stream()
                .anyMatch(Boolean::booleanValue));
        assertEquals(4, slc.getTokensCaptured().size());
        assertTrue(slc.getTokensCaptured().containsAll(Arrays.asList("abc",
                "def", "ghi", "jkl")), slc.getTokensCaptured().toString());
    }

    @Test
    void testOneBrokenOneRestarted() {
        StringListCapture slc = new StringListCapture("abc",
                "bcd");
        for (char c : "12 ababc 13 bc".toCharArray()) {
            assertFalse(slc.isListFinished(c));
        }
        assertTrue(slc.isListFinished('d'));
        assertEquals(2, slc.getTokensCaptured().size());
        assertTrue(slc.getTokensCaptured().containsAll(Arrays.asList(
                "12 ab", "13"
        )), slc.getTokensCaptured().toString());
    }

    @Test
    void testBothBrokenSameTimeListSepLonger() {
        StringListCapture slc = new StringListCapture("1pqrd",
                "pqrs");
        for (char c : "12 1pqrd 13 1pqr".toCharArray()) {
            assertFalse(slc.isListFinished(c));
        }
        assertTrue(slc.isListFinished('s'));
        assertEquals(2, slc.getTokensCaptured().size());
        assertTrue(slc.getTokensCaptured().containsAll(Arrays.asList("12",
                "13 1")), slc.getTokensCaptured().toString());
    }

    @Test
    void testBothBrokenSameTimeListEndLonger() {
        StringListCapture slc = new StringListCapture("abcd",
                "aabce");
        for (char c : "aabczaabc".toCharArray()) {
            assertFalse(slc.isListFinished(c));
        }
        assertTrue(slc.isListFinished('e'));
        assertEquals(1, slc.getTokensCaptured().size());
        assertEquals("aabcz", slc.getTokensCaptured().get(0));
    }

    @Test
    void testReclaimListCaptureSame() {
        KeyStringSearcher kss1 = new KeyStringSearcher("1b");
        KeyStringSearcher kss2 = new KeyStringSearcher("1a");
        kss1.tryFindKey('1');
        kss2.tryFindKey('1');
        assertEquals("", reclaimListCapture(kss1, kss2));
        assertEquals("", reclaimListCapture(kss2, kss2));
    }

    @Test
    void testReclaimListCaptureDiffMatchStates() {
        KeyStringSearcher kss1 = new KeyStringSearcher("12a");
        KeyStringSearcher kss2 = new KeyStringSearcher("a12");
        kss1.tryFindKey('1');
        kss2.tryFindKey('1');
        kss1.tryFindKey('1');
        kss2.tryFindKey('1');
        assertEquals("1", reclaimListCapture(kss1, kss2));
        assertEquals("", reclaimListCapture(kss2, kss1));
    }
}