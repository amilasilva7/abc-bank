package org.ostech.abcbank.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ostech.abcbank.DTOs.PagedResponse;
import org.ostech.abcbank.DTOs.TransactionResponse;
import org.ostech.abcbank.enums.AccountStatus;
import org.ostech.abcbank.enums.AccountType;
import org.ostech.abcbank.enums.TransactionType;
import org.ostech.abcbank.models.Account;
import org.ostech.abcbank.models.Transaction;
import org.ostech.abcbank.repositories.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account testAccount;
    private Transaction creditTransaction;
    private Transaction debitTransaction;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        // Setup test account
        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setAccountNumber("RHB-000100");
        testAccount.setHolderName("Ahmad bin Abdullah");
        testAccount.setAccountType(AccountType.SAVINGS);
        testAccount.setBalance(new BigDecimal("5000.00"));
        testAccount.setStatus(AccountStatus.ACTIVE);

        // Setup credit transaction
        creditTransaction = new Transaction();
        creditTransaction.setId(1L);
        creditTransaction.setAccount(testAccount);
        creditTransaction.setReferenceNumber("TXN-20260228-0001");
        creditTransaction.setType(TransactionType.CREDIT);
        creditTransaction.setAmount(new BigDecimal("1000.00"));
        creditTransaction.setDescription("Salary deposit");
        creditTransaction.setTransactionDate(LocalDateTime.now().minusDays(5));

        // Setup debit transaction
        debitTransaction = new Transaction();
        debitTransaction.setId(2L);
        debitTransaction.setAccount(testAccount);
        debitTransaction.setReferenceNumber("TXN-20260228-0002");
        debitTransaction.setType(TransactionType.DEBIT);
        debitTransaction.setAmount(new BigDecimal("500.00"));
        debitTransaction.setDescription("ATM withdrawal");
        debitTransaction.setTransactionDate(LocalDateTime.now().minusDays(2));

        // Setup pageable
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void testGetAllTransactions_Success() {
        List<Transaction> transactions = List.of(creditTransaction, debitTransaction);
        Page<Transaction> page = new PageImpl<>(transactions, pageable, 2);

        when(transactionRepository.findAll(pageable)).thenReturn(page);

        PagedResponse<TransactionResponse> response = transactionService.getAllTransactions(pageable);

        assertNotNull(response);
        assertEquals(2, response.content().size());
        assertEquals(0, response.pageNumber());
        assertEquals(10, response.pageSize());
        assertEquals(2, response.totalElements());
        assertTrue(response.last());

        TransactionResponse first = response.content().get(0);
        assertEquals("TXN-20260228-0001", first.referenceNumber());
        assertEquals(TransactionType.CREDIT, first.type());
        assertEquals(new BigDecimal("1000.00"), first.amount());

        verify(transactionRepository).findAll(pageable);
    }

    @Test
    void testGetAllTransactions_EmptyPage() {
        Page<Transaction> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(transactionRepository.findAll(pageable)).thenReturn(emptyPage);

        PagedResponse<TransactionResponse> response = transactionService.getAllTransactions(pageable);

        assertNotNull(response);
        assertTrue(response.content().isEmpty());
        assertEquals(0, response.totalElements());
        assertTrue(response.last());

        verify(transactionRepository).findAll(pageable);
    }

    @Test
    void testGetTransactionStatement_ByAccountType() {
        List<Transaction> savingsTransactions = List.of(creditTransaction, debitTransaction);

        when(transactionRepository.findTransactionsWithAccount(AccountType.SAVINGS))
            .thenReturn(savingsTransactions);

        List<TransactionResponse> responses = transactionService.getTransactionStatement(AccountType.SAVINGS);

        assertNotNull(responses);
        assertEquals(2, responses.size());

        // Verify both transactions are from the SAVINGS account
        for (TransactionResponse response : responses) {
            assertEquals("Ahmad bin Abdullah", response.holderName());
            assertEquals("RHB-000100", response.accountNumber());
        }

        // Verify transaction details
        assertEquals(TransactionType.CREDIT, responses.get(0).type());
        assertEquals(TransactionType.DEBIT, responses.get(1).type());

        verify(transactionRepository).findTransactionsWithAccount(AccountType.SAVINGS);
    }

    @Test
    void testGetTransactionStatement_EmptyResult() {
        when(transactionRepository.findTransactionsWithAccount(AccountType.CURRENT))
            .thenReturn(List.of());

        List<TransactionResponse> responses = transactionService.getTransactionStatement(AccountType.CURRENT);

        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(transactionRepository).findTransactionsWithAccount(AccountType.CURRENT);
    }

    @Test
    void testTransactionResponseMapping() {
        List<Transaction> transactions = List.of(creditTransaction);
        Page<Transaction> page = new PageImpl<>(transactions, pageable, 1);

        when(transactionRepository.findAll(pageable)).thenReturn(page);

        PagedResponse<TransactionResponse> response = transactionService.getAllTransactions(pageable);
        TransactionResponse txn = response.content().get(0);

        assertEquals(1L, txn.id());
        assertEquals("TXN-20260228-0001", txn.referenceNumber());
        assertEquals("RHB-000100", txn.accountNumber());
        assertEquals("Ahmad bin Abdullah", txn.holderName());
        assertEquals(TransactionType.CREDIT, txn.type());
        assertEquals(new BigDecimal("1000.00"), txn.amount());
        assertEquals("Salary deposit", txn.description());
        assertNotNull(txn.transactionDate());
    }
}
