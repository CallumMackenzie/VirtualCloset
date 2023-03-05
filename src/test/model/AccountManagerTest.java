package model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AccountManagerTest {

    AccountManager acm;
    Account jake, eric;

    @BeforeEach
    void constructAccountManager() {
        this.acm = new AccountManager();
        jake = new Account("Jake");
        eric = new Account("Eric");
    }

    @Test
    void testConstructor() {
        assertEquals(0, this.acm.getAccounts().size());
        assertEquals(AccountManager.FILE_SAVE_PATH, this.acm.getFileSavePath());
        List<Account> acs = new ArrayList<>();
        acs.add(jake);
        this.acm = new AccountManager(acs);
        this.acm.setFileSavePath("ABCD");
        assertEquals(1, this.acm.getAccounts().size());
        assertEquals(jake, this.acm.getAccounts().get(0));
        assertEquals("ABCD", acm.getFileSavePath());
    }

    @Test
    void testAddAccount() {
        assertTrue(this.acm.addAccount(jake));
        assertEquals(this.acm.getAccounts().size(), 1);
        assertEquals(this.acm.getAccounts().get(0), jake);

        assertTrue(this.acm.addAccount(eric));
        assertEquals(this.acm.getAccounts().size(), 2);
        assertEquals(this.acm.getAccounts().get(0), jake);
        assertEquals(this.acm.getAccounts().get(1), eric);

        assertFalse(this.acm.addAccount(eric));
        assertEquals(this.acm.getAccounts().size(), 2);
    }

    @Test
    void testHasActiveAccount() {
        assertFalse(this.acm.hasActiveAccount());
        this.acm.addAccount(jake);
        this.acm.setActiveAccount("Jake");
        assertTrue(this.acm.hasActiveAccount());
    }

    @Test
    void testSetActiveAccount() {
        this.acm.addAccount(jake);
        assertTrue(this.acm.setActiveAccount("Jake"));
        assertTrue(this.acm.hasActiveAccount());
        assertFalse(this.acm.setActiveAccount("Eric"));
    }

    @Test
    void testGetAccount() {
        assertNull(this.acm.getAccount("Jake"));
        this.acm.addAccount(jake);
        assertEquals(this.acm.getAccount("Jake"), jake);
    }

    @Test
    void testGetActiveAccount() {
        this.acm.addAccount(eric);
        this.acm.addAccount(jake);
        assertFalse(this.acm.getActiveAccount().isPresent());
        this.acm.setActiveAccount("Eric");
        assertTrue(this.acm.getActiveAccount().isPresent());
        assertEquals(this.acm.getActiveAccount().get(), eric);
    }

    @Test
    void testRemoveActiveAccount() {
        this.acm.addAccount(eric);
        this.acm.setActiveAccount("Eric");
        this.acm.removeActiveAccount();
        assertFalse(this.acm.hasActiveAccount());
    }

    @Test
    void testSetActiveAccountName() {
        this.acm.addAccount(eric);
        this.acm.addAccount(jake);
        assertFalse(this.acm.setActiveAccountName("Jack"));

        this.acm.setActiveAccount("Jake");
        assertFalse(this.acm.setActiveAccountName("Eric"));
        assertTrue(this.acm.getActiveAccount().isPresent());
        assertEquals(this.acm.getActiveAccount().get().getName(), "Jake");

        assertTrue(this.acm.setActiveAccountName("Sam"));
        assertEquals(this.acm.getActiveAccount().get().getName(), "Sam");
    }

    @Test
    void testRemoveAccount() {
        this.acm.addAccount(eric);
        assertFalse(this.acm.removeAccount("Jake"));
        assertTrue(this.acm.removeAccount("Eric"));
        assertTrue(this.acm.getAccounts().isEmpty());

        this.acm.addAccount(jake);
        this.acm.addAccount(eric);
        this.acm.setActiveAccount("Jake");
        assertTrue(this.acm.removeAccount("Jake"));
        assertFalse(this.acm.hasActiveAccount());
        assertEquals(this.acm.getAccounts().get(0), eric);
    }

    @Test
    void testToJson() {
        JSONObject jso = acm.toJson(null);
        JSONArray jsa = jso.getJSONArray(AccountManager.JSON_ACCOUNTS_KEY);
        assertNotNull(jsa);
        assertEquals(acm.getAccounts().size(), jsa.length());
    }

    @Test
    void testFromJson() {
        this.acm.addAccount(new Account("john"));
        JSONObject jso = acm.toJson(null);
        AccountManager acm2 = AccountManager.fromJson(jso);
        assertEquals(acm.getAccounts().size(), acm2.getAccounts().size());
        assertEquals(acm.getAccounts(), acm2.getAccounts());
    }

    @Test
    void testSaveStateLoadState() {
        assertDoesNotThrow(this.acm::saveState);
        AccountManager acm2 = new AccountManager();
        assertDoesNotThrow(acm2::loadState);
        assertEquals(this.acm.getAccounts(), acm2.getAccounts());
    }

    @Test
    void testLoadStateThrows() {
        this.acm.setFileSavePath("./data/DNOSADNASDDNSAD");
        try {
            this.acm.loadState();
            fail("Should have thrown IOException.");
        } catch (IOException e) {
            // Ok
        }
    }

    @Test
    void testSaveStateThrows() {
        this.acm.setFileSavePath("./data/dasjkjd12292312//;..dsdopa"
                + "]P{d$#@I#!kn21n\0sd");
        try {
            this.acm.saveState();
            fail("Should have thrown Exception");
        } catch (Exception e) {
            // Ok
        }
    }

}
