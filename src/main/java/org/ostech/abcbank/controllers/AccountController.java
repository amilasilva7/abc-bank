package org.ostech.abcbank.controllers;

import jakarta.validation.Valid;
import org.ostech.abcbank.DTOs.AccountRequest;
import org.ostech.abcbank.DTOs.AccountResponse;
import org.ostech.abcbank.DTOs.UpdateAccountStatusRequest;
import org.ostech.abcbank.constants.PathConstants;
import org.ostech.abcbank.services.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = PathConstants.ACCOUNTS)
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponse> updateAccount(@PathVariable Long id,
                                                         @Valid @RequestBody AccountRequest request) {
        return ResponseEntity.ok(accountService.updateAccount(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AccountResponse> updateStatus(@PathVariable Long id, @RequestBody UpdateAccountStatusRequest updateAccountStatusRequest) {
        return ResponseEntity.ok(accountService.updateStatus(id, updateAccountStatusRequest.status()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
