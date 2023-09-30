package ru.averkievnv.bankservice.models;

/**
 * Класс, представляющий объект для произведения операций с банковским счетом.
 * @author mrGreenNV
 */
public class AccountTransactionDTO {

    /** Название счета с которого производится операция */
    private String currentAccountName;

    /** Название счета на который совершается перевод */
    private String sourceAccountName;

    /** Сумма операции */
    private Double transferAmount;

    /** Пин-код для доступа к счету с которого производится операция */
    private String pin;
}
