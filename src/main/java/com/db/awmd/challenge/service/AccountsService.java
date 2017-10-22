package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.dto.TransferDTO;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.validation.ResourceValidator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.math.BigDecimal;

@Service
public class AccountsService {

    @Getter
    private final AccountsRepository accountsRepository;
    @Setter
    private NotificationService notificationService;
    @Autowired
    ResourceValidator resourceValidator;


    @Autowired
    public AccountsService(AccountsRepository accountsRepository) {
        this.accountsRepository = accountsRepository;
    }

    public void createAccount(Account account) {
        this.accountsRepository.createAccount(account);
    }

    public Account getAccount(String accountId) {
        return this.accountsRepository.getAccount(accountId);
    }

    public void doTransfer(TransferDTO transferDTO) {
        //Ran out of time....custom validators would not fire!! resorted to below.
        resourceValidator.validate(transferDTO);
        if (accountsRepository.transferAmount(transferDTO.getOriginatingAccountId(), transferDTO.getBeneficiaryAccountId(), transferDTO.getTransferAmoutEuqivalent())) {
            notificationService.notifyAboutTransfer(getAccountsRepository().getAccount(transferDTO.getOriginatingAccountId()), String.format(NotificationService.DEBIT_NOTIFICATION_MESSAGE, transferDTO.getTransferAmount(), transferDTO.getBeneficiaryAccountId()));
            notificationService.notifyAboutTransfer(getAccountsRepository().getAccount(transferDTO.getBeneficiaryAccountId()), String.format(NotificationService.CREDIT_NOTIFICATION_MESSAGE, transferDTO.getTransferAmount(), transferDTO.getOriginatingAccountId()));
        }
    }
}
