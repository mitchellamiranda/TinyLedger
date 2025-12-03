
package com.mitchell.tinyledger.http.handlers;

import com.mitchell.tinyledger.http.JsonUtil;
import com.mitchell.tinyledger.service.IAccountService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class GetBalanceHandler implements HttpHandler {
    private final IAccountService service;

    public GetBalanceHandler(IAccountService s) {
        this.service = s;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
            JsonUtil.sendError(ex, 405, "Use GET");
            return;
        }

        String query = ex.getRequestURI().getQuery();
        if (query==null || !query.startsWith("id=")) {
            JsonUtil.sendError(ex, 400, "Provide ?id=UUID");
            return;
        }

        UUID id = UUID.fromString(query.substring(3));
        BigDecimal balance = service.getBalance(id);
        JsonUtil.sendJson(ex, 200, Map.of("accountId", id.toString(), "balance", balance));
    }
}