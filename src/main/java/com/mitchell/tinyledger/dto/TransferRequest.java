package com.mitchell.tinyledger.dto;

import com.mitchell.tinyledger.model.Currency;
import java.math.BigDecimal;
import java.util.UUID;

public class TransferRequest {
    public UUID sourceAccountId;
    public UUID destinationAccountId;
    public BigDecimal amount;
    public Currency currency;
}

