package ru.averkiev.bankservice.models;

import jakarta.persistence.Column;
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
    private static final String CODE_BANK = "4070281050000";

    /** Номер счета внутри отделения банка */
    private static int number = 1;

    /** Номер счета */
    @Column(name = "number")
    private String accountNumber = CODE_BANK + String.format("%06d", number++);

    /** Название счета */
    @Column(name = "name")
    private String accountName;

    /** Баланс счета */
    @Column(name = "balance")
    private Double accountBalance = 0.;

    /** Пин-код для доступа к счету */
    @Column(name = "pin_code")
    private String pin;

}
