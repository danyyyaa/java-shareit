package ru.practicum.shareit.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class ValuesAllowedConstraintValidator implements ConstraintValidator<ValuesAllowedConstraint, String> {
    private String message;
    private List<String> allowable;

    @Override
    public void initialize(ValuesAllowedConstraint constraintAnnotation) {
        this.message = constraintAnnotation.message();
        this.allowable = Arrays.asList(constraintAnnotation.values());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean valid = value == null || this.allowable.stream().anyMatch(v -> v.equalsIgnoreCase(value));

        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }

        return valid;
    }
}
