package ru.averkievnv.bankservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.averkievnv.bankservice.models.*;
import ru.averkievnv.bankservice.services.impl.AccountServiceImpl;

import java.util.List;

/**
 * REST-контроллер для взаимодействия со счетами.
 * Содержит API-endpoints для создания, обновления, просмотра, удаления счетов, а также некоторые операции,
 * связанные с пополнением счета, снятием средств и переводом их между счетами.
 * @author mrGreenNV
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountsController {

    /** Сервис для взаимодействия со счетами */
    private final AccountServiceImpl accountService;

    /**
     * API-endpoint для создания нового банковского счета.
     * @param accountCreateDTO Данные для создания счета.
     * @return Информация о созданном счете.
     */
    @PostMapping()
    public ResponseEntity<AccountInfoDTO> openAccount(@Valid  @RequestBody AccountCreateDTO accountCreateDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.createAccount(accountCreateDTO));
    }

    /**
     * API-endpoint для обновления названия банковского счета.
     * @param accountId Идентификатор обновляемого счета.
     * @param accountUpdateNameDTO Данные для обновления счета.
     * @return Информация об обновленном счете.
     */
    @PutMapping("/{accountId}")
    public ResponseEntity<AccountInfoDTO> editAccount(@PathVariable Long accountId, @RequestBody AccountUpdateNameDTO accountUpdateNameDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.updateAccountName(accountId, accountUpdateNameDTO));
    }

    /**
     * API-endpoint для получения информации о счете по его идентификатору.
     * @param accountId Идентификатор счета.
     * @return Информация о запрашиваемом счете.
     */
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDTO> showAccount(@PathVariable Long accountId) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.getInfoAccount(accountId));
    }

    /**
     * API-endpoint для получения списка всех банковских счетов.
     * @return Список счетов содержащий информацию о названии и балансе.
     */
    @GetMapping()
    public ResponseEntity<List<AccountInfoDTO>> showAllAccounts(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize
    ) {
        if (page == null || pageSize == null) {
            return ResponseEntity.status(HttpStatus.OK).body(accountService.getAllAccounts());
        }
        return ResponseEntity.status(HttpStatus.OK).body(accountService.getAllAccounts(PageRequest.of(page, pageSize)));
    }

    /**
     * API-endpoint для удаления банковского счета.
     * @param accountId Идентификатор счета.
     * @return Статус выполнения запроса.
     */
    @DeleteMapping("/{accountId}")
    public ResponseEntity<HttpStatus> closeAccount(@PathVariable Long accountId) {
        accountService.deleteAccount(accountId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * API-endpoint для деактивации банковского счета.
     * @param accountId Идентификатор счета
     * @return Статус выполнения запроса.
     */
    @PostMapping("/{accountId}/soft")
    public ResponseEntity<HttpStatus> deactivateAccount(@PathVariable Long accountId) {
        accountService.softDeleteAccount(accountId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * API-endpoint для пополнения банковского счета.
     * @param accountId Идентификатор пополняемого счета.
     * @param accountTransactionDTO Данные для пополнения счета.
     * @return Информация о пополненном счете.
     */
    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<AccountInfoDTO> deposit(@PathVariable Long accountId, @RequestBody AccountTransactionDTO accountTransactionDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.deposit(accountId, accountTransactionDTO));
    }

    /**
     * API-endpoint для списания средств с банковского счета.
     * @param accountId Идентификатор счета, с которого происходит списание.
     * @param accountTransactionDTO Данные для списания средств со счета.
     * @return Информация о счете.
     */
    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<AccountInfoDTO> withdraw(@PathVariable Long accountId, @RequestBody AccountTransactionDTO accountTransactionDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.withdraw(accountId, accountTransactionDTO));
    }

    /**
     * API-endpoint для перевода средств между банковскими счетами.
     * @param accountId Идентификатор счета, с которого происходит списание.
     * @param accountTransactionDTO Данные для перевода средств между счетами.
     * @return Информация о счете.
     */
    @PostMapping("/{accountId}/transfer")
    public ResponseEntity<AccountInfoDTO> transfer(@PathVariable Long accountId, @RequestBody AccountTransactionDTO accountTransactionDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.transfer(accountId, accountTransactionDTO));
    }

}