package com.mitchell.tinyledger.service;

import com.mitchell.tinyledger.model.MovementType;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.mock;

@RunWith(DataProviderRunner.class)
public class TransactionFactoryServiceTest {
    private ITransactionFactoryService service;

    @Before
    public void setUp() {
        DepositServer depositServiceMock = mock(DepositServer.class);
        WithdrawalService withdrawalServiceMock = mock(WithdrawalService.class);
        PaymentService paymentServiceMock = mock(PaymentService.class);
        FeeService feeServiceMock = mock(FeeService.class);
        AdjustmentService adjustmentServiceMock = mock(AdjustmentService.class);

        this.service = new TransactionFactoryService(
                depositServiceMock,
                withdrawalServiceMock,
                paymentServiceMock,
                feeServiceMock,
                adjustmentServiceMock
        );
    }

    @DataProvider
    public static Object[][] getServiceDataProvider() {
        return new Object[][]{
                {MovementType.DEPOSIT, DepositServer.class},
                {MovementType.WITHDRAWAL, WithdrawalService.class},
                {MovementType.PAYMENT, PaymentService.class},
                {MovementType.FEE, FeeService.class},
                {MovementType.ADJUSTMENT, AdjustmentService.class},
                {MovementType.ADJUSTMENT, AdjustmentService.class},
        };
    }

    @Test
    @UseDataProvider("getServiceDataProvider")
    public void getServiceTest(MovementType type, Class<? extends ITransactionService> expectedClass) {
        ITransactionService transactionService = this.service.getService(type).orElseThrow();
        assert transactionService.getClass().equals(expectedClass);
    }
}
