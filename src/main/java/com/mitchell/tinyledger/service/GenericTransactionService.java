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

public class GenericTransactionService implements ITransactionService {
    private final ILedgerRepository repo;
    private final Map<UUID, ReentrantLock> locks = new ConcurrentHashMap<>();

    public GenericTransactionService(ILedgerRepository repo) {
        this.repo = repo;
    }

    public Transaction record(UUID accountId, MovementType type, BigDecimal amount, Currency currency) {
        validateAmount(amount);
        Account acc = repo.findAccount(accountId).orElseThrow(() -> new NoSuchElementException("Account not found"));
        if (currency != acc.getCurrency()) throw new IllegalArgumentException("Currency mismatch");
        ReentrantLock lock = locks.computeIfAbsent(accountId, k -> new ReentrantLock());
        lock.lock();
        try {
            BigDecimal newBalance = acc.getBalance();
            newBalance = getNewBalance(newBalance, amount);

            Account updated = acc.withBalance(newBalance);
            repo.upsertAccount(updated);
            Transaction tx = new Transaction(UUID.randomUUID(), accountId, type, amount, currency, Instant.now());
            repo.appendTransaction(tx);
            return tx;
        } finally {
            lock.unlock();
        }
    }

    protected void validateAmount(BigDecimal amount) {
        if (amount==null || amount.signum() <= 0) throw new IllegalArgumentException("Amount must be > 0");
    }

     protected BigDecimal getNewBalance(BigDecimal newBalance, BigDecimal amount) {
        return newBalance;
    }
}
