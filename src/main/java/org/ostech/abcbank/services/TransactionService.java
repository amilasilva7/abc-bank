package org.ostech.abcbank.services;

import jakarta.persistence.criteria.Predicate;
import org.ostech.abcbank.DTOs.PagedResponse;
import org.ostech.abcbank.DTOs.TransactionResponse;
import org.ostech.abcbank.enums.AccountType;
import org.ostech.abcbank.enums.TransactionType;
import org.ostech.abcbank.models.Transaction;
import org.ostech.abcbank.repositories.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    public TransactionService(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    @Transactional(readOnly = true)
    public PagedResponse<TransactionResponse> getAllTransactions(Pageable pageable) {
        Page<TransactionResponse> page = transactionRepository.findAll(pageable)
            .map(TransactionResponse::from);
        return PagedResponse.from(page);
    }

    @Transactional(readOnly = true)
    public PagedResponse<TransactionResponse> searchTransactions(
        String accountNumber, TransactionType type,
        LocalDate from, LocalDate to, Pageable pageable) {

        Specification<Transaction> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (accountNumber != null && !accountNumber.isBlank()) {
                predicates.add(cb.equal(root.get("account").get("accountNumber"), accountNumber));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDate"),
                    from.atStartOfDay()));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("transactionDate"),
                    to.atTime(LocalTime.MAX)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<TransactionResponse> page = transactionRepository.findAll(spec, pageable)
            .map(TransactionResponse::from);
        return PagedResponse.from(page);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionStatement(AccountType accountType) {
        return transactionRepository.findTransactionsWithAccount(accountType)
            .stream()
            .map(TransactionResponse::from)
            .collect(Collectors.toList());
    }
}
