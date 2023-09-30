package ru.averkievnv.bankservice.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.averkievnv.bankservice.exceptions.*;
import ru.averkievnv.bankservice.models.*;
import ru.averkievnv.bankservice.repositories.AccountRepository;
import ru.averkievnv.bankservice.services.AccountService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author mrGreenNV
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    /** Репозиторий для взаимодействия с базой данных */
    private final AccountRepository accountRepository;

    /** Позволяет преобразовывать модели и DTO между собой */
    private final ModelMapper modelMapper;

    /** Позволяет шифровать данны */
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Создает новый банковский счет.
     * @param accountCreateDTO Данные для создания счета.
     * @return Информация о созданном счете.
     * @throws AccountCreatedException Выбрасывает при возникновении ошибки на этапе создания счета.
     */
    @Override
    public AccountInfoDTO createAccount(AccountCreateDTO accountCreateDTO) throws AccountCreatedException {

        String accountName = accountCreateDTO.getAccountName();

        try {
            if (accountName == null || accountName.equals("")) {
                log.error("IN createAccount - новая запись о банковском счете не сохранена");
                throw new NullPointerException("Ошибка при создании банковского счета. Название счета не может быть пустым");
            }

            if (existAccountByName(accountName)) {
                log.error("IN createAccount - новая запись о банковском счете: {} не сохранена", accountName);
                throw new AccountWithNameAlreadyExistsException("Ошибка при создании банковского счета. Название счета: " + accountName + " уже используется");
            }
        } catch (NullPointerException | AccountWithNameAlreadyExistsException ex) {
            throw new AccountCreatedException(ex.getMessage());
        }

        Account account = modelMapper.map(accountCreateDTO, Account.class);
        account.setPin(passwordEncoder.encode(account.getPin()));
        account = accountRepository.save(account);

        log.info("IN createAccount - новая запись о банковском счете: {} успешно сохранена", accountName);
        return modelMapper.map(account, AccountInfoDTO.class);
    }

    /**
     * Обновляет наименование счета.
     * @param accountId Идентификатор обновляемого счета.
     * @param accountUpdateNameDTO Данные для обновления счета.
     * @return Информация об обновленном счете.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     * @throws AccountWithNameAlreadyExistsException Выбрасывает при возникновении ошибки дублирования названия счета.
     */
    @Override
    public AccountInfoDTO updateAccountName(Long accountId, AccountUpdateNameDTO accountUpdateNameDTO) throws AccountNotFoundException, AccountWithNameAlreadyExistsException {

        Account account = getAccount(accountId);

        if (!passwordEncoder.matches(accountUpdateNameDTO.getPin(), account.getPin())) {
            log.error("IN updateAccountName - название счета: {} не обновлено", account.getAccountName());
            throw new AccountAccessException("Введен неверный пин код");
        }

        String newAccountName = accountUpdateNameDTO.getUpdatedAccountName();

        if (newAccountName == null || newAccountName.equals("")) {
            log.error("IN updateAccountName - название счета не обновлено");
            throw new NullPointerException("Название счета не должно быть пустым");
        }

        account.setAccountName(newAccountName);
        account = accountRepository.save(account);

        log.info("IN updateAccountName - название счета: {} успешно обновлено", account.getAccountName());
        return modelMapper.map(account, AccountInfoDTO.class);
    }

    /**
     * Получает информацию о счете по его идентификатору.
     * @param accountId Идентификатор счета.
     * @return Информацию о счете.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     */
    @Override
    public AccountDTO getInfoAccount(Long accountId) throws AccountNotFoundException {
        Account account = getAccount(accountId);
        return modelMapper.map(account, AccountDTO.class);
    }

    /**
     * Получает информацию о всех счетах.
     * @return Список объектов, содержащих информацию о всех счетах.
     */
    @Override
    public List<AccountInfoDTO> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(account -> modelMapper.map(account, AccountInfoDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Удаляет счет по его идентификатору.
     * @param accountId Идентификатор счета.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     */
    @Override
    public void deleteAccount(Long accountId) throws AccountNotFoundException {
        if (notExistAccountById(accountId)) {
            log.error("IN deleteAccount - счет с идентификатором: {} не удален", accountId);
            throw new AccountNotFoundException("Счет с идентификатором: " + accountId + " не найден");
        }
        accountRepository.deleteById(accountId);

        log.info("IN deleteAccount - счет с идентификатором: {} успешно удален", accountId);
    }

    /**
     * Деактивирует счет по его идентификатору.
     * @param accountId Идентификатор счета.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     */
    @Override
    public void softDeleteAccount(Long accountId) throws AccountNotFoundException {
        if (notExistAccountById(accountId)) {
            log.error("IN softDeleteAccount - счет с идентификатором: {} не деактивирован", accountId);
            throw new AccountNotFoundException("Счет с идентификатором: " + accountId + " не найден");
        }

        Account account = getAccount(accountId);
        account.setEntityStatus(EntityStatus.DELETED);
        accountRepository.save(account);

        log.info("IN softDeleteAccount - счет с идентификатором: {} успешно деактивирован", accountId);
    }

    /**
     * Выполняет пополнение счета.
     * @param accountId Идентификатор пополняемого счета.
     * @param accountTransactionDTO Данные для совершения пополнения счета.
     * @return Информация о счете.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     */
    @Override
    public AccountInfoDTO deposit(Long accountId, AccountTransactionDTO accountTransactionDTO) throws AccountNotFoundException {

        Account account = getAccount(accountId);
        Double amount = accountTransactionDTO.getTransferAmount();

        if (amount == null || amount <= 0) {
            log.error("IN deposit - банковский счет с названием: {} не пополнен", account.getAccountName());
            throw new AccountWithdrawException("Сумма пополнения должна быть положительной и не может быть пустой");
        }

        account.setAccountBalance(account.getAccountBalance() + amount);
        account = accountRepository.save(account);

        log.info("IN deposit - банковский счет с названием: {} успешно пополнен на сумму: {}", account.getAccountName(), amount);
        return modelMapper.map(account, AccountInfoDTO.class);
    }

    /**
     * Выполняет списание средств со счета.
     * @param accountId Идентификатор счета, с которого происходит списание средств.
     * @param accountTransactionDTO Данные для списания средств со счета.
     * @return Информация о счете.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     * @throws AccountAccessException Выбрасывает при возникновении ошибки на этапе доступа к счету.
     * @throws AccountWithdrawException Выбрасывает при возникновении ошибки на этапе списание средств со счета.
     */
    @Override
    public AccountInfoDTO withdraw(Long accountId, AccountTransactionDTO accountTransactionDTO) throws AccountNotFoundException, AccountAccessException, AccountWithdrawException {

        Account account = getAccount(accountId);

        if (!passwordEncoder.matches(accountTransactionDTO.getPin(), account.getPin())) {
            log.error("IN withdraw - списание средств с банковского счета с названием: {} не прошло", account.getAccountName());
            throw new AccountAccessException("Введен неверный пин код");
        }

        Double amount = accountTransactionDTO.getTransferAmount();

        try {
            if (amount == null || amount <= 0) {
                throw new AccountWithdrawException("Сумма списания должна быть положительной и не может быть пустой");
            }

            if (account.getAccountBalance() - amount < 0) {
                throw new AccountWithdrawException("Сумма списания должна быть не больше текущего баланса");
            }
        } catch (AccountWithdrawException awEx) {
            log.error("IN withdraw - списание средств с банковского счета с названием: {} не прошло", account.getAccountName());
            throw new AccountWithdrawException(awEx.getMessage());
        }

        account.setAccountBalance(account.getAccountBalance() - amount);
        account = accountRepository.save(account);

        log.info("IN withdraw - списание средств на сумму: {} с банковского счета с названием: {} успешно прошло", amount, account.getAccountName());
        return modelMapper.map(account, AccountInfoDTO.class);
    }

    /**
     * Выполняет перевод средств между счетами.
     * @param accountId Идентификатор счета, с которого происходит списание средств.
     * @param accountTransactionDTO Данные для перевода средств.
     * @return Информация о счете.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     * @throws AccountAccessException Выбрасывает при возникновении ошибки на этапе доступа к счету.
     * @throws AccountWithdrawException Выбрасывает при возникновении ошибки на этапе снятия средств со счета.
     */
    @Override
    public AccountInfoDTO transfer(Long accountId, AccountTransactionDTO accountTransactionDTO) throws AccountNotFoundException, AccountAccessException, AccountWithdrawException {

        Account account = getAccount(accountId);

        if (!passwordEncoder.matches(accountTransactionDTO.getPin(), account.getPin())) {
            log.error("IN transfer - перевод средств с банковского счета с названием: {} не прошел", account.getAccountName());
            throw new AccountAccessException("Введен неверный пин код");
        }

        String sourceAccountName = accountTransactionDTO.getSourceAccountName();
        Account sourceAccount = getAccountByName(sourceAccountName);

        Double amount = accountTransactionDTO.getTransferAmount();

        try {
            if (amount == null || amount <= 0) {
                throw new AccountWithdrawException("Сумма перевода должна быть положительной и не может быть пустой");
            }

            if (account.getAccountBalance() - amount < 0) {
                throw new AccountWithdrawException("Сумма перевода должна быть не больше текущего баланса");
            }
        } catch (AccountWithdrawException awEx) {
            log.error("IN transfer - перевод средств с банковского счета с названием: {} не прошел", account.getAccountName());
            throw new AccountWithdrawException(awEx.getMessage());
        }

        account.setAccountBalance(account.getAccountBalance() - amount);
        account = accountRepository.save(account);

        sourceAccount.setAccountBalance(sourceAccount.getAccountBalance() + amount);
        accountRepository.save(sourceAccount);

        log.info("IN transfer - перевод средств на сумму: {} с банковского счета с названием: {} успешно прошел", amount, account.getAccountName());
        return modelMapper.map(account, AccountInfoDTO.class);
    }

    /**
     * Получает полную информацию о счете по его идентификатору.
     * @param accountId Идентификатор счета.
     * @return Информация о счете.
     */
    private Account getAccount(Long accountId) throws AccountNotFoundException {
        Optional<Account> account = accountRepository.findById(accountId);

        if (account.isEmpty()) {
            log.error("IN getAccount - счет с идентификатором: {} не найден", accountId);
            throw new AccountNotFoundException("Счет с идентификатором: " + accountId + " не найден");
        }

        log.info("IN getAccount - счет с идентификатором: {} успешно найден", accountId);
        return account.get();
    }

    /**
     * Получает полную информацию о счете по его названию.
     * @param accountName Название счета.
     * @return Информация о счете.
     */
    private Account getAccountByName(String accountName) throws AccountNotFoundException {
        Optional<Account> account = accountRepository.findAccountByAccountName(accountName);

        if (account.isEmpty()) {
            log.error("IN getAccountByName - счет с названием: {} не найден", accountName);
            throw new AccountNotFoundException("Счет с идентификатором: " + accountName + " не найден");
        }

        log.info("IN getAccountByName - счет с названием: {} успешно найден", accountName);
        return account.get();
    }

    /**
     * Проверяет существование в базе данных записи о счете по переданному идентификатору счета.
     * @param accountId Идентификатор счета.
     * @return true, если запись не найдена, иначе - false.
     */
    private boolean notExistAccountById(Long accountId) {
        return !accountRepository.existsById(accountId);
    }

    /**
     * Проверяет существование в базе данных записи о счете по переданному названию счета.
     * @param accountName Название счета.
     * @return true, если запись найдена, иначе - false.
     */
    private boolean existAccountByName(String accountName) {
        return accountRepository.existsAccountByAccountName(accountName);
    }
}
