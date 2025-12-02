package com.mitchell.tinyledger.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class Transaction {
    private final UUID id;
    private final UUID accountId;
    private final MovementType type;
    private final BigDecimal amount;
    private final Currency currency;
    private final Instant occurredAt;

    public Transaction(UUID id, UUID accountId, MovementType type, BigDecimal amount,
                       Currency currency, Instant occurredAt) {
        this.id = id;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.occurredAt = occurredAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public MovementType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
