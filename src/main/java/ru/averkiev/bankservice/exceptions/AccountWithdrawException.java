package ru.averkiev.bankservice.exceptions;

/**
 * Класс представляет собой исключение, сообщающее об ошибке при списании средств с банковского счета.
 * @author mrGreenNV
 */
public class AccountWithdrawException extends RuntimeException {

    /**
     * Создаёт новый экземпляр исключения с указанным сообщением об ошибке.
     * @param message - сообщение об ошибке.
     */
    public AccountWithdrawException(String message) {
        super(message);
    }
}
