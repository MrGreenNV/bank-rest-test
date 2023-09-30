package ru.averkievnv.bankservice.services;

import ru.averkievnv.bankservice.exceptions.*;
import ru.averkievnv.bankservice.models.*;

import java.util.List;

/**
 * @author mrGreenNV
 */
public interface AccountService {

    /**
     * Создает новый банковский счет.
     * @param accountCreateDTO Данные для создания счета.
     * @return Информация о созданном счете.
     * @throws AccountNotCreatedException Выбрасывает при возникновении ошибки на этапе создания счета.
     */
    AccountInfoDTO createAccount(AccountCreateDTO accountCreateDTO)
            throws AccountNotCreatedException;

    /**
     * Обновляет наименование счета.
     * @param accountId Идентификатор обновляемого счета.
     * @param accountUpdateNameDTO Данные для обновления счета.
     * @return Информация об обновленном счете.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     * @throws AccountWithNameAlreadyExistsException Выбрасывает при возникновении ошибки дублирования названия счета.
     */
    AccountInfoDTO updateAccountName(Long accountId, AccountUpdateNameDTO accountUpdateNameDTO)
            throws AccountNotFoundException, AccountWithNameAlreadyExistsException;

    /**
     * Получает информацию о счете по его идентификатору.
     * @param accountId Идентификатор счета.
     * @return Информацию о счете.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     */
    AccountDTO getAccount(Long accountId)
            throws AccountNotFoundException;

    /**
     * Получает информацию о счете по его названию.
     * @param accountName Название счета.
     * @return Информация о счете.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     */
    AccountDTO getAccount(String accountName)
            throws AccountNotFoundException;

    /**
     * Получает информацию о всех счетах.
     * @return Список объектов, содержащих информацию о всех счетах.
     */
    List<AccountInfoDTO> getAllAccounts();

    /**
     * Удаляет аккаунт по его идентификатору.
     * @param accountId Идентификатор счета.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     */
    void deleteAccount(Long accountId)
            throws AccountNotFoundException;

    /**
     * Удаляет аккаунт по его названию.
     * @param accountName Название счета.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     */
    void deleteAccount(String accountName)
            throws AccountNotFoundException;

    /**
     * Деактивирует счет по его идентификатору.
     * @param accountId Идентификатор счета.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     */
    void softDeleteAccount(Long accountId)
            throws AccountNotFoundException;

    /**
     * Деактивирует счет по его названию.
     * @param accountName Название счета.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     */
    void softDeleteAccount(String accountName)
            throws AccountNotFoundException;

    /**
     * Выполняет пополнение счета.
     * @param accountTransactionDTO Данные для совершения пополнения счета.
     * @return Информация о счете.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     */
    AccountInfoDTO deposit(AccountTransactionDTO accountTransactionDTO)
            throws AccountNotFoundException;

    /**
     * Выполняет снятие средств со счета.
     * @param accountTransactionDTO Данные для снятия средств со счета.
     * @return Информация о счете.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     * @throws AccountAccessException Выбрасывает при возникновении ошибки на этапе доступа к счету.
     * @throws AccountWithdrawException Выбрасывает при возникновении ошибки на этапе снятия средств со счета.
     */
    AccountInfoDTO withdraw(AccountTransactionDTO accountTransactionDTO)
            throws AccountNotFoundException, AccountAccessException, AccountWithdrawException;

    /**
     * Выполняет перевод средств между счетами.
     * @param accountTransactionDTO Данные для перевода средств.
     * @return Информация о счете.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     * @throws AccountAccessException Выбрасывает при возникновении ошибки на этапе доступа к счету.
     * @throws AccountWithdrawException Выбрасывает при возникновении ошибки на этапе снятия средств со счета.
     */
    AccountInfoDTO transfer(AccountTransactionDTO accountTransactionDTO)
            throws AccountNotFoundException, AccountAccessException, AccountWithdrawException;

}
