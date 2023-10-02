package ru.averkiev.bankservice.exceptions;

/**
 * Класс представляет собой исключение, сообщающее об ошибке при поиске счета.
 * @author mrGreenNV
 */
public class AccountNotFoundException extends RuntimeException {

    /**
     * Создаёт новый экземпляр исключения с указанным сообщением об ошибке.
     * @param message - сообщение об ошибке.
     */
    public AccountNotFoundException(String message) {
        super(message);
    }

}
