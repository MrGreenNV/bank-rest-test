package ru.averkievnv.bankservice.models;

/**
 * Класс, представляющий объект для обновления названия банковского счета.
 * @author mrGreenNV
 */
public class AccountUpdateNameDTO {

    /** Название счета */
    private String accountName;

    /** Новое название счета */
    private String updatedAccountName;

    /** Пин-код для доступа к счету */
    private String pin;

}
