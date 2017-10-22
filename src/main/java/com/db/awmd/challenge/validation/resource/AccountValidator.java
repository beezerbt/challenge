package com.db.awmd.challenge.validation.resource;

import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.validation.resource.annotation.ValidAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class AccountValidator implements ConstraintValidator<ValidAccount, String> {

    @Autowired
    AccountsService accountsService;

    @Override
    public void initialize(ValidAccount constraintAnnotation) {

    }

    @Override
    public boolean isValid(String accountId, ConstraintValidatorContext context) {
        if(accountsService.getAccount(accountId) == null) {
            return false;
        }
        return true;
    }
}