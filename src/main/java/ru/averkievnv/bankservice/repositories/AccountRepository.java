package ru.averkievnv.bankservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.averkievnv.bankservice.models.Account;

import java.util.Optional;

/**
 * Репозиторий для взаимодействия с сущностью Account.
 * Этот интерфейс расширяет JpaRepository, предоставляя стандартные методы для доступа и управления счетами в базе данных.
 * @author mrGreenNV
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Проверяет существование в базе данных записи о счете по переданному названию счета.
     * @param accountName Название счета.
     * @return true, если запись найдена, иначе - false.
     */
    boolean existsAccountByAccountName(String accountName);

    /**
     * Выполняет поиск счета по его названию.
     * @param accountName Название счета.
     * @return Optional, содержащий информацию о счете, если счет найден, иначе - пустой.
     */
    Optional<Account> findAccountByAccountName(String accountName);
}