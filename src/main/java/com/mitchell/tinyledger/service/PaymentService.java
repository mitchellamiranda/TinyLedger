package com.mitchell.tinyledger.service;

import com.mitchell.tinyledger.model.Currency;
import com.mitchell.tinyledger.model.MovementType;
import com.mitchell.tinyledger.model.Transaction;
import com.mitchell.tinyledger.repo.ILedgerRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class PaymentService extends GenericTransactionService {
    private final Map<UUID, ReentrantLock> locks = new ConcurrentHashMap<>();

    public PaymentService(ILedgerRepository repo) {
        super(repo);
    }

    @Override
    protected BigDecimal getNewBalance(BigDecimal newBalance, BigDecimal amount) {
        return newBalance.subtract(amount);
    }
}
