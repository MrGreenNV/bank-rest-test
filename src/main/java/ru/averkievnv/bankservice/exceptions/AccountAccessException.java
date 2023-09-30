package ru.averkievnv.bankservice.exceptions;

/**
 * Класс представляет собой исключение, сообщающее об ошибке при попытке доступа к банковскому счету.
 * @author mrGreenNV
 */
public class AccountAccessException extends RuntimeException {

    /**
     * Создаёт новый экземпляр исключения с указанным сообщением об ошибке.
     * @param message - сообщение об ошибке.
     */
    public AccountAccessException(String message) {
        super(message);
    }
}
