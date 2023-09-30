package ru.averkievnv.bankservice.exceptions;

/**
 * Класс представляет собой исключение, сообщающее об ошибке при создании счета.
 * @author mrGreenNV
 */
public class AccountNotCreatedException extends RuntimeException {

    /**
     * Создаёт новый экземпляр исключения с указанным сообщением об ошибке.
     * @param message - сообщение об ошибке.
     */
    public AccountNotCreatedException(String message) {
        super(message);
    }

}
