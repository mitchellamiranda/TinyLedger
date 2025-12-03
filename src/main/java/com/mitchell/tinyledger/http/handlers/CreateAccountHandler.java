package com.mitchell.tinyledger.http.handlers;

import com.mitchell.tinyledger.http.JsonUtil;
import com.mitchell.tinyledger.model.Account;
import com.mitchell.tinyledger.model.Currency;
import com.mitchell.tinyledger.service.IAccountService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

public class CreateAccountHandler implements HttpHandler {
    private final IAccountService service;
    public CreateAccountHandler(IAccountService service) { this.service = service; }

    @Override public void handle(HttpExchange ex) throws IOException {
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            JsonUtil.sendError(ex, 405, "Use POST");
            return;
        }
        Map body = JsonUtil.readBody(ex, Map.class);
        String name = (String) body.getOrDefault("name", "Unnamed");
        String currencyStr = (String) body.getOrDefault("currency", "EUR");
        Currency currency = Currency.valueOf(currencyStr);
        BigDecimal initial = body.containsKey("initialBalance")
                ? new BigDecimal(String.valueOf(body.get("initialBalance")))
                : BigDecimal.ZERO;

        Account acc = service.createAccount(name, currency, initial);
        JsonUtil.sendJson(ex, 201, Map.of(
                "accountId", acc.getId().toString(),
                "currency", acc.getCurrency().name(),
                "balance", acc.getBalance(),
                "name", acc.getName()
        ));
    }
}
