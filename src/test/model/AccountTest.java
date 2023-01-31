package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {

    Account ac1, ac2;

    @BeforeEach
    void constructAccount() {
        this.ac1 = new Account("Callum");
        this.ac2 = new Account("Jake");
    }

    @Test
    void testConstructor() {
        assertEquals(this.ac1.getName(), "Callum");
    }

    @Test
    void testSetName() {
        boolean result = this.ac1.setName("Eric", new ArrayList<>());
        assertTrue(result);
        assertEquals(this.ac1.getName(), "Eric");

        result = this.ac1.setName("ABC", List.of(ac1));
        assertTrue(result);
        assertEquals(this.ac1.getName(), "ABC");

        result = this.ac1.setName("Jake", List.of(ac2));
        assertFalse(result);
        assertEquals(this.ac1.getName(), "ABC");
    }

}
