package org.ostech.abcbank.DTOs;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.ostech.abcbank.enums.AccountType;

import java.math.BigDecimal;

public record AccountRequest(
    @NotBlank(message = "Holder name is required")
    String holderName,

    @NotNull(message = "Account type is required")
    AccountType accountType,

    @NotNull(message = "Initial balance is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Initial balance must be zero or positive")
    BigDecimal initialBalance
) {
}
