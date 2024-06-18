package ru.yandex.practicum.filmorate.validator.reliseDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class RealiseDateValidator implements ConstraintValidator<RealiseDateConstraint, LocalDate> {
    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (localDate == null) {
            return true;
        }
        return localDate.isAfter(LocalDate.of(1895, 12, 27));
    }
}
