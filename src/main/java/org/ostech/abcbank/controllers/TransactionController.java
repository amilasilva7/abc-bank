package org.ostech.abcbank.controllers;

import org.ostech.abcbank.DTOs.PagedResponse;
import org.ostech.abcbank.DTOs.TransactionResponse;
import org.ostech.abcbank.constants.PathConstants;
import org.ostech.abcbank.enums.AccountType;
import org.ostech.abcbank.enums.TransactionType;
import org.ostech.abcbank.services.ExchangeRateService;
import org.ostech.abcbank.services.TransactionService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(PathConstants.TRANSACTIONS)
public class TransactionController {

    private final TransactionService transactionService;
    private final ExchangeRateService exchangeRateService;

    public TransactionController(TransactionService transactionService, ExchangeRateService exchangeRateService) {
        this.transactionService = transactionService;
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping
    public ResponseEntity<PagedResponse<TransactionResponse>> getAllTransactions(
        @PageableDefault(size = 10, sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(transactionService.getAllTransactions(pageable));
    }

    @GetMapping(PathConstants.TRANSACTIONS_SEARCH)
    public ResponseEntity<PagedResponse<TransactionResponse>> searchTransactions(
        @RequestParam(required = false) String accountNumber,
        @RequestParam(required = false) TransactionType type,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        @PageableDefault(size = 10, sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(transactionService.searchTransactions(accountNumber, type, from, to, pageable));
    }

    @GetMapping(PathConstants.TRANSACTIONS_STATEMENT)
    public ResponseEntity<List<TransactionResponse>> getStatement(
        @RequestParam(required = false) AccountType accountType) {
        return ResponseEntity.ok(transactionService.getTransactionStatement(accountType));
    }

    @GetMapping(PathConstants.TRANSACTIONS_EXCHANGE_RATE)
    public ResponseEntity<Map<String, Object>> getExchangeRate(
        @RequestParam(defaultValue = "MYR") String base) {
        return ResponseEntity.ok(exchangeRateService.getExchangeRates(base));
    }
}
