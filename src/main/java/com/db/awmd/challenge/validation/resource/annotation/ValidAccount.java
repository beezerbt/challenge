package com.db.awmd.challenge.validation.resource.annotation;

import com.db.awmd.challenge.validation.resource.AccountValidator;
import com.db.awmd.challenge.web.AccountsController;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, ANNOTATION_TYPE, PARAMETER, METHOD, LOCAL_VARIABLE, FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = AccountValidator.class)
public @interface ValidAccount {
    String message() default "Account is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
