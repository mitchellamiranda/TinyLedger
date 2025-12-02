
package com.mitchell.tinyledger.http.handlers;

import com.mitchell.tinyledger.dto.TransferRequest;
import com.mitchell.tinyledger.http.JsonUtil;
import com.mitchell.tinyledger.model.Transaction;
import com.mitchell.tinyledger.service.ITransferService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.Map;

public class TransferHandler implements HttpHandler {
    private final ITransferService service;

    public TransferHandler(ITransferService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            JsonUtil.sendError(ex, 405, "Use POST");
            return;
        }
        TransferRequest req;
        try {
            req = JsonUtil.readBody(ex, TransferRequest.class);
        } catch (Exception e) {
            JsonUtil.sendError(ex, 400, "Invalid JSON: " + e.getMessage());
            return;
        }
        try {
            Transaction tx = service.transfer(req.sourceAccountId, req.destinationAccountId, req.amount, req.currency);
            JsonUtil.sendJson(ex, 201, Map.of(
                    "transactionId", tx.getId().toString(),
                    "sourceAccountId", req.sourceAccountId.toString(),
                    "destinationAccountId", req.destinationAccountId.toString(),
                    "amount", req.amount,
                    "currency", req.currency.name()
            ));
        } catch (IllegalArgumentException | IllegalStateException ex1) {
            JsonUtil.sendError(ex, 400, ex1.getMessage());
        }
    }
}
