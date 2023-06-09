package model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

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
        assertNotNull(this.ac1.getCatalogue());
        assertNotNull(this.ac1.getClosets());
        assertTrue(this.ac1.getClosets().isEmpty());
    }

    @Test
    void testSetName() {
        boolean result = this.ac1.setName("Eric", new ArrayList<>());
        assertTrue(result);
        assertEquals(this.ac1.getName(), "Eric");

        result = this.ac1.setName("ABC", Collections.singletonList(ac1));
        assertTrue(result);
        assertEquals(this.ac1.getName(), "ABC");

        result = this.ac1.setName("Jake", Collections.singletonList(ac2));
        assertFalse(result);
        assertEquals(this.ac1.getName(), "ABC");
    }

    @Test
    void testAddCloset() {
        assertTrue(this.ac1.addCloset("A"));
        assertFalse(this.ac1.addCloset("A"));
        assertTrue(this.ac1.addCloset("B"));
        assertTrue(this.ac1.getClosets()
                .stream().anyMatch(c -> c.getName().equals("A")));
        assertTrue(this.ac1.getClosets()
                .stream().anyMatch(c -> c.getName().equals("B")));
        assertEquals(this.ac1.getClosets().size(), 2);
    }

    @Test
    void testRemoveCloset() {
        this.ac1.addCloset("A");
        this.ac1.addCloset("C");
        assertFalse(this.ac1.removeCloset("B"));
        assertTrue(this.ac1.removeCloset("A"));
        assertEquals(this.ac1.getClosets().size(), 1);

        assertTrue(this.ac1.removeCloset("C"));
        assertTrue(this.ac1.getClosets().isEmpty());

        assertFalse(this.ac1.removeCloset("C"));
    }

    @Test
    void testHasCloset() {
        assertFalse(this.ac1.hasCloset("A"));
        this.ac1.addCloset("A");
        assertTrue(this.ac1.hasCloset("A"));
    }

    @Test
    void testGetCloset() {
        assertFalse(this.ac2.getCloset("A").isPresent());
        this.ac2.addCloset("A");
        this.ac2.addCloset("B");
        assertTrue(this.ac2.getCloset("A").isPresent());
        assertEquals(this.ac2.getCloset("A").get().getName(), "A");
    }

    @Test
    void testToJson() {
        JSONObject jso = this.ac1.toJson(null);
        assertEquals(ac1.getName(), jso.getString(Account.JSON_NAME_KEY));
        assertNotNull(jso.getJSONObject(Account.JSON_CATALOGUE_KEY));
        JSONArray jsa = jso.getJSONArray(Account.JSON_CLOSETS_KEY);
        assertEquals(ac1.getClosets().size(), jsa.length());
    }

    @Test
    void testFromJson() {
        Clothing shirt1 = new Clothing(Collections.singletonList("shirt"),
                Size.XL,
                "Adidas",
                "Silk",
                Collections.singletonList("casual"),
                Collections.singletonList("blue"),
                true);
        this.ac1.addCloset("ABC");
        this.ac1.getCloset("ABC")
                .ifPresent(c -> c.addClothing(shirt1));
        JSONObject jso = this.ac1.toJson(null);
        Account a = Account.fromJson(jso);
        assertEquals(1, a.getClosets().size());
        assertTrue(a.getCloset("ABC").isPresent());
        assertTrue(a.getCloset("ABC").get().getClothing().contains(shirt1));
    }

    @Test
    void testEquals() {
        assertNotEquals(null, this.ac1);
        assertEquals(this.ac1, this.ac1);
        assertNotEquals(this.ac1, this.ac2);
    }

}
