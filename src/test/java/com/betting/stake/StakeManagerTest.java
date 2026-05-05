package com.betting.stake;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StakeManagerTest {
    
    @Test
    void testAddStake() {
        StakeManager mgr = new StakeManager();
        mgr.addStake(888, 1234, 4500);
        
        String result = mgr.getTopStakes(888);
        assertEquals("1234=4500", result);
    }
    
    @Test
    void testKeepHighestStake() {
        StakeManager mgr = new StakeManager();
        mgr.addStake(888, 1234, 1000);
        mgr.addStake(888, 1234, 4500);
        mgr.addStake(888, 1234, 2000);
        
        String result = mgr.getTopStakes(888);
        assertEquals("1234=4500", result);
    }
    
    @Test
    void testNoStakes() {
        StakeManager mgr = new StakeManager();
        String result = mgr.getTopStakes(999);
        assertEquals("", result);
    }
    
    @Test
    void testSortedDescending() {
        StakeManager mgr = new StakeManager();
        mgr.addStake(888, 1234, 4500);
        mgr.addStake(888, 5678, 1337);
        mgr.addStake(888, 9999, 5000);
        
        String result = mgr.getTopStakes(888);
        assertEquals("9999=5000,1234=4500,5678=1337", result);
    }
    
    @Test
    void testTop20Limit() {
        StakeManager mgr = new StakeManager();
        
        for (int i = 1; i <= 25; i++) {
            mgr.addStake(888, i, i * 100);
        }
        
        String result = mgr.getTopStakes(888);
        String[] parts = result.split(",");
        
        assertEquals(20, parts.length);
        assertEquals("25=2500", parts[0]);
    }
    
    @Test
    void testMultipleBetOffers() {
        StakeManager mgr = new StakeManager();
        mgr.addStake(888, 1234, 4500);
        mgr.addStake(999, 5678, 1337);
        
        assertEquals("1234=4500", mgr.getTopStakes(888));
        assertEquals("5678=1337", mgr.getTopStakes(999));
    }
}
