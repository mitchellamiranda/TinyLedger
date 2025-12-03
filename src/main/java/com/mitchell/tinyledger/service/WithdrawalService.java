package com.mitchell.tinyledger.service;

import com.mitchell.tinyledger.repo.ILedgerRepository;

import java.math.BigDecimal;

public class WithdrawalService extends GenericTransactionService {

    public WithdrawalService(ILedgerRepository repo) {
        super(repo);
    }

    @Override
    protected BigDecimal getNewBalance(BigDecimal newBalance, BigDecimal amount) {
        return newBalance.subtract(amount);
    }
}
