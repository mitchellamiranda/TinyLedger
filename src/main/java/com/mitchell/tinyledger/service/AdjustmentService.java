package com.mitchell.tinyledger.service;

import com.mitchell.tinyledger.model.Account;
import com.mitchell.tinyledger.model.Currency;
import com.mitchell.tinyledger.model.MovementType;
import com.mitchell.tinyledger.model.Transaction;
import com.mitchell.tinyledger.repo.ILedgerRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class AdjustmentService extends GenericTransactionService {
    private final Map<UUID, ReentrantLock> locks = new ConcurrentHashMap<>();

    public AdjustmentService(ILedgerRepository repo) {
        super(repo);
    }

    @Override
    protected void validateAmount(BigDecimal amount) {
        if (amount==null || amount.signum() == 0) throw new IllegalArgumentException("Amount must be != 0");
    }

    @Override
    protected BigDecimal getNewBalance(BigDecimal newBalance, BigDecimal amount) {
        return newBalance.add(amount);
    }
}
