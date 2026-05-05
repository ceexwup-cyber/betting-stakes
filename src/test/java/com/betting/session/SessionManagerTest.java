package com.betting.session;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {
    
    @Test
    void testGetSession() {
        SessionManager mgr = new SessionManager();
        String key = mgr.getSession(1234);
        
        assertNotNull(key);
        assertEquals(7, key.length());
        assertTrue(key.matches("[A-Z0-9]+"));
    }
    
    @Test
    void testSameCustomerGetsSameSession() {
        SessionManager mgr = new SessionManager();
        String key1 = mgr.getSession(1234);
        String key2 = mgr.getSession(1234);
        
        assertEquals(key1, key2);
    }
    
    @Test
    void testDifferentCustomersGetDifferentSessions() {
        SessionManager mgr = new SessionManager();
        String key1 = mgr.getSession(1234);
        String key2 = mgr.getSession(5678);
        
        assertNotEquals(key1, key2);
    }
    
    @Test
    void testValidSession() {
        SessionManager mgr = new SessionManager();
        String key = mgr.getSession(1234);
        
        assertTrue(mgr.isValid(key));
        assertFalse(mgr.isValid("INVALID"));
    }
    
    @Test
    void testGetCustomerId() {
        SessionManager mgr = new SessionManager();
        String key = mgr.getSession(1234);
        
        assertEquals(1234, mgr.getCustomerId(key));
        assertNull(mgr.getCustomerId("INVALID"));
    }
}
