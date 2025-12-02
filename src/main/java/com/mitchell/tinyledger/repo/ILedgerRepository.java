package com.mitchell.tinyledger.repo;

import com.mitchell.tinyledger.model.Account;
import com.mitchell.tinyledger.model.Transaction;

import java.util.*;

public interface ILedgerRepository {
    Optional<Account> findAccount(UUID id);

    void upsertAccount(Account account);

    List<Transaction> listTransactions(UUID accountId);

    void appendTransaction(Transaction tx);

    Set<UUID> listAccountIds();
}
