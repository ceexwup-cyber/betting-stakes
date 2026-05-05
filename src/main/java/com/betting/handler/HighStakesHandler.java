package com.betting.handler;

import com.betting.stake.StakeManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HighStakesHandler implements HttpHandler {
    
    private Pattern pattern = Pattern.compile("^/(\\d+)/highstakes$");
    private StakeManager stakeMgr;
    
    public HighStakesHandler(StakeManager sm) {
        this.stakeMgr = sm;
    }
    
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("GET")) {
            sendResponse(exchange, 405, "Method not allowed");
            return;
        }
        
        String path = exchange.getRequestURI().getPath();
        Matcher m = pattern.matcher(path);
        
        if (!m.matches()) {
            sendResponse(exchange, 400, "Bad request");
            return;
        }
        
        try {
            int betOfferId = Integer.parseInt(m.group(1));
            String result = stakeMgr.getTopStakes(betOfferId);
            sendResponse(exchange, 200, result);
        } catch (Exception e) {
            sendResponse(exchange, 500, "Server error");
        }
    }
    
    private void sendResponse(HttpExchange exchange, int code, String msg) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        exchange.sendResponseHeaders(code, msg.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(msg.getBytes());
        os.close();
    }
}
