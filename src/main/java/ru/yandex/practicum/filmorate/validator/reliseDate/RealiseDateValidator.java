package ru.yandex.practicum.filmorate.validator.reliseDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class RealiseDateValidator implements ConstraintValidator<RealiseDateConstraint, LocalDate> {
    private static final LocalDate MIN_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 27);

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (localDate == null) {
            return true;
        }
        return localDate.isAfter(MIN_FILM_RELEASE_DATE);
    }
}
