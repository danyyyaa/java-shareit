package ru.practicum.shareit.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ValuesAllowedConstraintValidatorTests {
    @Mock
    private ConstraintValidatorContext context;

    @InjectMocks
    private ValuesAllowedConstraintValidator validator;

    @Test
    void shouldReturnTrueForNullValue() {
        validator.initialize(createAnnotation("value1", "value2"));
        assertTrue(validator.isValid(null, context));
    }

    @Test
    void shouldReturnTrueForValidValue() {
        validator.initialize(createAnnotation("value1", "value2"));
        assertTrue(validator.isValid("value1", context));
    }

    private ValuesAllowedConstraint createAnnotation(String... values) {
        return new ValuesAllowedConstraint() {
            @Override
            public String message() {
                return "{value.hasWrong}";
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public String propName() {
                return "testProp";
            }

            @Override
            public String[] values() {
                return values;
            }

            @Override
            public Class<ValuesAllowedConstraint> annotationType() {
                return ValuesAllowedConstraint.class;
            }
        };
    }
}
