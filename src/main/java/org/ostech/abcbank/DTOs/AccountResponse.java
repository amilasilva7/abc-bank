package org.ostech.abcbank.DTOs;

import org.ostech.abcbank.enums.AccountStatus;
import org.ostech.abcbank.enums.AccountType;
import org.ostech.abcbank.models.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
    Long id,
    String accountNumber,
    String holderName,
    AccountType accountType,
    BigDecimal balance,
    AccountStatus status,
    LocalDateTime createdAt) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
            account.getId(),
            account.getAccountNumber(),
            account.getHolderName(),
            account.getAccountType(),
            account.getBalance(),
            account.getStatus(),
            account.getCreatedAt()
        );
    }
}
