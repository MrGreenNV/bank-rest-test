package ru.averkievnv.bankservice.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Класс, представляющий объект для отображения информации о состоянии банковского счета.
 * @author mrGreenNV
 */
@Getter
@Setter
public class AccountInfoDTO {

    /** Название счета */
    private String accountName;

    /** Баланс счета */
    private Double accountBalance = 0.;
}