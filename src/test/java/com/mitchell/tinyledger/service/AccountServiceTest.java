package com.mitchell.tinyledger.service;

import com.mitchell.tinyledger.model.Account;
import com.mitchell.tinyledger.model.Currency;
import com.mitchell.tinyledger.repo.ILedgerRepository;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static com.mitchell.tinyledger.model.Currency.EUR;
import static com.mitchell.tinyledger.model.Currency.USD;
import static java.util.Collections.emptyList;
import static org.mockito.Mockito.*;

@RunWith(DataProviderRunner.class)
public class AccountServiceTest {
    IAccountService service;
    ILedgerRepository repoMock;

    @Before
    public void setUp() {
        this.repoMock = mock(ILedgerRepository.class);
        this.service = new AccountService(repoMock);
    }

    @DataProvider
    public static Object[][] createAccountTestDataProvider() {
        return new Object[][]{
                {"Test Account 1", null, null},
                {"Test Account 2", USD, null},
                {"Test Account 3", EUR, new BigDecimal("100.00")},
        };
    }

    @Test
    @UseDataProvider("createAccountTestDataProvider")
    public void createAccountTest(String name, Currency currency, BigDecimal initialBalance) {
        Account account = service.createAccount(name, currency, initialBalance);

        assert account.getName().equals(name);
        assert account.getCurrency() == (currency == null ? Currency.USD : currency);
        assert account.getBalance().equals(initialBalance == null ? BigDecimal.ZERO : initialBalance);

        verify(repoMock, times(1)).upsertAccount(account);
    }

    @Test
    public void getBalanceTest() {
        UUID accountId = UUID.randomUUID();
        BigDecimal balance = new BigDecimal("250.00");
        Account account = new Account(accountId, "Test Account", USD, balance);

        when(repoMock.findAccount(accountId)).thenReturn(Optional.of(account));

        BigDecimal retrievedBalance = service.getBalance(accountId);
        assert retrievedBalance.equals(balance);

        verify(repoMock, times(1)).findAccount(accountId);
    }

    @Test
    public void getAccountNotFoundTest() {
        UUID accountId = UUID.randomUUID();

        when(repoMock.findAccount(accountId)).thenReturn(Optional.empty());

        try {
            service.getBalance(accountId);
            assert false; // Should not reach here
        } catch (Exception e) {
            assert e instanceof NoSuchElementException;
            assert e.getMessage().equals("Account not found");
        }

        verify(repoMock, times(1)).findAccount(accountId);
    }

    @Test
    public void getAccountTest() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account(accountId, "Test Account", USD, new BigDecimal("500.00"));

        when(repoMock.findAccount(accountId)).thenReturn(Optional.of(account));

        Optional<Account> retrievedAccount = service.getAccount(accountId);
        assert retrievedAccount.isPresent();
        assert retrievedAccount.get().equals(account);

        verify(repoMock, times(1)).findAccount(accountId);
    }

    @Test
    public void historyTest() {
        UUID accountId = UUID.randomUUID();
        when(repoMock.listTransactions(accountId)).thenReturn(emptyList());

        service.history(accountId);

        verify(repoMock, times(1)).listTransactions(accountId);
    }
}
