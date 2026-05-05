package com.betting.handler;

import com.betting.session.SessionManager;
import com.betting.stake.StakeManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StakeHandler implements HttpHandler {
    
    private Pattern pattern = Pattern.compile("^/(\\d+)/stake$");
    private SessionManager sessionMgr;
    private StakeManager stakeMgr;
    
    public StakeHandler(SessionManager sm, StakeManager stm) {
        this.sessionMgr = sm;
        this.stakeMgr = stm;
    }
    
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("POST")) {
            sendResponse(exchange, 405, "Method not allowed");
            return;
        }
        
        String path = exchange.getRequestURI().getPath();
        Matcher m = pattern.matcher(path);
        
        if (!m.matches()) {
            sendResponse(exchange, 400, "Bad request");
            return;
        }
        
        String sessionKey = getSessionKey(exchange.getRequestURI());
        if (sessionKey == null || !sessionMgr.isValid(sessionKey)) {
            sendResponse(exchange, 401, "Unauthorized");
            return;
        }
        
        Integer custId = sessionMgr.getCustomerId(sessionKey);
        if (custId == null) {
            sendResponse(exchange, 401, "Unauthorized");
            return;
        }
        
        try {
            int betOfferId = Integer.parseInt(m.group(1));
            int stake = readStake(exchange);
            
            stakeMgr.addStake(betOfferId, custId, stake);
            sendResponse(exchange, 200, "");
        } catch (Exception e) {
            sendResponse(exchange, 400, "Invalid stake");
        }
    }
    
    private String getSessionKey(URI uri) {
        String query = uri.getQuery();
        if (query == null) return null;
        
        String[] params = query.split("&");
        for (String p : params) {
            String[] kv = p.split("=");
            if (kv.length == 2 && kv[0].equals("sessionkey")) {
                return kv[1];
            }
        }
        return null;
    }
    
    private int readStake(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        String line = reader.readLine();
        reader.close();
        return Integer.parseInt(line.trim());
    }
    
    private void sendResponse(HttpExchange exchange, int code, String msg) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        exchange.sendResponseHeaders(code, msg.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(msg.getBytes());
        os.close();
    }
}
