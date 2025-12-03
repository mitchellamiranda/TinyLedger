package com.mitchell.tinyledger.service;

import com.mitchell.tinyledger.model.Account;
import com.mitchell.tinyledger.model.Currency;
import com.mitchell.tinyledger.model.MovementType;
import com.mitchell.tinyledger.model.Transaction;
import com.mitchell.tinyledger.repo.ILedgerRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class TransferService implements ITransferService{
    private final ILedgerRepository repo;
    private final Map<UUID, ReentrantLock> locks = new ConcurrentHashMap<>();

    public TransferService(ILedgerRepository repo) {
        this.repo = repo;
    }

    @Override
    public Transaction transfer(UUID sourceAccountId, UUID destinationAccountId, BigDecimal amount, Currency currency) {
        if (sourceAccountId.equals(destinationAccountId)) {
            throw new IllegalArgumentException("Source and destination must differ");
        }

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be > 0");
        }

        List<UUID> ids = Arrays.asList(sourceAccountId, destinationAccountId);
        ids.sort(Comparator.naturalOrder()); // Prevent deadlocks
        ReentrantLock lock1 = locks.computeIfAbsent(ids.get(0), k -> new ReentrantLock());
        ReentrantLock lock2 = locks.computeIfAbsent(ids.get(1), k -> new ReentrantLock());

        lock1.lock();
        lock2.lock();
        try {
            Account src = repo.findAccount(sourceAccountId).orElseThrow(() -> new NoSuchElementException("Source not found"));
            Account dst = repo.findAccount(destinationAccountId).orElseThrow(() -> new NoSuchElementException("Destination not found"));

            if (currency != src.getCurrency() || currency != dst.getCurrency()) {
                throw new IllegalArgumentException("Currency mismatch");
            }

            if (src.getBalance().compareTo(amount) < 0) {
                throw new IllegalStateException("Insufficient funds");
            }

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
