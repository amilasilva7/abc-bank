package org.ostech.abcbank.DTOs;

import org.ostech.abcbank.enums.AccountStatus;

public record UpdateAccountStatusRequest(AccountStatus status) {
}
