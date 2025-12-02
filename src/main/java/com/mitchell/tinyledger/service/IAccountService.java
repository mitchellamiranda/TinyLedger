package com.mitchell.tinyledger.service;

import com.mitchell.tinyledger.model.Account;
import com.mitchell.tinyledger.model.Currency;
import com.mitchell.tinyledger.model.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface IAccountService {
    Account createAccount(String name, Currency currency, BigDecimal initialBalance);

    Optional<Account> getAccount(UUID id);

    BigDecimal getBalance(UUID id);

    List<Transaction> history(UUID accountId);

    Set<UUID> listAccountIds();
}
