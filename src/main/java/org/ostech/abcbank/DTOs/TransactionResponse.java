package org.ostech.abcbank.DTOs;

import org.ostech.abcbank.enums.TransactionType;
import org.ostech.abcbank.models.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
    Long id,
    String referenceNumber,
    String accountNumber,
    String holderName,
    TransactionType type,
    BigDecimal amount,
    String description,
    LocalDateTime transactionDate
) {
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
            transaction.getId(),
            transaction.getReferenceNumber(),
            transaction.getAccount().getAccountNumber(),
            transaction.getAccount().getHolderName(),
            transaction.getType(),
            transaction.getAmount(),
            transaction.getDescription(),
            transaction.getTransactionDate()
        );
    }
}
