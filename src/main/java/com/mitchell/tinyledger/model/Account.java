package com.mitchell.tinyledger.model;

import java.math.BigDecimal;
import java.util.UUID;

public final class Account {
    private final UUID id;
    private final String name;
    private final Currency currency;
    private final BigDecimal balance;

    public Account(UUID id, String name, Currency currency, BigDecimal balance) {
        this.id = id;
        this.name = name;
        this.currency = currency;
        this.balance = balance;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Account withBalance(BigDecimal newBalance) {
        return new Account(id, name, currency, newBalance);
    }
}
