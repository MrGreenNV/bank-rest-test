package ru.averkievnv.bankservice.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Класс, представляющий объект для доступа к банковскому счету.
 * @author mrGreenNV
 */
@Getter
@Setter
public class AccountDTO {

    /** Идентификатор счета */
    private Long id;

    /** Название счета */
    private String accountName;

    /** Баланс счета */
    private Double balance;
}
