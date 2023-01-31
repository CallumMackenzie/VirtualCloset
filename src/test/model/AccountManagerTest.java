package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

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
        assertEquals(this.acm.getAccounts().size(), 0);
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
        assertTrue(this.acm.getActiveAccount().isEmpty());
        this.acm.setActiveAccount("Eric");
        assertTrue(this.acm.getActiveAccount().isPresent());
        assertEquals(this.acm.getActiveAccount().get(), eric);
    }

    @Test
    void testSetActiveAccountName() {
        this.acm.addAccount(eric);
        this.acm.addAccount(jake);
        assertFalse(this.acm.setActiveAccountName("Jack"));

        this.acm.setActiveAccount("Jake");
        assertFalse(this.acm.setActiveAccountName("Eric"));
        assertEquals(this.acm.getActiveAccount().get().getName(), "Jake");

        assertTrue(this.acm.setActiveAccountName("Sam"));
        assertEquals(this.acm.getActiveAccount().get().getName(), "Sam");
    }

}
