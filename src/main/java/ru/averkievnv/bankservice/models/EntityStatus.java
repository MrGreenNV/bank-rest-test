package ru.averkievnv.bankservice.models;

/**
 * Статус сущностей в системе.
 * @author mrGreenNV
 */
public enum EntityStatus {

    /** Сущность активна. */
    ACTIVE,

    /** Сущность неактивна. */
    NOT_ACTIVE,

    /** Сущность помечена на удаление. */
    DELETED
}
