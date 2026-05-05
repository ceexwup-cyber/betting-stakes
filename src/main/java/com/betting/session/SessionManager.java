package com.betting.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SessionManager {
    
    private Map<Integer, SessionData> customerSessions = new HashMap<>();
    private Map<String, SessionData> keySessions = new HashMap<>();
    private Random rand = new Random();
    
    private static final long TEN_MINUTES = 10 * 60 * 1000;
    
    public String getSession(int customerId) {
        SessionData session = customerSessions.get(customerId);
        
        if (session != null && !session.isExpired()) {
            return session.key;
        }
        
        String newKey = createKey();
        SessionData newSession = new SessionData(newKey, customerId, System.currentTimeMillis() + TEN_MINUTES);
        
        if (session != null) {
            keySessions.remove(session.key);
        }
        
        customerSessions.put(customerId, newSession);
        keySessions.put(newKey, newSession);
        
        return newKey;
    }
    
    public boolean isValid(String key) {
        SessionData s = keySessions.get(key);
        return s != null && !s.isExpired();
    }
    
    public Integer getCustomerId(String key) {
        SessionData s = keySessions.get(key);
        if (s != null && !s.isExpired()) {
            return s.customerId;
        }
        return null;
    }
    
    public void cleanup() {
        keySessions.entrySet().removeIf(e -> e.getValue().isExpired());
        customerSessions.entrySet().removeIf(e -> e.getValue().isExpired());
    }
    
    private String createKey() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            sb.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
    class SessionData {
        String key;
        int customerId;
        long expiresAt;
        
        SessionData(String key, int customerId, long expiresAt) {
            this.key = key;
            this.customerId = customerId;
            this.expiresAt = expiresAt;
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }
    }
}
