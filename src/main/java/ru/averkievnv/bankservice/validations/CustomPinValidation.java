package ru.averkievnv.bankservice.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Класс представляет собой валидатор для проверки пин-кода.
 * @author mrGreenNV
 */
public class CustomPinValidation implements ConstraintValidator<CustomPin, String> {

    /** Регулярное выражение для проверки электронной почты. */
    private final static Pattern PIN_PATTERN = Pattern.compile("[0-9]{4}");

    /**
     * Инициализирует валидацию.
     * @param constraintAnnotation инициализируемая аннотация
     */
    @Override
    public void initialize(CustomPin constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    /**
     * Проверяет валидность электронной почты.
     * @param value проверяемая электронная почта.
     * @param context контекст.
     * @return true если электронная почта валидна, иначе false
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null) {
            return false;
        }

        return PIN_PATTERN.matcher(value).matches();
    }
}
