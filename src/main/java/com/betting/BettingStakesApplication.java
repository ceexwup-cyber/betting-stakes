package com.betting;

import com.betting.handler.HighStakesHandler;
import com.betting.handler.SessionHandler;
import com.betting.handler.StakeHandler;
import com.betting.session.SessionManager;
import com.betting.stake.StakeManager;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class BettingStakesApplication {
    
    private static int PORT = 8001;
    
    public static void main(String[] args) {
        try {
            SessionManager sessionMgr = new SessionManager();
            StakeManager stakeMgr = new StakeManager();
            
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            
            server.createContext("/", exchange -> {
                String path = exchange.getRequestURI().getPath();
                
                if (path.matches("^/\\d+/session$")) {
                    new SessionHandler(sessionMgr).handle(exchange);
                } else if (path.matches("^/\\d+/stake$")) {
                    new StakeHandler(sessionMgr, stakeMgr).handle(exchange);
                } else if (path.matches("^/\\d+/highstakes$")) {
                    new HighStakesHandler(stakeMgr).handle(exchange);
                } else {
                    String resp = "Not found";
                    exchange.sendResponseHeaders(404, resp.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(resp.getBytes());
                    os.close();
                }
            });
            
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
            
            Thread cleanupThread = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(5 * 60 * 1000);
                        sessionMgr.cleanup();
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            });
            cleanupThread.setDaemon(true);
            cleanupThread.start();
            
            System.out.println("Server started on port " + PORT);
            
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
