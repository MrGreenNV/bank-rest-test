package ru.averkievnv.bankservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс представляет собой модель банковского счета.
 * @author mrGreenNV
 */
@Entity
@Table(name = "accounts")
@Getter
@Setter
public class Account extends BaseEntity {

    /** Постоянная часть счета для конкретного отделения банка */
    private static final String p = "4070281050000";

    /** Номер счета */
    private String accountNumber = p + String.format("%07d", getId());

    /** Имя счета */
    private String accountName;

    /** Баланс счета */
    private Double accountBalance = 0.;

    /** Пин-код для доступа к счету */
    private String pin;

}
