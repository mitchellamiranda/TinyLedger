package com.mitchell.tinyledger.http.handlers;

import com.mitchell.tinyledger.dto.MoneyMovementRequest;
import com.mitchell.tinyledger.http.JsonUtil;
import com.mitchell.tinyledger.model.MovementType;
import com.mitchell.tinyledger.model.Transaction;
import com.mitchell.tinyledger.service.ITransactionFactoryService;
import com.mitchell.tinyledger.service.ITransactionService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PostTransactionHandler implements HttpHandler {
    private final ITransactionFactoryService factory;

    public PostTransactionHandler (ITransactionFactoryService factory) {
        this.factory = factory;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            JsonUtil.sendError(ex, 405, "Use POST");
            return;
        }

        String query = ex.getRequestURI().getQuery();
        if (query == null || !query.startsWith("accountId=")) {
            JsonUtil.sendError(ex, 400, "Provide ?accountId=UUID");
            return;
        }
        UUID accountId = UUID.fromString(query.substring("accountId=".length()));
        MoneyMovementRequest req;
        try {
            req = JsonUtil.readBody(ex, MoneyMovementRequest.class);
        } catch (Exception e) {
            JsonUtil.sendError(ex, 400, "Invalid JSON: " + e.getMessage()); return;
        }

        MovementType type = req.type;
        Optional<ITransactionService> serviceOpt = factory.getService(type);
        if (!serviceOpt.isPresent()) {
            JsonUtil.sendError(ex, 400, "Unsupported transaction type: " + type);
            return;
        }

        try {
            ITransactionService service = serviceOpt.get();
            Transaction tx = service.record(accountId, type, req.amount, req.currency);
            JsonUtil.sendJson(ex, 201, Map.of(
                    "transactionId", tx.getId().toString(),
                    "accountId", tx.getAccountId().toString(),
                    "type", tx.getType().name(),
                    "amount", tx.getAmount(),
                    "currency", tx.getCurrency().name(),
                    "occurredAt", tx.getOccurredAt().toString()
            ));
        } catch (IllegalArgumentException | IllegalStateException ex1) {
            JsonUtil.sendError(ex, 400, ex1.getMessage());
        }
    }
}
