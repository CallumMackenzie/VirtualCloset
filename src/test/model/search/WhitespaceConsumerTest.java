package model.search;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WhitespaceConsumerTest {

    @Test
    void testReset() {
        WhitespaceConsumer c = new WhitespaceConsumer();
        c.shouldConsumeWhitespace('A');
        c.shouldConsumeWhitespace('B');
        assertFalse(c.shouldConsumeWhitespace(' '));
        c.reset();
        assertTrue(c.shouldConsumeWhitespace(' '));
        assertTrue(c.isConsuming());
    }

    @Test
    void testShouldConsumeWhitespace() {
        WhitespaceConsumer c = new WhitespaceConsumer();
        assertTrue(c.shouldConsumeWhitespace(' '));
        assertTrue(c.shouldConsumeWhitespace('\t'));
        assertFalse(c.shouldConsumeWhitespace('a'));
        assertFalse(c.shouldConsumeWhitespace('\t'));
        assertFalse(c.shouldConsumeWhitespace(' '));
        assertFalse(c.shouldConsumeWhitespace('c'));
    }

    @Test
    void testIsConsuming() {
        WhitespaceConsumer c = new WhitespaceConsumer();
        assertTrue(c.isConsuming());
        c.shouldConsumeWhitespace(' ');
        assertTrue(c.isConsuming());
        c.shouldConsumeWhitespace('\t');
        assertTrue(c.isConsuming());
        c.shouldConsumeWhitespace('c');
        assertFalse(c.isConsuming());
    }
}