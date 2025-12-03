package com.mitchell.tinyledger.dataset;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mitchell.tinyledger.model.Currency;
import com.mitchell.tinyledger.model.MovementType;
import com.mitchell.tinyledger.service.IAccountService;
import com.mitchell.tinyledger.service.ITransactionFactoryService;
import com.mitchell.tinyledger.service.ITransactionService;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatasetLoader {
    private final IAccountService accountService;
    private final ITransactionFactoryService transactionFactoryService;
    private String seedResourcePath = "datasets/seed.json";

    public DatasetLoader(IAccountService accountService, ITransactionFactoryService transactionFactoryService) {
        this.accountService = accountService;
        this.transactionFactoryService = transactionFactoryService;
    }

    public void setSeedResourcePath(String path) { this.seedResourcePath = path; }

    public void seed() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(seedResourcePath);
            if (is == null) {
                return;
            }

            JsonNode root = mapper.readTree(is);
            List<java.util.UUID> created = new ArrayList<>();

            createAccounts(root.path("accounts"),  created);
            createTransactions(root.path("transactions"), created);

            System.out.println("Dataset seeded: accounts=" + created.size());
        } catch (Exception e) {
            System.err.println("Dataset seed failed: " + e.getMessage());
        }
    }

    private void createAccounts(JsonNode accounts, List<java.util.UUID> created) {
        for (JsonNode acc : accounts) {
            String name = acc
                    .path("name")
                    .asText("Unnamed");
            Currency currency = Currency.valueOf(acc.path("currency").asText("EUR"));
            BigDecimal init = acc.has("initialBalance")
                    ? new BigDecimal(acc.get("initialBalance").asText("0"))
                    : BigDecimal.ZERO;
            created.add(accountService.createAccount(name, currency, init).getId());
        }
    }

    private void createTransactions(JsonNode transactions, List<java.util.UUID> created) {
        for (JsonNode tx : transactions) {
            int idx = tx.path("accountIndex").asInt(0);
            java.util.UUID accountId = created.get(idx);
            MovementType type = MovementType.valueOf(tx.path("type").asText("DEPOSIT"));
            BigDecimal amount = new BigDecimal(tx.path("amount").asText("0"));
            Currency currency = Currency.valueOf(tx.path("currency").asText("EUR"));
            Optional<ITransactionService> serviceOpt = transactionFactoryService.getService(type);
            if (!serviceOpt.isPresent()) {
                continue;
            }
            ITransactionService service = serviceOpt.get();
            service.record(accountId, type, amount, currency);
        }
    }
}
