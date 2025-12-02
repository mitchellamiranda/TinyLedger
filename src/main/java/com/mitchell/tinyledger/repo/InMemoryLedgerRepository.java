package com.mitchell.tinyledger.repo;

import com.mitchell.tinyledger.model.Account;
import com.mitchell.tinyledger.model.Transaction;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryLedgerRepository implements ILedgerRepository {
    private final Map<UUID, Account> accounts = new ConcurrentHashMap<>();
    private final Map<UUID, List<Transaction>> txs = new ConcurrentHashMap<>();

    @Override
    public Optional<Account> findAccount(UUID id) {
        return Optional.ofNullable(accounts.get(id));
    }

    @Override
    public void upsertAccount(Account account) {
        accounts.put(account.getId(), account);
        txs.computeIfAbsent(account.getId(), k -> new CopyOnWriteArrayList<>());
    }

    @Override
    public List<Transaction> listTransactions(UUID accountId) {
        return new ArrayList<>(txs.getOrDefault(accountId, List.of()));
    }

    @Override
    public void appendTransaction(Transaction tx) {
        txs.computeIfAbsent(tx.getAccountId(), k -> new CopyOnWriteArrayList<>()).add(tx);
    }

    @Override
    public Set<UUID> listAccountIds() {
        return accounts.keySet();
    }
}
