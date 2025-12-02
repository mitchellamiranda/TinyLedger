package com.mitchell.tinyledger.service;

import com.mitchell.tinyledger.model.MovementType;
import java.util.Optional;

public interface ITransactionFactoryService {
    Optional<ITransactionService> getService(MovementType type);
}
