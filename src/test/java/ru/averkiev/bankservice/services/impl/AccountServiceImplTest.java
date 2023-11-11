package ru.averkiev.bankservice.services.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.averkiev.bankservice.exceptions.*;
import ru.averkiev.bankservice.models.*;
import ru.averkiev.bankservice.repositories.AccountRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Класс тестирует функциональность сервисного уровня при взаимодействии со счетом.
 * @author mrGreenNV
 */
@Tag("account-service")
class AccountServiceImplTest {

    private AutoCloseable closeable;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    public void openMocks() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void releaseMocks() throws Exception {
        closeable.close();
    }

    /**
     * Проверяет создание банковского счета с валидными данными.
     * @throws AccountCreatedException Выбрасывает при возникновении ошибки на этапе создания счета.
     */
    @Test
    @Tag("create-account")
    public void testCreateAccount_Success()
            throws AccountCreatedException {

        String testAccountName = "testName";
        String testAccountPin = "1234";

        AccountCreateDTO accountCreateDTO = new AccountCreateDTO();
        accountCreateDTO.setAccountName(testAccountName);
        accountCreateDTO.setPin(testAccountPin);

        Account account = new Account();
        account.setAccountName(accountCreateDTO.getAccountName());
        account.setPin(accountCreateDTO.getPin());

        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();
        accountInfoDTO.setAccountName(testAccountName);

        when(modelMapper.map(accountCreateDTO, Account.class)).thenReturn(account);
        when(modelMapper.map(account, AccountInfoDTO.class)).thenReturn(accountInfoDTO);
        when(passwordEncoder.encode(any(String.class))).thenReturn("hashedPassword");
        when(accountRepository.save(account)).thenReturn(account);

        AccountInfoDTO result = accountService.createAccount(accountCreateDTO);

        verify(modelMapper, times(1)).map(accountCreateDTO, Account.class);
        verify(passwordEncoder, times(1)).encode(any(String.class));
        verify(accountRepository, times(1)).save(any(Account.class));

        assertNotNull(result);
        assertEquals(testAccountName, result.getAccountName());
        assertEquals("hashedPassword", account.getPin());
        assertNotEquals(testAccountPin, account.getPin());

    }

    /**
     * Проверяет выброс исключения при создании счета с пустым значением вместо названия счета.
     * @throws AccountCreatedException Выбрасывает при возникновении ошибки на этапе создания счета.
     */
    @Test
    @Tag("create-account")
    public void testCreateAccount_EmptyName_ThrowAccountCreatedException()
            throws AccountCreatedException {

        AccountCreateDTO accountWithEmptyName = new AccountCreateDTO();
        accountWithEmptyName.setAccountName("");
        accountWithEmptyName.setPin("1234");

        Throwable result = assertThrows(AccountCreatedException.class, () -> accountService.createAccount(accountWithEmptyName));

        assertEquals(AccountCreatedException.class, result.getClass());
        assertEquals("Ошибка при создании банковского счета. Название счета не может быть пустым", result.getMessage());

    }

    /**
     * Проверяет выброс исключения при создании счета со значением null вместо названия счета.
     * @throws AccountCreatedException Выбрасывает при возникновении ошибки на этапе создания счета.
     */
    @Test
    @Tag("create-account")
    public void testCreateAccount_NullName_ThrowAccountCreatedException()
            throws AccountCreatedException {

        AccountCreateDTO accountWithNullName = new AccountCreateDTO();
        accountWithNullName.setAccountName(null);
        accountWithNullName.setPin("1234");

        Throwable result = assertThrows(AccountCreatedException.class, () -> accountService.createAccount(accountWithNullName));

        assertEquals(AccountCreatedException.class, result.getClass());
        assertEquals("Ошибка при создании банковского счета. Название счета не может быть пустым", result.getMessage());

    }

    /**
     * Проверяет выброс исключения при дублирующем названии создаваемого счета.
     * @throws AccountCreatedException Выбрасывает при возникновении ошибки на этапе создания счета.
     */
    @Test
    @Tag("create-account")
    public void testCreateAccount_DuplicateName_ThrowAccountCreatedException()
            throws AccountCreatedException {

        AccountCreateDTO accountWithDuplicateName = new AccountCreateDTO();
        accountWithDuplicateName.setAccountName("duplicate_name");
        accountWithDuplicateName.setPin("1234");

        when(accountRepository.existsAccountByAccountName(any(String.class))).thenReturn(true);

        Throwable result = assertThrows(AccountCreatedException.class, () -> accountService.createAccount(accountWithDuplicateName));

        assertEquals(AccountCreatedException.class, result.getClass());
        assertEquals("Ошибка при создании банковского счета. Название счета: " + accountWithDuplicateName.getAccountName() + " уже используется", result.getMessage());

    }

    /**
     * Проверяет обновление названия банковского счета с валидными данными.
     * @throws AccountAccessException Выбрасывает при возникновении ошибки на этапе доступа к счету.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     * @throws AccountWithNameAlreadyExistsException Выбрасывает при возникновении ошибки дублирования названия счета.
     */
    @Test
    @Tag("update-account-name")
    public void testUpdateAccountName_Success()
            throws AccountAccessException, AccountNotFoundException, AccountWithNameAlreadyExistsException {

        Long accountId = 1L;
        String saveAccountName = "test_name";
        String saveAccountPin = "valid_pin";
        String newAccountName = "test_new_name";

        AccountUpdateNameDTO accountUpdateNameDTO = new AccountUpdateNameDTO();
        accountUpdateNameDTO.setUpdatedAccountName(newAccountName);
        accountUpdateNameDTO.setPin(saveAccountPin);

        Account saveAccount = new Account();
        saveAccount.setAccountName(saveAccountName);
        saveAccount.setPin(saveAccountPin);

        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(saveAccount));
        when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(true);
        when(accountRepository.save(any(Account.class))).thenReturn(saveAccount);
        when(modelMapper.map(saveAccount, AccountInfoDTO.class)).thenReturn(accountInfoDTO);

        AccountInfoDTO result = accountService.updateAccountName(accountId, accountUpdateNameDTO);

        verify(accountRepository, times(1)).findById(accountId);
        verify(passwordEncoder, times(1)).matches(any(String.class), any(String.class));
        verify(accountRepository, times(1)).save(saveAccount);
        verify(modelMapper, times(1)).map(saveAccount, AccountInfoDTO.class);

        assertNotNull(result);
        assertEquals(newAccountName, saveAccount.getAccountName());

    }

    /**
     * Проверяет выброс исключения при неправильном пин коде.
     * @throws AccountAccessException Выбрасывает при возникновении ошибки на этапе доступа к счету.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     * @throws AccountWithNameAlreadyExistsException Выбрасывает при возникновении ошибки дублирования названия счета.
     */
    @Test
    @Tag("update-account-name")
    public void testUpdateAccountName_InvalidPin_ThrowAccountAccessException()
            throws AccountAccessException, AccountNotFoundException, AccountWithNameAlreadyExistsException {

        Long accountId = 1L;
        String saveAccountPin = "valid_pin";

        AccountUpdateNameDTO accountUpdateNameDTO = new AccountUpdateNameDTO();
        accountUpdateNameDTO.setPin("invalid_pin");

        Account saveAccount = new Account();
        saveAccount.setPin(saveAccountPin);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(saveAccount));
        when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(false);

        Throwable result = assertThrows(AccountAccessException.class, () -> accountService.updateAccountName(accountId, accountUpdateNameDTO));

        verify(accountRepository, times(1)).findById(accountId);
        verify(passwordEncoder, times(1)).matches(any(String.class), any(String.class));
        verify(accountRepository, never()).save(saveAccount);
        verify(modelMapper, never()).map(saveAccount, AccountInfoDTO.class);

        assertNotNull(result);
        assertEquals(AccountAccessException.class, result.getClass());
        assertEquals("Введен неверный пин код", result.getMessage());

    }

    /**
     * Проверяет выброс исключения если счет не удалось получить по его идентификатору.
     * @throws AccountAccessException Выбрасывает при возникновении ошибки на этапе доступа к счету.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     * @throws AccountWithNameAlreadyExistsException Выбрасывает при возникновении ошибки дублирования названия счета.
     */
    @Test
    @Tag("update-account-name")
    public void testUpdateAccountName_AccountNotFound_ThrowAccountNotFoundException()
            throws AccountAccessException, AccountNotFoundException, AccountWithNameAlreadyExistsException {

        Long accountId = 1L;

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        Throwable result = assertThrows(AccountNotFoundException.class, () -> accountService.updateAccountName(accountId, any(AccountUpdateNameDTO.class)));

        verify(accountRepository, times(1)).findById(anyLong());
        verify(passwordEncoder, never()).matches(any(String.class), any(String.class));
        verify(accountRepository, never()).save(any(Account.class));

        assertNotNull(result);
        assertEquals(AccountNotFoundException.class, result.getClass());
        assertEquals("Счет с идентификатором: " + accountId + " не найден", result.getMessage());

    }

    /**
     * Проверяет выброс исключения при дублирующем значении названия обновляемого счета.
     * @throws AccountAccessException Выбрасывает при возникновении ошибки на этапе доступа к счету.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     * @throws AccountWithNameAlreadyExistsException Выбрасывает при возникновении ошибки дублирования названия счета.
     */
    @Test
    @Tag("update-account-name")
    public void testUpdateAccountName_DuplicateAccountName_ThrowAccountWithNameAlreadyExistsException()
            throws AccountAccessException, AccountNotFoundException, AccountWithNameAlreadyExistsException {

        Long accountId = 1L;
        String saveAccountName = "test_name";
        String saveAccountPin = "valid_pin";
        String newAccountName = "test_name";

        AccountUpdateNameDTO accountUpdateNameDTO = new AccountUpdateNameDTO();
        accountUpdateNameDTO.setUpdatedAccountName(newAccountName);
        accountUpdateNameDTO.setPin(saveAccountPin);

        Account saveAccount = new Account();
        saveAccount.setAccountName(saveAccountName);
        saveAccount.setPin(saveAccountPin);

        when(accountRepository.existsAccountByAccountName(any(String.class))).thenReturn(true);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(saveAccount));
        when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(true);
        when(accountRepository.save(any(Account.class))).thenReturn(saveAccount);

        Throwable result = assertThrows(AccountWithNameAlreadyExistsException.class, () -> accountService.updateAccountName(accountId, accountUpdateNameDTO));

        verify(accountRepository, times(1)).findById(accountId);
        verify(passwordEncoder, times(1)).matches(any(String.class), any(String.class));
        verify(accountRepository, never()).save(saveAccount);

        assertNotNull(result);
        assertEquals(AccountWithNameAlreadyExistsException.class, result.getClass());
        assertEquals("Ошибка при обновлении банковского счета. Название счета: " + newAccountName + " уже используется", result.getMessage());

    }

    /**
     * Проверяет выброс исключения при значении null названия обновляемого счета.
     * @throws NullPointerException Выбрасывает при возникновении ошибки на этапе проверки названия счета на null.
     * @throws AccountAccessException Выбрасывает при возникновении ошибки на этапе доступа к счету.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     * @throws AccountWithNameAlreadyExistsException Выбрасывает при возникновении ошибки дублирования названия счета.
     */
    @Test
    @Tag("update-account-name")
    public void testUpdateAccountName_NullAccountName_ThrowNullPointerException()
            throws NullPointerException, AccountAccessException, AccountNotFoundException, AccountWithNameAlreadyExistsException {

        Long accountId = 1L;
        String saveAccountName = "test_name";
        String saveAccountPin = "valid_pin";

        AccountUpdateNameDTO accountUpdateNameDTO = new AccountUpdateNameDTO();
        accountUpdateNameDTO.setUpdatedAccountName(null);
        accountUpdateNameDTO.setPin(saveAccountPin);

        Account saveAccount = new Account();
        saveAccount.setAccountName(saveAccountName);
        saveAccount.setPin(saveAccountPin);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(saveAccount));
        when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(true);

        Throwable result = assertThrows(NullPointerException.class, () -> accountService.updateAccountName(accountId, accountUpdateNameDTO));

        verify(accountRepository, times(1)).findById(accountId);
        verify(passwordEncoder, times(1)).matches(any(String.class), any(String.class));
        verify(accountRepository, never()).save(saveAccount);

        assertNotNull(result);
        assertEquals(NullPointerException.class, result.getClass());
        assertEquals("Название счета не должно быть пустым", result.getMessage());

    }

    /**
     * Проверяет выброс исключения при пустом значении названия обновляемого счета.
     * @throws NullPointerException Выбрасывает при возникновении ошибки на этапе проверки названия счета на пустое значение.
     * @throws AccountAccessException Выбрасывает при возникновении ошибки на этапе доступа к счету.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     * @throws AccountWithNameAlreadyExistsException Выбрасывает при возникновении ошибки дублирования названия счета.
     */
    @Test
    @Tag("update-account-name")
    public void testUpdateAccountName_EmptyAccountName_ThrowNullPointerException()
            throws NullPointerException, AccountAccessException, AccountNotFoundException, AccountWithNameAlreadyExistsException {

        Long accountId = 1L;
        String saveAccountName = "test_name";
        String saveAccountPin = "valid_pin";

        AccountUpdateNameDTO accountUpdateNameDTO = new AccountUpdateNameDTO();
        accountUpdateNameDTO.setUpdatedAccountName("");
        accountUpdateNameDTO.setPin(saveAccountPin);

        Account saveAccount = new Account();
        saveAccount.setAccountName(saveAccountName);
        saveAccount.setPin(saveAccountPin);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(saveAccount));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        Throwable result = assertThrows(NullPointerException.class, () -> accountService.updateAccountName(accountId, accountUpdateNameDTO));

        verify(accountRepository, times(1)).findById(accountId);
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(accountRepository, never()).save(saveAccount);

        assertNotNull(result);
        assertEquals(NullPointerException.class, result.getClass());
        assertEquals("Название счета не должно быть пустым", result.getMessage());

    }

    /**
     * Проверяет получение счета по его идентификатору.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     */
    @Test
    @Tag("get-info-account")
    public void testGetInfoAccount_Success()
            throws AccountNotFoundException {

        Account account = new Account();
        AccountDTO accountDTO = new AccountDTO();

        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        when(modelMapper.map(account, AccountDTO.class)).thenReturn(accountDTO);

        AccountDTO result = accountService.getInfoAccount(anyLong());

        verify(accountRepository, times(1)).findById(anyLong());
        verify(modelMapper, times(1)).map(account, AccountDTO.class);

        assertNotNull(result);

    }

    /**
     * Проверяет выброс исключения если аккаунт не удалось получить по его идентификатору.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     */
    @Test
    @Tag("get-info-account")
    public void testGetInfoAccount_AccountNotFound_ThrowAccountNotFoundException()
            throws AccountNotFoundException {

        Long accountId =1L;
        Account account = new Account();

        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable result = assertThrows(AccountNotFoundException.class, () -> accountService.getInfoAccount(accountId));

        verify(accountRepository, times(1)).findById(anyLong());
        verify(modelMapper, never()).map(account, AccountDTO.class);

        assertNotNull(result);
        assertEquals(AccountNotFoundException.class, result.getClass());
        assertEquals("Счет с идентификатором: " + accountId + " не найден", result.getMessage());

    }

    /**
     * Проверяет получение списка всех счетов.
     */
    @Test
    @Tag("get-all-accounts")
    public void testGetAllAccounts_Success() {

        Account account = new Account();
        List<Account> accountList = new ArrayList<>(List.of(new Account(), new Account(), new Account()));
        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();

        when(accountRepository.findAll()).thenReturn(accountList);
        when(modelMapper.map(account, AccountInfoDTO.class)).thenReturn(accountInfoDTO);

        List<AccountInfoDTO> result = accountService.getAllAccounts();

        verify(modelMapper, times(accountList.size())).map(account, AccountInfoDTO.class);

        assertNotNull(result);
        assertEquals(accountList.size(), result.size());

    }

    /**
     * Проверяет удаление счета по его идентификатору.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     */
    @Test
    @Tag("delete-account")
    public void testDeleteAccount_Success()
            throws AccountNotFoundException {

        when(accountRepository.existsById(anyLong())).thenReturn(true);

        accountService.deleteAccount(anyLong());

        verify(accountRepository, times(1)).existsById(anyLong());
        verify(accountRepository, times(1)).deleteById(anyLong());

    }

    /**
     * Проверяет выброс исключения при удалении счета если счет не найден по идентификатору.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     */
    @Test
    @Tag("delete-account")
    public void testDeleteAccount_AccountNotFound_ThrowAccountNotFoundException()
            throws AccountNotFoundException {

        Long accountId = 1L;

        when(accountRepository.existsById(anyLong())).thenReturn(false);

        Throwable result = assertThrows(AccountNotFoundException.class, () -> accountService.deleteAccount(accountId));

        verify(accountRepository, times(1)).existsById(anyLong());
        verify(accountRepository, never()).deleteById(anyLong());

        assertNotNull(result);
        assertEquals(AccountNotFoundException.class, result.getClass());
        assertEquals("Счет с идентификатором: " + accountId + " не найден", result.getMessage());

    }

    /**
     * Проверяет деактивацию счета по его идентификатору.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     */
    @Test
    @Tag("soft-delete-account")
    public void testSoftDeleteAccount_Success()
            throws AccountNotFoundException {

        Account account = new Account();
        account.setEntityStatus(EntityStatus.ACTIVE);

        when(accountRepository.existsById(anyLong())).thenReturn(true);
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));

        accountService.softDeleteAccount(anyLong());

        verify(accountRepository, times(1)).existsById(anyLong());
        verify(accountRepository, times(1)).findById(anyLong());
        verify(accountRepository, never()).deleteById(anyLong());

        assertEquals(EntityStatus.DELETED, account.getEntityStatus());

    }

    /**
     * Проверяет выброс исключения при деактивации счета если счет не найден по идентификатору.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     */
    @Test
    @Tag("soft-delete-account")
    public void testSoftDeleteAccount_AccountNotFound_ThrowAccountNotFoundException()
            throws AccountNotFoundException {

        Long accountId = 1L;

        when(accountRepository.existsById(anyLong())).thenReturn(false);

        Throwable result = assertThrows(AccountNotFoundException.class, () -> accountService.softDeleteAccount(accountId));

        verify(accountRepository, times(1)).existsById(anyLong());
        verify(accountRepository, never()).save(any());

        assertNotNull(result);
        assertEquals(AccountNotFoundException.class, result.getClass());
        assertEquals("Счет с идентификатором: " + accountId + " не найден", result.getMessage());

    }

    /**
     * Проверяет корректность пополнения банковского счета.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     */
    @Test
    @Tag("deposit")
    public void testDeposit_Success()
            throws AccountNotFoundException {

        Double startBalance = 0.;
        Double amount = 50.;
        Double currentBalance = startBalance + amount;

        Account account = new Account();
        account.setAccountBalance(startBalance);

        AccountTransactionDTO accountTransactionDTO = new AccountTransactionDTO();
        accountTransactionDTO.setTransferAmount(amount);

        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();

        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenReturn(account);
        when(modelMapper.map(account, AccountInfoDTO.class)).thenReturn(accountInfoDTO);

        AccountInfoDTO result = accountService.deposit(anyLong(), accountTransactionDTO);

        verify(accountRepository, times(1)).findById(anyLong());
        verify(accountRepository, times(1)).save(any());

        assertNotNull(result);
        assertEquals(currentBalance, account.getAccountBalance());

    }

    /**
     * Проверяет выброс исключения при пополнении счета на этапе поиска счета.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     * @throws AccountWithdrawException Выбрасывает при возникновении ошибки на этапе списание средств со счета.
     */
    @Test
    @Tag("deposit")
    public void testDeposit_AccountNotFound_ThrowAccountNotFoundException()
            throws AccountWithdrawException, AccountNotFoundException {

        Long accountId = 1L;

        AccountTransactionDTO accountTransactionDTO = new AccountTransactionDTO();

        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(accountRepository.save(any())).thenReturn(any());

        Throwable result = assertThrows(AccountNotFoundException.class, () -> accountService.deposit(accountId, accountTransactionDTO));

        verify(accountRepository, times(1)).findById(anyLong());
        verify(accountRepository, never()).save(any());

        assertNotNull(result);
        assertEquals(AccountNotFoundException.class, result.getClass());
        assertEquals("Счет с идентификатором: " + accountId + " не найден", result.getMessage());

    }

    /**
     * Проверяет выброс исключения при пополнении счета на некорректную сумму.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     * @throws AccountWithdrawException Выбрасывает при возникновении ошибки на этапе списание средств со счета.
     */
    @Test
    @Tag("deposit")
    public void testDeposit_IncorrectAmount_ThrowAccountWithdrawException()
            throws AccountWithdrawException, AccountNotFoundException {

        Double startBalance = 0.;
        Double amount = -50.;
        Double currentBalance = startBalance + amount;

        Account account = new Account();
        account.setAccountBalance(startBalance);

        AccountTransactionDTO accountTransactionDTO = new AccountTransactionDTO();
        accountTransactionDTO.setTransferAmount(amount);

        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenReturn(account);

        Throwable result = assertThrows(AccountWithdrawException.class, () -> accountService.deposit(anyLong(), accountTransactionDTO));

        verify(accountRepository, times(1)).findById(anyLong());
        verify(accountRepository, never()).save(any());

        assertNotNull(result);
        assertNotEquals(currentBalance, account.getAccountBalance());
        assertEquals(AccountWithdrawException.class, result.getClass());
        assertEquals("Сумма пополнения должна быть положительной и не может быть пустой", result.getMessage());

    }

    /**
     * Проверяет корректность списания средств с банковского счета.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     * @throws AccountAccessException Выбрасывает при возникновении ошибки на этапе доступа к счету.
     * @throws AccountWithdrawException Выбрасывает при возникновении ошибки на этапе списание средств со счета.
     */
    @Test
    @Tag("withdraw")
    public void testWithdraw_Success()
            throws AccountNotFoundException, AccountAccessException, AccountWithdrawException {

        Double startBalance = 100.;
        Double amount = 30.;
        Double currentBalance = startBalance - amount;

        Account account = new Account();
        account.setAccountBalance(startBalance);
        account.setPin("hashed_valid_pin");

        AccountTransactionDTO accountTransactionDTO = new AccountTransactionDTO();
        accountTransactionDTO.setTransferAmount(amount);
        accountTransactionDTO.setPin("valid_pin");

        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);
        when(modelMapper.map(account, AccountInfoDTO.class)).thenReturn(accountInfoDTO);

        AccountInfoDTO result = accountService.withdraw(anyLong(), accountTransactionDTO);

        verify(accountRepository, times(1)).findById(anyLong());
        verify(accountRepository, times(1)).save(account);
        verify(modelMapper, times(1)).map(account, AccountInfoDTO.class);

        assertNotNull(result);
        assertEquals(currentBalance, account.getAccountBalance());

    }

    /**
     * Проверяет выброс исключения при списании с банковского счета на этапе его поиска.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     * @throws AccountAccessException Выбрасывает при возникновении ошибки на этапе доступа к счету.
     * @throws AccountWithdrawException Выбрасывает при возникновении ошибки на этапе списание средств со счета.
     */
    @Test
    @Tag("withdraw")
    public void testWithdraw_AccountNotFound_ThrowAccountNotFoundException()
            throws AccountNotFoundException, AccountAccessException, AccountWithdrawException {

        Long accountId = 1L;

        AccountTransactionDTO accountTransactionDTO = new AccountTransactionDTO();

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        Throwable result = assertThrows(AccountNotFoundException.class, () -> accountService.withdraw(accountId, accountTransactionDTO));

        verify(accountRepository, times(1)).findById(anyLong());
        verify(accountRepository, never()).save(any());

        assertNotNull(result);
        assertEquals(AccountNotFoundException.class, result.getClass());
        assertEquals("Счет с идентификатором: " + accountId + " не найден", result.getMessage());

    }

    /**
     * Проверяет выброс исключения при списании с банковского счета на этапе получения доступа к счету.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     * @throws AccountAccessException Выбрасывает при возникновении ошибки на этапе доступа к счету.
     * @throws AccountWithdrawException Выбрасывает при возникновении ошибки на этапе списание средств со счета.
     */
    @Test
    @Tag("withdraw")
    public void testWithdraw_AccountNotAccess_ThrowAccountAccessException()
            throws AccountNotFoundException, AccountAccessException, AccountWithdrawException {

        Account account = new Account();
        account.setPin("hashed_valid_pin");

        AccountTransactionDTO accountTransactionDTO = new AccountTransactionDTO();
        accountTransactionDTO.setPin("invalid_pin");

        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);
        when(modelMapper.map(account, AccountInfoDTO.class)).thenReturn(accountInfoDTO);

        Throwable result = assertThrows(AccountAccessException.class, () -> accountService.withdraw(anyLong(), accountTransactionDTO));

        verify(accountRepository, times(1)).findById(anyLong());
        verify(accountRepository, never()).save(account);
        verify(modelMapper, never()).map(account, AccountInfoDTO.class);

        assertNotNull(result);
        assertEquals(AccountAccessException.class, result.getClass());
        assertEquals("Введен неверный пин код", result.getMessage());

    }

    /**
     * Проверяет выброс исключения при списании с банковского счета при некорректной сумме списания.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     * @throws AccountAccessException Выбрасывает при возникновении ошибки на этапе доступа к счету.
     * @throws AccountWithdrawException Выбрасывает при возникновении ошибки на этапе списание средств со счета.
     */
    @Test
    @Tag("withdraw")
    public void testWithdraw_AmountNegativeOrZero_ThrowAccountWithdrawException()
            throws AccountNotFoundException, AccountAccessException, AccountWithdrawException {

        Double startBalance = 100.;
        Double amount = -10.;

        Account account = new Account();
        account.setAccountBalance(startBalance);
        account.setPin("hashed_valid_pin");

        AccountTransactionDTO accountTransactionDTO = new AccountTransactionDTO();
        accountTransactionDTO.setTransferAmount(amount);
        accountTransactionDTO.setPin("valid_pin");

        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);
        when(modelMapper.map(account, AccountInfoDTO.class)).thenReturn(accountInfoDTO);

        Throwable result = assertThrows(AccountWithdrawException.class, () -> accountService.withdraw(anyLong(), accountTransactionDTO));

        verify(accountRepository, times(1)).findById(anyLong());
        verify(accountRepository, never()).save(account);
        verify(modelMapper, never()).map(account, AccountInfoDTO.class);

        assertNotNull(result);
        assertEquals(AccountWithdrawException.class, result.getClass());
        assertEquals("Сумма списания должна быть положительной и не может быть пустой", result.getMessage());

    }

    /**
     * Проверяет выброс исключения при списании с банковского счета при сумме списания превышающей текущий баланс счета.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     * @throws AccountAccessException Выбрасывает при возникновении ошибки на этапе доступа к счету.
     * @throws AccountWithdrawException Выбрасывает при возникновении ошибки на этапе списание средств со счета.
     */
    @Test
    @Tag("withdraw")
    public void testWithdraw_AmountExceedingBalance_ThrowAccountWithdrawException()
            throws AccountNotFoundException, AccountAccessException, AccountWithdrawException {

        Double startBalance = 100.;
        Double amount = 101.;

        Account account = new Account();
        account.setAccountBalance(startBalance);
        account.setPin("hashed_valid_pin");

        AccountTransactionDTO accountTransactionDTO = new AccountTransactionDTO();
        accountTransactionDTO.setTransferAmount(amount);
        accountTransactionDTO.setPin("valid_pin");

        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);
        when(modelMapper.map(account, AccountInfoDTO.class)).thenReturn(accountInfoDTO);

        Throwable result = assertThrows(AccountWithdrawException.class, () -> accountService.withdraw(anyLong(), accountTransactionDTO));

        verify(accountRepository, times(1)).findById(anyLong());
        verify(accountRepository, never()).save(account);
        verify(modelMapper, never()).map(account, AccountInfoDTO.class);

        assertNotNull(result);
        assertEquals(AccountWithdrawException.class, result.getClass());
        assertEquals("Сумма списания должна быть не больше текущего баланса", result.getMessage());

    }

    /**
     * Проверяет корректность списания средств при переводе с одного банковского счета на другой.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     * @throws AccountAccessException Выбрасывает при возникновении ошибки на этапе доступа к счету.
     * @throws AccountWithdrawException Выбрасывает при возникновении ошибки на этапе списание средств со счета.
     */
    @Test
    @Tag("transfer")
    public void testTransfer_Access()
            throws AccountNotFoundException, AccountAccessException, AccountWithdrawException {

        Double startBalanceAccount = 500.;
        Double startBalanceSourceAccount = 100.;
        Double transferAmount = 300.;
        Double newBalanceAccount = startBalanceAccount - transferAmount;
        Double newBalanceSourceAccount = startBalanceSourceAccount + transferAmount;

        Account account = new Account();
        account.setPin("hashed_valid_pin");
        account.setAccountBalance(startBalanceAccount);

        Account sourceAccount = new Account();
        sourceAccount.setAccountBalance(startBalanceSourceAccount);

        AccountTransactionDTO accountTransactionDTO = new AccountTransactionDTO();
        accountTransactionDTO.setSourceAccountName("source_account");
        accountTransactionDTO.setPin("valid_pin");
        accountTransactionDTO.setTransferAmount(transferAmount);

        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();

        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(accountRepository.save(account)).thenReturn(account);
        when(accountRepository.findAccountByAccountName(anyString())).thenReturn(Optional.of(sourceAccount));
        when(modelMapper.map(account, AccountInfoDTO.class)).thenReturn(accountInfoDTO);

        AccountInfoDTO result = accountService.transfer(anyLong(), accountTransactionDTO);

        verify(accountRepository, times(1)).findById(anyLong());
        verify(accountRepository, times(2)).save(account);
        verify(accountRepository, times(1)).findAccountByAccountName(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(modelMapper, times(1)).map(account, AccountInfoDTO.class);

        assertNotNull(result);
        assertEquals(newBalanceAccount, account.getAccountBalance());
        assertEquals(newBalanceSourceAccount, sourceAccount.getAccountBalance());

    }

    /**
     * Проверяет выброс исключения при переводе средств между счетами на этапе поиска счета на который необходимо совершить перевод.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     * @throws AccountAccessException Выбрасывает при возникновении ошибки на этапе доступа к счету.
     * @throws AccountWithdrawException Выбрасывает при возникновении ошибки на этапе списание средств со счета.
     */
    @Test
    @Tag("transfer")
    public void testTransfer_AccountNotFound_ThrowAccountNotFoundException()
            throws AccountNotFoundException, AccountAccessException, AccountWithdrawException {


        Account account = new Account();
        account.setPin("hashed_valid_pin");

        AccountTransactionDTO accountTransactionDTO = new AccountTransactionDTO();
        accountTransactionDTO.setSourceAccountName("source_account");
        accountTransactionDTO.setPin("valid_pin");

        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(accountRepository.save(account)).thenReturn(account);
        when(accountRepository.findAccountByAccountName(anyString())).thenReturn(Optional.empty());

        Throwable result = assertThrows(AccountNotFoundException.class, () -> accountService.transfer(anyLong(), accountTransactionDTO));

        verify(accountRepository, times(1)).findById(anyLong());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(accountRepository, times(1)).findAccountByAccountName(anyString());
        verify(accountRepository, never()).save(account);

        assertNotNull(result);
        assertEquals(AccountNotFoundException.class, result.getClass());
        assertEquals("Счет с названием: " + accountTransactionDTO.getSourceAccountName() + " не найден", result.getMessage());

    }

    /**
     * Проверяет выброс исключения при переводе средств между счетами на сумму списания превышающую текущий баланс счета.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     * @throws AccountAccessException Выбрасывает при возникновении ошибки на этапе доступа к счету.
     * @throws AccountWithdrawException Выбрасывает при возникновении ошибки на этапе списание средств со счета.
     */
    @Test
    @Tag("transfer")
    public void testTransfer_AmountExceedingBalance_ThrowAccountWithdrawException()
            throws AccountNotFoundException, AccountAccessException, AccountWithdrawException {

        Double startBalanceAccount = 500.;
        Double startBalanceSourceAccount = 100.;
        Double transferAmount = 800.;

        Account account = new Account();
        account.setPin("hashed_valid_pin");
        account.setAccountBalance(startBalanceAccount);

        Account sourceAccount = new Account();
        sourceAccount.setAccountBalance(startBalanceSourceAccount);

        AccountTransactionDTO accountTransactionDTO = new AccountTransactionDTO();
        accountTransactionDTO.setSourceAccountName("source_account");
        accountTransactionDTO.setPin("valid_pin");
        accountTransactionDTO.setTransferAmount(transferAmount);

        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();

        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(accountRepository.save(account)).thenReturn(account);
        when(accountRepository.findAccountByAccountName(anyString())).thenReturn(Optional.of(sourceAccount));
        when(modelMapper.map(account, AccountInfoDTO.class)).thenReturn(accountInfoDTO);

        Throwable result = assertThrows(AccountWithdrawException.class, () -> accountService.transfer(anyLong(), accountTransactionDTO));

        verify(accountRepository, times(1)).findById(anyLong());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(accountRepository, times(1)).findAccountByAccountName(anyString());
        verify(accountRepository, never()).save(account);

        assertNotNull(result);
        assertEquals(AccountWithdrawException.class, result.getClass());
        assertEquals("Сумма перевода должна быть не больше текущего баланса", result.getMessage());

    }

    /**
     * Проверяет выброс исключения при переводе средств между счетами на сумму списания превышающую текущий баланс счета.
     * @throws AccountNotFoundException Выбрасывает при возникновении ошибки на этапе поиска счета.
     * @throws AccountAccessException Выбрасывает при возникновении ошибки на этапе доступа к счету.
     * @throws AccountWithdrawException Выбрасывает при возникновении ошибки на этапе списание средств со счета.
     */
    @Test
    @Tag("transfer")
    public void testTransfer_AccountNotAccess_ThrowAccountAccessException()
            throws AccountNotFoundException, AccountAccessException, AccountWithdrawException {

        Account account = new Account();
        account.setPin("hashed_valid_pin");

        AccountTransactionDTO accountTransactionDTO = new AccountTransactionDTO();
        accountTransactionDTO.setPin("valid_pin");

        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        Throwable result = assertThrows(AccountAccessException.class, () -> accountService.transfer(anyLong(), accountTransactionDTO));

        verify(accountRepository, times(1)).findById(anyLong());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());

        assertNotNull(result);
        assertEquals(AccountAccessException.class, result.getClass());
        assertEquals("Введен неверный пин код", result.getMessage());

    }
}