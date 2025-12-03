package com.mitchell.tinyledger.service;

import com.mitchell.tinyledger.model.Account;
import com.mitchell.tinyledger.model.Currency;
import com.mitchell.tinyledger.model.Transaction;
import com.mitchell.tinyledger.repo.ILedgerRepository;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@RunWith(DataProviderRunner.class)
public class TransferServiceTest {
    ITransferService service;
    ILedgerRepository repoMock;

    @Before
    public void setup() {
        this.repoMock = mock(ILedgerRepository.class);
        this.service = new TransferService(repoMock);
    }

    @DataProvider
    public static Object[][] transferTestDataProvider() {
        return new Object[][]{
                {UUID.randomUUID(), UUID.randomUUID(), "100.00", "USD"},
                {UUID.randomUUID(), UUID.randomUUID(), "250.50", "EUR"},
        };
    }

    @Test
    @UseDataProvider("transferTestDataProvider")
    public void transferTest(
            UUID sourceAccountId,
            UUID destinationAccountId,
            String amountStr,
            String currencyStr
    ) {
        BigDecimal srcAmount = new BigDecimal("1000.00");
        Account srcAccountMock = mock(Account.class);
        when(srcAccountMock.getCurrency()).thenReturn(Currency.valueOf(currencyStr));
        when(srcAccountMock.getBalance()).thenReturn(srcAmount);
        when(srcAccountMock.withBalance(srcAmount.subtract(new BigDecimal(amountStr)))).thenReturn(srcAccountMock);

        BigDecimal dstAmount = new BigDecimal("1000.00");
        Account dstAccountMock = mock(Account.class);
        when(dstAccountMock.getCurrency()).thenReturn(Currency.valueOf(currencyStr));
        when(dstAccountMock.getBalance()).thenReturn(dstAmount);
        when(dstAccountMock.withBalance(dstAmount.add(new BigDecimal(amountStr)))).thenReturn(dstAccountMock);


        when(repoMock.findAccount(sourceAccountId)).thenReturn(Optional.of(srcAccountMock));
        when(repoMock.findAccount(destinationAccountId)).thenReturn(Optional.ofNullable(dstAccountMock));

        Transaction tx = service.transfer(
                sourceAccountId,
                destinationAccountId,
                new BigDecimal(amountStr),
                Currency.valueOf(currencyStr)
        );
        assert tx.getAccountId().equals(sourceAccountId);
        assert tx.getAmount().equals(new BigDecimal(amountStr).negate());
        assert tx.getCurrency() == Currency.valueOf(currencyStr);

        verify(srcAccountMock, times(1)).getCurrency();
        verify(srcAccountMock, times(2)).getBalance();
        verify(dstAccountMock, times(1)).getCurrency();
        verify(dstAccountMock, times(1)).getBalance();
        verify(repoMock, times(1)).findAccount(sourceAccountId);
        verify(repoMock, times(1)).findAccount(destinationAccountId);
        verify(repoMock, times(2)).upsertAccount(any(Account.class));
        verify(repoMock, times(2)).appendTransaction(any(Transaction.class));
    }
}
