package com.db.awmd.challenge.validation;

import com.db.awmd.challenge.exception.RestValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

@Component
public class ResourceValidator {

    private final Validator validator;

    @Autowired
    public ResourceValidator(Validator validator) {
        this.validator = validator;
    }

    public <T> void validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        Set<FieldErrorTO> errors = newHashSet();

        if (!violations.isEmpty()) {
            for (ConstraintViolation<T> violation : violations) {
                errors.add(new FieldErrorTO(violation.getPropertyPath().toString(), violation.getMessage()));
            }
            throw new RestValidationException(errors);
        }
    }
}
