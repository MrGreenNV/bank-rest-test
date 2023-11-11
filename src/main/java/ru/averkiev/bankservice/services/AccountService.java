package ru.averkiev.bankservice.services;

import ru.averkiev.bankservice.exceptions.*;
import ru.averkiev.bankservice.models.*;

import java.util.List;

/**
 * @author mrGreenNV
 */
public interface AccountService {

    /**
     * Создает новый банковский счет.
     * @param accountCreateDTO Данные для создания счета.
     * @return Информация о созданном счете.
     * @throws AccountCreatedException Выбрасывает при возникновении ошибки на этапе создания счета.
     */
    AccountInfoDTO createAccount(AccountCreateDTO accountCreateDTO)
            throws AccountCreatedException;

    /**
     * Обновляет наименование счета.
     * @param accountId Идентификатор обновляемого счета.
     * @param accountUpdateNameDTO Данные для обновления счета.
     * @return Информация об обновленном счете.
     * @throws AccountAccessException Выбрасывает при возникновении ошибки на этапе доступа к счету.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     * @throws AccountWithNameAlreadyExistsException Выбрасывает при возникновении ошибки дублирования названия счета.
     */
    AccountInfoDTO updateAccountName(Long accountId, AccountUpdateNameDTO accountUpdateNameDTO)
            throws AccountAccessException, AccountNotFoundException, AccountWithNameAlreadyExistsException;

    /**
     * Получает информацию о счете по его идентификатору.
     * @param accountId Идентификатор счета.
     * @return Информацию о счете.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     */
    AccountDTO getInfoAccount(Long accountId)
            throws AccountNotFoundException;

    /**
     * Получает информацию о всех счетах.
     * @return Список объектов, содержащих информацию о всех счетах.
     */
    List<AccountInfoDTO> getAllAccounts();

    /**
     * Удаляет счет по его идентификатору.
     * @param accountId Идентификатор счета.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     */
    void deleteAccount(Long accountId)
            throws AccountNotFoundException;

    /**
     * Деактивирует счет по его идентификатору.
     * @param accountId Идентификатор счета.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     */
    void softDeleteAccount(Long accountId)
            throws AccountNotFoundException;

    /**
     * Выполняет пополнение счета.
     * @param accountId Идентификатор пополняемого счета.
     * @param accountTransactionDTO Данные для совершения пополнения счета.
     * @return Информация о счете.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     * @throws AccountWithdrawException Выбрасывает при возникновении ошибки на этапе списание средств со счета.
     */
    AccountInfoDTO deposit(Long accountId, AccountTransactionDTO accountTransactionDTO)
            throws AccountWithdrawException, AccountNotFoundException;

    /**
     * Выполняет списание средств со счета.
     * @param accountId Идентификатор счета, с которого происходит списание средств.
     * @param accountTransactionDTO Данные для списания средств со счета.
     * @return Информация о счете.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     * @throws AccountAccessException Выбрасывает при возникновении ошибки на этапе доступа к счету.
     * @throws AccountWithdrawException Выбрасывает при возникновении ошибки на этапе списание средств со счета.
     */
    AccountInfoDTO withdraw(Long accountId, AccountTransactionDTO accountTransactionDTO)
            throws AccountNotFoundException, AccountAccessException, AccountWithdrawException;

    /**
     * Выполняет перевод средств между счетами.
     * @param accountId Идентификатор счета, с которого происходит списание средств.
     * @param accountTransactionDTO Данные для перевода средств.
     * @return Информация о счете.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     * @throws AccountAccessException Выбрасывает при возникновении ошибки на этапе доступа к счету.
     * @throws AccountWithdrawException Выбрасывает при возникновении ошибки на этапе снятия средств со счета.
     */
    AccountInfoDTO transfer(Long accountId, AccountTransactionDTO accountTransactionDTO)
            throws AccountNotFoundException, AccountAccessException, AccountWithdrawException;

}
