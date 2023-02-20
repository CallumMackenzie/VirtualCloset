package model.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StateMachineTest {

    StateMachine<String, Throwable> sm;

    @BeforeEach
    void createStateMachine() {
        this.sm = new StateMachine<>("A") {
            @Override
            public String nextState(String in, char input) {
                return in + input;
            }
        };
    }

    @Test
    void testStep() throws Throwable {
        this.sm.step('a');
        assertEquals("Aa", this.sm.getState());
        this.sm.step('Z');
        assertEquals("AaZ", this.sm.getState());
    }

    @Test
    void testProcessInput() throws Throwable {
        String out = this.sm.processInput("ABCDEF".toCharArray());
        assertEquals("AABCDEF", out);
    }

    @Test
    void testNextState() throws Throwable {
        String next = this.sm.nextState("CURRENT", 'a');
        assertEquals("CURRENTa", next);
    }
}