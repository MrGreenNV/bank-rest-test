package ru.averkievnv.bankservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.averkievnv.bankservice.models.Account;

/**
 * Репозиторий для взаимодействия с сущностью Account.
 * Этот интерфейс расширяет JpaRepository, предоставляя стандартные методы для доступа и управления счетами в базе данных.
 * @author mrGreenNV
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

}