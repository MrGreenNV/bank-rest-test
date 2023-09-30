package ru.averkievnv.bankservice.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Аннотация для валидации пин-кода.
 * @author mrGreenNV
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = CustomPinValidation.class)
public @interface CustomPin {

    /**
     * Определяет сообщение, которое будет отображаться при нарушении валидации.
     * @return сообщение.
     */
    String message() default "Пин-код должен состоять из четырех цифр";

    /**
     * Определяет группы ограничений, которым будет принадлежать аннотация.
     * @return группы ограничений.
     */
    Class<?>[] groups() default {};

    /**
     * Определяет нагрузку (payload) для аннотации, которая может быть использована для передачи дополнительной
     * информации в процессе валидации.
     * @return нагрузка для аннотации.
     */
    Class<? extends Payload>[] payload() default {};
}
