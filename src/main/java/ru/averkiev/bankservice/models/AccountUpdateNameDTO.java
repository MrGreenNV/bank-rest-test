package ru.averkiev.bankservice.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Класс, представляющий объект для обновления названия банковского счета.
 * @author mrGreenNV
 */
@Getter
@Setter
public class AccountUpdateNameDTO {

    /** Новое название счета */
    private String updatedAccountName;

    /** Пин-код для доступа к счету */
    private String pin;

}
