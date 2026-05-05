package com.betting.handler;

import com.betting.session.SessionManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SessionHandler implements HttpHandler {
    
    private Pattern pattern = Pattern.compile("^/(\\d+)/session$");
    private SessionManager sessionMgr;
    
    public SessionHandler(SessionManager sm) {
        this.sessionMgr = sm;
    }
    
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("GET")) {
            sendError(exchange, 405, "Method not allowed");
            return;
        }
        
        String path = exchange.getRequestURI().getPath();
        Matcher m = pattern.matcher(path);
        
        if (!m.matches()) {
            sendError(exchange, 400, "Bad request");
            return;
        }
        
        try {
            int customerId = Integer.parseInt(m.group(1));
            String sessionKey = sessionMgr.getSession(customerId);
            sendResponse(exchange, 200, sessionKey);
        } catch (Exception e) {
            sendError(exchange, 500, "Server error");
        }
    }
    
    private void sendResponse(HttpExchange exchange, int code, String msg) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        exchange.sendResponseHeaders(code, msg.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(msg.getBytes());
        os.close();
    }
    
    private void sendError(HttpExchange exchange, int code, String msg) throws IOException {
        sendResponse(exchange, code, msg);
    }
}
