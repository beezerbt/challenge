package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.AccountWouldBeInDeficitException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.StampedLock;

import org.springframework.stereotype.Repository;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final StampedLock lock = new StampedLock();

    @Override
    public void createAccount(Account account) throws DuplicateAccountIdException {
        Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
        if (previousAccount != null) {
            throw new DuplicateAccountIdException(
                    "Account id " + account.getAccountId() + " already exists!");
        }
    }

    @Override
    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    @Override
    //This should be a privileged operation...as its public and can cause havoc
    public void clearAccounts() {
        accounts.clear();
    }

    private BigDecimal debitAccount(String debitAccountId, BigDecimal debitAmount) {
        Account debitAccount = getAccount(debitAccountId);
        BigDecimal predebitBalance = debitAccount.getBalance();
        BigDecimal debitedBalane = predebitBalance.subtract(debitAmount);
        if (debitedBalane.compareTo(BigDecimal.ZERO) < 0) {
            throw new AccountWouldBeInDeficitException("Debit action during transfer not possible for account:["+debitAccountId+"]. Overdraft not supported.");
        }
        Account debitedAccount = new Account(debitAccountId, debitedBalane);
        accounts.replace(debitAccountId, debitAccount, debitedAccount);
        return getAccount(debitAccountId).getBalance();
    }

    @Override
    public boolean transferAmount(String originatingAcountId, String beneficiaryAccountId, BigDecimal transferAmount) {
        if(accounts.size() == 0) {
            return false;
        }
        long stamp = lock.writeLock();
        try {
            debitAccount(originatingAcountId, transferAmount);
            creditAccount(beneficiaryAccountId, transferAmount);
        } finally {
            lock.unlockWrite(stamp);
        }
        return true;
    }

    private BigDecimal creditAccount(String creditAccountId, BigDecimal creditAmount) {
        Account creditAccount = getAccount(creditAccountId);
        BigDecimal preCreditBalance = creditAccount.getBalance();
        BigDecimal creditedBalane = preCreditBalance.add(creditAmount);
        Account creditedAccount = new Account(creditAccountId, creditedBalane);
        accounts.replace(creditAccountId, creditAccount, creditedAccount);
        return getAccount(creditAccountId).getBalance();
    }
}
