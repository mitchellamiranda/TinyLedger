package com.mitchell.tinyledger.dto;

import com.mitchell.tinyledger.model.Currency;
import com.mitchell.tinyledger.model.MovementType;
import java.math.BigDecimal;

public class MoneyMovementRequest {
    public MovementType type;
    public BigDecimal amount;
    public Currency currency;
}
