package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.dto.TransferDTO;
import com.db.awmd.challenge.exception.AccountWouldBeInDeficitException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.NotificationService;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

    @Mock
    private NotificationService notificationService;

    @Autowired
    @InjectMocks
    private AccountsService accountsService;

    @After
    public void tearDown() {
        accountsService.getAccountsRepository().clearAccounts();
    }

    @Test
    public void addAccount() throws Exception {
        Account account = new Account("Id-123");
        account.setBalance(new BigDecimal(1000));
        this.accountsService.createAccount(account);

        assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
    }

    @Test
    public void addAccount_failsOnDuplicateId() throws Exception {
        String uniqueId = "Id-" + System.currentTimeMillis();
        Account account = new Account(uniqueId);
        this.accountsService.createAccount(account);

        try {
            this.accountsService.createAccount(account);
            fail("Should have failed when adding duplicate account");
        } catch (DuplicateAccountIdException ex) {
            assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
        }
    }

    @Test
    public void doTransferHappyPath() throws Exception {
        Account origin = new Account("Id-123");
        Account beneficiary = new Account("Id-456");
        origin.setBalance(new BigDecimal(1000));
        beneficiary.setBalance(new BigDecimal(0));
        this.accountsService.createAccount(origin);
        this.accountsService.createAccount(beneficiary);

        assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(origin);
        assertThat(this.accountsService.getAccount("Id-456")).isEqualTo(beneficiary);

        TransferDTO transferDTO = new TransferDTO("Id-123", "Id-456", "500");
        this.accountsService.doTransfer(transferDTO);
        String debitNotification = String.format(NotificationService.DEBIT_NOTIFICATION_MESSAGE, "500", "Id-456");
        String creditNotification = String.format(NotificationService.CREDIT_NOTIFICATION_MESSAGE, "500", "Id-123");
        verify(this.notificationService).notifyAboutTransfer(this.accountsService.getAccountsRepository().getAccount("Id-123"), debitNotification);
        verify(this.notificationService).notifyAboutTransfer(this.accountsService.getAccountsRepository().getAccount("Id-456"), creditNotification);

        assertThat(this.accountsService.getAccount("Id-456").getBalance()).isEqualTo(new BigDecimal(500));
        assertThat(this.accountsService.getAccount("Id-123").getBalance()).isEqualTo(new BigDecimal(500));
    }

    @Test
    public void doTransferHappyPathAcuracyToTwoDecimalPlaces() throws Exception {
        Account origin = new Account("Id-123");
        Account beneficiary = new Account("Id-456");
        origin.setBalance(new BigDecimal(500));
        beneficiary.setBalance(new BigDecimal(0));
        this.accountsService.createAccount(origin);
        this.accountsService.createAccount(beneficiary);

        assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(origin);
        assertThat(this.accountsService.getAccount("Id-456")).isEqualTo(beneficiary);

        TransferDTO transferDTO = new TransferDTO("Id-123", "Id-456", "100.75");
        this.accountsService.doTransfer(transferDTO);
        String debitNotification = String.format(NotificationService.DEBIT_NOTIFICATION_MESSAGE, "100.75", "Id-456");
        String creditNotification = String.format(NotificationService.CREDIT_NOTIFICATION_MESSAGE, "100.75", "Id-123");
        verify(this.notificationService).notifyAboutTransfer(this.accountsService.getAccountsRepository().getAccount("Id-123"), debitNotification);
        verify(this.notificationService).notifyAboutTransfer(this.accountsService.getAccountsRepository().getAccount("Id-456"), creditNotification);

        BigDecimal beneficiaryBalance = this.accountsService.getAccount("Id-456").getBalance();
        BigDecimal beneficiaryBalanceRounded = beneficiaryBalance.round(new MathContext(5, RoundingMode.HALF_UP));
        BigDecimal originatorBalance = this.accountsService.getAccount("Id-123").getBalance();
        BigDecimal originatorBalanceRounded = originatorBalance.round(new MathContext(5, RoundingMode.HALF_UP));
        assertEquals(originatorBalanceRounded.toString(), "399.25");
        assertEquals(beneficiaryBalanceRounded.toString(), "100.75");

    }

    @Test
    public void doTransferHappyPathBoundaryInOrigin() throws Exception {
        Account origin = new Account("Id-123");
        Account beneficiary = new Account("Id-456");
        origin.setBalance(new BigDecimal(500));
        beneficiary.setBalance(new BigDecimal(0));
        this.accountsService.createAccount(origin);
        this.accountsService.createAccount(beneficiary);

        assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(origin);
        assertThat(this.accountsService.getAccount("Id-456")).isEqualTo(beneficiary);

        TransferDTO transferDTO = new TransferDTO("Id-123", "Id-456", "500");
        this.accountsService.doTransfer(transferDTO);

        String debitNotification = String.format(NotificationService.DEBIT_NOTIFICATION_MESSAGE, "500", "Id-456");
        String creditNotification = String.format(NotificationService.CREDIT_NOTIFICATION_MESSAGE, "500", "Id-123");
        verify(this.notificationService).notifyAboutTransfer(this.accountsService.getAccountsRepository().getAccount("Id-123"), debitNotification);
        verify(this.notificationService).notifyAboutTransfer(this.accountsService.getAccountsRepository().getAccount("Id-456"), creditNotification);


        assertThat(this.accountsService.getAccount("Id-456").getBalance()).isEqualTo(new BigDecimal(500));
        assertThat(this.accountsService.getAccount("Id-123").getBalance()).isEqualTo(new BigDecimal(0));
    }

    @Test
    public void doTransferHappyPathTransferFromToSameAccount() throws Exception {
        Account origin = new Account("Id-123");
        origin.setBalance(new BigDecimal(500));
        this.accountsService.createAccount(origin);

        assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(origin);

        TransferDTO transferDTO = new TransferDTO("Id-123", "Id-123", "500");
        this.accountsService.doTransfer(transferDTO);

        String debitNotification = String.format(NotificationService.DEBIT_NOTIFICATION_MESSAGE, "500", "Id-123");
        String creditNotification = String.format(NotificationService.CREDIT_NOTIFICATION_MESSAGE, "500", "Id-123");
        verify(this.notificationService).notifyAboutTransfer(this.accountsService.getAccountsRepository().getAccount("Id-123"), debitNotification);
        verify(this.notificationService).notifyAboutTransfer(this.accountsService.getAccountsRepository().getAccount("Id-123"), creditNotification);

        assertThat(this.accountsService.getAccount("Id-123").getBalance()).isEqualTo(new BigDecimal(500));
    }

    @Test
    public void doTransferInvalidDebitAccountIntoDeficit() throws Exception {
        Account origin = new Account("Id-123");
        Account beneficiary = new Account("Id-456");
        origin.setBalance(new BigDecimal(500));
        beneficiary.setBalance(new BigDecimal(0));
        this.accountsService.createAccount(origin);
        this.accountsService.createAccount(beneficiary);

        assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(origin);
        assertThat(this.accountsService.getAccount("Id-456")).isEqualTo(beneficiary);

        try {
            TransferDTO transferDTO = new TransferDTO("Id-123", "Id-456", "501");
            this.accountsService.doTransfer(transferDTO);
        } catch (AccountWouldBeInDeficitException e) {
            e.printStackTrace();
        }
        String debitNotification = String.format(NotificationService.DEBIT_NOTIFICATION_MESSAGE, "501", "Id-456");
        String creditNotification = String.format(NotificationService.CREDIT_NOTIFICATION_MESSAGE, "501", "Id-123");
        verify(this.notificationService, never()).notifyAboutTransfer(this.accountsService.getAccountsRepository().getAccount("Id-456"), debitNotification);
        verify(this.notificationService, never()).notifyAboutTransfer(this.accountsService.getAccountsRepository().getAccount("Id-123"), creditNotification);
        assertThat(this.accountsService.getAccount("Id-456").getBalance()).isEqualTo(new BigDecimal(0));
        assertThat(this.accountsService.getAccount("Id-123").getBalance()).isEqualTo(new BigDecimal(500));
    }

    @Test
    public void doTransferInvalidSomoneClearsAllTheAccountsBeforeTransfer() throws Exception {
        Account origin = new Account("Id-123");
        Account beneficiary = new Account("Id-456");
        origin.setBalance(new BigDecimal(500));
        beneficiary.setBalance(new BigDecimal(0));
        this.accountsService.createAccount(origin);
        this.accountsService.createAccount(beneficiary);

        assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(origin);
        assertThat(this.accountsService.getAccount("Id-456")).isEqualTo(beneficiary);

        accountsService.getAccountsRepository().clearAccounts();
        TransferDTO transferDTO = new TransferDTO("Id-123", "Id-456", "500");
        try {
            this.accountsService.doTransfer(transferDTO);
            assertTrue(false);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(true);
        }

        String debitNotification = String.format(NotificationService.DEBIT_NOTIFICATION_MESSAGE, "500", "Id-456");
        String creditNotification = String.format(NotificationService.CREDIT_NOTIFICATION_MESSAGE, "500", "Id-123");
        verify(this.notificationService, never()).notifyAboutTransfer(this.accountsService.getAccountsRepository().getAccount("Id-456"), debitNotification);
        verify(this.notificationService, never()).notifyAboutTransfer(this.accountsService.getAccountsRepository().getAccount("Id-123"), creditNotification);
    }


}
