package ru.averkiev.bankservice.exceptions;

/**
 * Класс представляет собой исключение, сообщающее о дублировании названия счета.
 * @author mrGreenNV
 */
public class AccountWithNameAlreadyExistsException extends RuntimeException {

    /**
     * Создаёт новый экземпляр исключения с указанным сообщением об ошибке.
     * @param message - сообщение об ошибке.
     */
    public AccountWithNameAlreadyExistsException(String message) {
        super(message);
    }
}
