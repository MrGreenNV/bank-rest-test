package ru.averkievnv.bankservice.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Класс, представляющий объект для произведения операций с банковским счетом.
 * @author mrGreenNV
 */
@Getter
@Setter
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
