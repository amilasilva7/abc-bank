package org.ostech.abcbank.services;

import org.ostech.abcbank.DTOs.AccountRequest;
import org.ostech.abcbank.DTOs.AccountResponse;
import org.ostech.abcbank.enums.AccountStatus;
import org.ostech.abcbank.exceptions.ResourceNotFoundException;
import org.ostech.abcbank.models.Account;
import org.ostech.abcbank.repositories.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll()
            .stream()
            .map(AccountResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Long id) {
        Account account = findAccountById(id);
        return AccountResponse.from(account);
    }

    public AccountResponse createAccount(AccountRequest request) {
        String accountNumber = generateAccountNumber();
        Account account = new Account(
            accountNumber,
            request.holderName(),
            request.accountType(),
            request.initialBalance()
        );
        Account saved = accountRepository.save(account);
        log.info("Created account: {}", saved.getAccountNumber());
        return AccountResponse.from(saved);
    }

    public AccountResponse updateAccount(Long id, AccountRequest request) {
        Account account = findAccountById(id);
        account.setHolderName(request.holderName());
        account.setAccountType(request.accountType());
        return AccountResponse.from(accountRepository.save(account));
    }

    public AccountResponse updateStatus(Long id, AccountStatus status) {
        Account account = findAccountById(id);
        account.setStatus(status);
        log.info("Account {} status changed to {}", account.getAccountNumber(), status);
        return AccountResponse.from(accountRepository.save(account));
    }

    public void deleteAccount(Long id) {
        Account account = findAccountById(id);
        accountRepository.delete(account);
        log.info("Deleted account: {}", account.getAccountNumber());
    }

    public Account findAccountById(Long id) {
        return accountRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
    }

    private String generateAccountNumber() {
        long count = accountRepository.count() + 1;
        String candidate = String.format("RHB-%06d", count);
        while (accountRepository.existsByAccountNumber(candidate)) {
            count++;
            candidate = String.format("RHB-%06d", count);
        }
        return candidate;
    }
}
