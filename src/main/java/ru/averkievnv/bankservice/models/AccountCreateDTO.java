package ru.averkievnv.bankservice.models;

import lombok.Getter;
import lombok.Setter;
import ru.averkievnv.bankservice.validations.CustomPin;

/**
 * Класс, представляющий объект для создания банковского счета.
 * @author mrGreenNV
 */
@Getter
@Setter
public class AccountCreateDTO {

    /** Название счета */
    private String accountName;

    /** Пин-код для доступа к счету */
    @CustomPin
    private String pin;

}
