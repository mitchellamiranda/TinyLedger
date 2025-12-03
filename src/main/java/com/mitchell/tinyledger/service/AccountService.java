package com.mitchell.tinyledger.service;

import com.mitchell.tinyledger.model.Account;
import com.mitchell.tinyledger.model.Currency;
import com.mitchell.tinyledger.model.Transaction;
import com.mitchell.tinyledger.repo.ILedgerRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class AccountService implements IAccountService{
    private final ILedgerRepository repo;
    private final Map<UUID, ReentrantLock> locks = new ConcurrentHashMap<>();

    public AccountService(ILedgerRepository repo) {
        this.repo = repo;
    }

    @Override
    public Account createAccount(String name, Currency currency, BigDecimal initialBalance) {
        UUID id = UUID.randomUUID();
        Account acc = new Account(
                id,
                name,
                currency == null ? Currency.USD : currency,
                initialBalance == null ? BigDecimal.ZERO:initialBalance
        );
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
}
