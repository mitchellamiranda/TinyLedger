package com.mitchell.tinyledger.service;

import com.mitchell.tinyledger.model.Account;
import com.mitchell.tinyledger.model.Currency;
import com.mitchell.tinyledger.model.MovementType;
import com.mitchell.tinyledger.model.Transaction;

import java.math.BigDecimal;
import java.util.*;

public interface ILedgerService {
    Account createAccount(String name, Currency currency, BigDecimal initialBalance);

    Optional<Account> getAccount(UUID id);

    BigDecimal getBalance(UUID id);

    Transaction record(UUID accountId, MovementType type, BigDecimal amount, Currency currency);

    List<Transaction> history(UUID accountId);

    Set<UUID> listAccountIds();

    Transaction transfer(UUID sourceAccountId, UUID destinationAccountId, BigDecimal amount, Currency currency);
}
