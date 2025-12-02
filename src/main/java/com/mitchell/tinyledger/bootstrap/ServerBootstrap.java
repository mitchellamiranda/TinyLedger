package com.mitchell.tinyledger.bootstrap;

import com.mitchell.tinyledger.http.handlers.CreateAccountHandler;
import com.mitchell.tinyledger.http.handlers.GetBalanceHandler;
import com.mitchell.tinyledger.http.handlers.GetTransactionsHandler;
import com.mitchell.tinyledger.http.handlers.PostTransactionHandler;
import com.mitchell.tinyledger.http.handlers.TransferHandler;
import org.springframework.context.ApplicationContext;
import com.sun.net.httpserver.HttpServer;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * ServerBootstrap template for TinyLedger.
 * Registers handlers using beans from Spring ApplicationContext.
 */
public class ServerBootstrap implements ApplicationContextAware {
    private int port = 8080;
    private HttpServer server;

    private ApplicationContext ctx;
    public void setApplicationContext(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    public void setPort(int port) { this.port = port; }

    /**
     * Start the HTTP server and register contexts using Spring beans.
     */
    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // Register endpoints using beans from Spring context
        server.createContext("/api/v1/accounts", ctx.getBean("createAccountHandler", CreateAccountHandler.class));
        server.createContext("/api/v1/accounts/balance", ctx.getBean("getBalanceHandler", GetBalanceHandler.class));
        server.createContext("/api/v1/accounts/transactions", ctx.getBean("getTransactionsHandler", GetTransactionsHandler.class));
        server.createContext("/api/v1/transactions", ctx.getBean("postTransactionHandler", PostTransactionHandler.class));
        server.createContext("/api/v1/accounts/transfer", ctx.getBean("transferHandler", TransferHandler.class));

        server.setExecutor(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
        server.start();
        System.out.println("TinyLedger running on http://localhost:" + port);
    }

    /**
     * Stop the HTTP server.
     */
    public void stop() {
        if (server != null) server.stop(0);
    }
}
