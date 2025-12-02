package com.mitchell.tinyledger.service;

import com.mitchell.tinyledger.model.*;
import com.mitchell.tinyledger.model.Currency;
import com.mitchell.tinyledger.repo.ILedgerRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LedgerServiceImpl implements ILedgerService {
    private final ILedgerRepository repo;
    private final Map<UUID, ReentrantLock> locks = new ConcurrentHashMap<>();

    public LedgerServiceImpl(ILedgerRepository repo) {
        this.repo = repo;
    }

    @Override
    public Account createAccount(String name, Currency currency, BigDecimal initialBalance) {
        UUID id = UUID.randomUUID();
        Account acc = new Account(id, name, currency, initialBalance==null ? BigDecimal.ZERO:initialBalance);
        repo.upsertAccount(acc);
        locks.putIfAbsent(id, new ReentrantLock());
        return acc;
    }

    @Override
    public Optional<Account> getAccount(UUID id) {
        return repo.findAccount(id);
    }

    @Override
    public BigDecimal getBalance(UUID id) {
        return repo.findAccount(id).orElseThrow(() -> new NoSuchElementException("Account not found")).getBalance();
    }

    @Override
    public List<Transaction> history(UUID accountId) {
        return repo.listTransactions(accountId);
    }

    @Override
    public Set<UUID> listAccountIds() {
        return repo.listAccountIds();
    }


    public Transaction record(UUID accountId, MovementType type, BigDecimal amount, Currency currency) {
        if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException("Amount must be > 0");
        Account acc = repo.findAccount(accountId).orElseThrow(() -> new NoSuchElementException("Account not found"));
        if (currency != acc.getCurrency()) throw new IllegalArgumentException("Currency mismatch");
        ReentrantLock lock = locks.computeIfAbsent(accountId, k -> new ReentrantLock());
        lock.lock();
        try {
            BigDecimal newBalance = acc.getBalance();
            switch (type) {
                case DEPOSIT:
                    newBalance = newBalance.add(amount);
                    break;
                case WITHDRAWAL:
                case PAYMENT:
                case FEE:
                    if (newBalance.compareTo(amount) < 0) throw new IllegalStateException("Insufficient funds");
                    newBalance = newBalance.subtract(amount);
                    break;
                case ADJUSTMENT:
                    newBalance = newBalance.add(amount); // amount can be negative
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported type for record()");
            }
            Account updated = acc.withBalance(newBalance);
            repo.upsertAccount(updated);
            Transaction tx = new Transaction(UUID.randomUUID(), accountId, type, amount, currency, Instant.now());
            repo.appendTransaction(tx);
            return tx;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Transaction transfer(UUID sourceAccountId, UUID destinationAccountId, BigDecimal amount, Currency currency) {
        if (sourceAccountId.equals(destinationAccountId)) throw new IllegalArgumentException("Source and destination must differ");
        if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException("Amount must be > 0");

        // Lock both accounts to avoid race conditions
        List<UUID> ids = Arrays.asList(sourceAccountId, destinationAccountId);
        ids.sort(Comparator.naturalOrder()); // Prevent deadlocks
        ReentrantLock lock1 = locks.computeIfAbsent(ids.get(0), k -> new ReentrantLock());
        ReentrantLock lock2 = locks.computeIfAbsent(ids.get(1), k -> new ReentrantLock());

        lock1.lock();
        lock2.lock();
        try {
            Account src = repo.findAccount(sourceAccountId).orElseThrow(() -> new NoSuchElementException("Source not found"));
            Account dst = repo.findAccount(destinationAccountId).orElseThrow(() -> new NoSuchElementException("Destination not found"));
            if (currency != src.getCurrency() || currency != dst.getCurrency())
                throw new IllegalArgumentException("Currency mismatch");
            if (src.getBalance().compareTo(amount) < 0) throw new IllegalStateException("Insufficient funds");

            Account srcUpdated = src.withBalance(src.getBalance().subtract(amount));
            Account dstUpdated = dst.withBalance(dst.getBalance().add(amount));
            repo.upsertAccount(srcUpdated);
            repo.upsertAccount(dstUpdated);

            Transaction txSrc = new Transaction(UUID.randomUUID(), sourceAccountId, MovementType.TRANSFER, amount.negate(), currency, Instant.now());
            Transaction txDst = new Transaction(UUID.randomUUID(), destinationAccountId, MovementType.TRANSFER, amount, currency, Instant.now());
            repo.appendTransaction(txSrc);
            repo.appendTransaction(txDst);

            return txSrc; // Or return both transactions as needed
        } finally {
            lock2.unlock();
            lock1.unlock();
        }
    }

}
