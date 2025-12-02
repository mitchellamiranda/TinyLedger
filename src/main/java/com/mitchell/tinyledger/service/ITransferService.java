package com.mitchell.tinyledger.service;

import com.mitchell.tinyledger.model.Currency;
import com.mitchell.tinyledger.model.Transaction;

import java.math.BigDecimal;
import java.util.UUID;

public interface ITransferService {

    Transaction transfer(UUID sourceAccountId, UUID destinationAccountId, BigDecimal amount, Currency currency);
}
