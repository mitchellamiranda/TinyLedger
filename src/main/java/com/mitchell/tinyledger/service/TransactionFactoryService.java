package com.mitchell.tinyledger.service;

import com.mitchell.tinyledger.model.MovementType;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class TransactionFactoryService implements ITransactionFactoryService {
    private final Map<MovementType, ITransactionService> serviceMap;

    public TransactionFactoryService(
            DepositServer depositService,
            WithdrawalService withdrawalService,
            PaymentService paymentService,
            FeeService feeService,
            AdjustmentService adjustmentService
    ) {
        serviceMap = new EnumMap<>(MovementType.class);
        serviceMap.put(MovementType.DEPOSIT, depositService);
        serviceMap.put(MovementType.WITHDRAWAL, withdrawalService);
        serviceMap.put(MovementType.PAYMENT, paymentService);
        serviceMap.put(MovementType.FEE, feeService);
        serviceMap.put(MovementType.ADJUSTMENT, adjustmentService);
    }

    @Override
    public Optional<ITransactionService> getService(MovementType type) {
        return Optional.ofNullable(serviceMap.get(type));
    }
}
