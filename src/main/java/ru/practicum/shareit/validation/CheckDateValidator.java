package ru.practicum.shareit.validation;

import ru.practicum.shareit.booking.dto.BookingSavingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDateValidator implements ConstraintValidator<StartBeforeEndDateValid, BookingSavingDto> {
    @Override
    public void initialize(StartBeforeEndDateValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingSavingDto bookingSavingDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = bookingSavingDto.getStart();
        LocalDateTime end = bookingSavingDto.getEnd();
        if (start == null || end == null || start.equals(end)) {
            return false;
        }
        return start.isBefore(end);
    }
}
