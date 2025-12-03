package com.mitchell.tinyledger.http.handlers;

import com.mitchell.tinyledger.http.JsonUtil;
import com.mitchell.tinyledger.model.Transaction;
import com.mitchell.tinyledger.service.IAccountService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class GetTransactionsHandler implements HttpHandler {
    private final IAccountService service;
    public GetTransactionsHandler(IAccountService s){ this.service = s; }

    @Override public void handle(HttpExchange ex) throws IOException {
        if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
            JsonUtil.sendError(ex, 405, "Use GET");
            return;
        }

        String query = ex.getRequestURI().getQuery();
        if (query == null || !query.startsWith("id=")) {
            JsonUtil.sendError(ex, 400, "Provide ?id=UUID");
            return;
        }

        UUID id = UUID.fromString(query.substring(3));
        List<Transaction> txs = service.history(id);
        JsonUtil.sendJson(ex, 200, Map.of(
                "accountId", id.toString(),
                "transactions", txs.stream().map(t -> Map.of(
                        "id", t.getId().toString(),
                        "type", t.getType().name(),
                        "amount", t.getAmount(),
                        "currency", t.getCurrency().name(),
                        "occurredAt", t.getOccurredAt().toString()
                )).collect(Collectors.toList())
        ));
    }
}
