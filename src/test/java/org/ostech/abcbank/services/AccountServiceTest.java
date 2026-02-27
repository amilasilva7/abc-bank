package org.ostech.abcbank.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ostech.abcbank.DTOs.AccountRequest;
import org.ostech.abcbank.DTOs.AccountResponse;
import org.ostech.abcbank.enums.AccountStatus;
import org.ostech.abcbank.enums.AccountType;
import org.ostech.abcbank.exceptions.ResourceNotFoundException;
import org.ostech.abcbank.models.Account;
import org.ostech.abcbank.repositories.AccountRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;
    private AccountRequest accountRequest;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setAccountNumber("RHB-000100");
        testAccount.setSeqNumber(100L);
        testAccount.setHolderName("Ahmad bin Abdullah");
        testAccount.setAccountType(AccountType.SAVINGS);
        testAccount.setBalance(new BigDecimal("5000.00"));
        testAccount.setStatus(AccountStatus.ACTIVE);
        testAccount.setCreatedAt(LocalDateTime.now());

        accountRequest = new AccountRequest(
            "Ahmad bin Abdullah",
            AccountType.SAVINGS,
            new BigDecimal("5000.00")
        );
    }

    @Test
    void testCreateAccount_Success() {
        when(accountRepository.nextSequenceValue()).thenReturn(100L);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        AccountResponse response = accountService.createAccount(accountRequest);

        assertNotNull(response);
        assertEquals("Ahmad bin Abdullah", response.holderName());
        assertEquals(AccountType.SAVINGS, response.accountType());
        assertEquals(new BigDecimal("5000.00"), response.balance());
        assertEquals(AccountStatus.ACTIVE, response.status());
        verify(accountRepository, times(1)).nextSequenceValue();
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testCreateAccount_GeneratesCorrectAccountNumber() {
        Account newAccount = new Account();
        newAccount.setId(1L);
        newAccount.setSeqNumber(100L);
        newAccount.setAccountNumber("RHB-000100");
        newAccount.setHolderName("Test User");
        newAccount.setAccountType(AccountType.CURRENT);
        newAccount.setBalance(new BigDecimal("1000.00"));

        when(accountRepository.nextSequenceValue()).thenReturn(100L);
        when(accountRepository.save(any(Account.class))).thenReturn(newAccount);

        AccountResponse response = accountService.createAccount(accountRequest);

        assertEquals("RHB-000100", response.accountNumber());
    }

    @Test
    void testGetAccountById_Success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        AccountResponse response = accountService.getAccountById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Ahmad bin Abdullah", response.holderName());
        assertEquals("RHB-000100", response.accountNumber());
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAccountById_NotFound() {
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            accountService.getAccountById(999L);
        });

        verify(accountRepository, times(1)).findById(999L);
    }

    @Test
    void testGetAllAccounts_Success() {
        Account account2 = new Account();
        account2.setId(2L);
        account2.setAccountNumber("RHB-000101");
        account2.setHolderName("Fatimah binti Hassan");
        account2.setAccountType(AccountType.CURRENT);
        account2.setBalance(new BigDecimal("10000.00"));
        account2.setStatus(AccountStatus.ACTIVE);

        when(accountRepository.findAll()).thenReturn(List.of(testAccount, account2));

        List<AccountResponse> responses = accountService.getAllAccounts();

        assertEquals(2, responses.size());
        assertEquals("Ahmad bin Abdullah", responses.get(0).holderName());
        assertEquals("Fatimah binti Hassan", responses.get(1).holderName());
        verify(accountRepository, times(1)).findAll();
    }

    @Test
    void testGetAllAccounts_EmptyList() {
        when(accountRepository.findAll()).thenReturn(List.of());

        List<AccountResponse> responses = accountService.getAllAccounts();

        assertTrue(responses.isEmpty());
        verify(accountRepository, times(1)).findAll();
    }

    @Test
    void testUpdateAccount_Success() {
        AccountRequest updateRequest = new AccountRequest(
            "Ahmad Abdullah (Updated)",
            AccountType.CURRENT,
            new BigDecimal("7500.00")
        );

        Account updatedAccount = new Account();
        updatedAccount.setId(1L);
        updatedAccount.setAccountNumber("RHB-000100");
        updatedAccount.setHolderName("Ahmad Abdullah (Updated)");
        updatedAccount.setAccountType(AccountType.CURRENT);
        updatedAccount.setBalance(new BigDecimal("7500.00"));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);

        AccountResponse response = accountService.updateAccount(1L, updateRequest);

        assertEquals("Ahmad Abdullah (Updated)", response.holderName());
        assertEquals(AccountType.CURRENT, response.accountType());
        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testUpdateAccount_NotFound() {
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            accountService.updateAccount(999L, accountRequest);
        });

        verify(accountRepository, times(1)).findById(999L);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void testUpdateStatus_Success() {
        Account suspendedAccount = new Account();
        suspendedAccount.setId(1L);
        suspendedAccount.setAccountNumber("RHB-000100");
        suspendedAccount.setStatus(AccountStatus.SUSPENDED);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(suspendedAccount);

        AccountResponse response = accountService.updateStatus(1L, AccountStatus.SUSPENDED);

        assertEquals(AccountStatus.SUSPENDED, response.status());
        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testDeleteAccount_Success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        doNothing().when(accountRepository).delete(testAccount);

        assertDoesNotThrow(() -> accountService.deleteAccount(1L));

        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).delete(testAccount);
    }

    @Test
    void testDeleteAccount_NotFound() {
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            accountService.deleteAccount(999L);
        });

        verify(accountRepository, times(1)).findById(999L);
        verify(accountRepository, never()).delete(any(Account.class));
    }

    @Test
    void testFindAccountById_ExceptionMessage() {
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            accountService.findAccountById(999L);
        });

        assertTrue(exception.getMessage().contains("Account not found with id: 999"));
    }
}
