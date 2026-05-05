package com.betting.stake;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StakeManager {
    
    private Map<Integer, Map<Integer, Integer>> stakes = new ConcurrentHashMap<>();
    
    public void addStake(int betOfferId, int customerId, int stake) {
        if (!stakes.containsKey(betOfferId)) {
            stakes.put(betOfferId, new ConcurrentHashMap<>());
        }
        
        Map<Integer, Integer> offerStakes = stakes.get(betOfferId);
        if (!offerStakes.containsKey(customerId) || offerStakes.get(customerId) < stake) {
            offerStakes.put(customerId, stake);
        }
    }
    
    public String getTopStakes(int betOfferId) {
        Map<Integer, Integer> offerStakes = stakes.get(betOfferId);
        
        if (offerStakes == null || offerStakes.size() == 0) {
            return "";
        }
        
        List<Map.Entry<Integer, Integer>> list = new ArrayList<>(offerStakes.entrySet());
        list.sort((a, b) -> b.getValue() - a.getValue());
        
        StringBuilder result = new StringBuilder();
        int count = 0;
        for (Map.Entry<Integer, Integer> entry : list) {
            if (count >= 20) break;
            
            if (count > 0) {
                result.append(",");
            }
            result.append(entry.getKey()).append("=").append(entry.getValue());
            count++;
        }
        
        return result.toString();
    }
}
