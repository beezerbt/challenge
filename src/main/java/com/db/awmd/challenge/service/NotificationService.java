package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import lombok.Data;
import lombok.Value;

public interface NotificationService {
  String CURRENT_CURRENCY = "Euro";
  String DEBIT_NOTIFICATION_MESSAGE = "Fund Transfer Notification. "+CURRENT_CURRENCY+" %s was debited from you account and credited to the beneficiary account number %s";
  String CREDIT_NOTIFICATION_MESSAGE = "Fund Transfer Notification. "+CURRENT_CURRENCY+" %s was credited from originating account number %s to your account.";
  void notifyAboutTransfer(Account account, String transferDescription);
}
