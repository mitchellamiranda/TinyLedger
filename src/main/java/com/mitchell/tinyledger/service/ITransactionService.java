package com.mitchell.tinyledger.service;

import com.mitchell.tinyledger.model.Currency;
import com.mitchell.tinyledger.model.MovementType;
import com.mitchell.tinyledger.model.Transaction;

import java.math.BigDecimal;
import java.util.UUID;

public interface ITransactionService {

    Transaction record(UUID accountId, MovementType type, BigDecimal amount, Currency currency);
}
