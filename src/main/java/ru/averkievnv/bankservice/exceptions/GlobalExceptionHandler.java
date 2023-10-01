package ru.averkievnv.bankservice.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.averkievnv.bankservice.utils.ErrorResponse;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс отлавливает все исключения возникающие на уровне контроллера, для предоставления ошибки клиенту в виде JSON.
 * @author mrGreenNV
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Позволяет обработать ошибки связанные с валидацией данных.
     * @param ex Ошибки при валидации данных.
     * @param request HTTP запрос.
     * @return ResponseEntity, содержащий информацию об ошибке.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        List<String> errorMessages = fieldErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorResponse response = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Ошибки при валидации данных",
                request.getRequestURI(),
                errorMessages
        );
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * Позволяет обработать ошибки связанные с получением доступа к банковскому счету.
     * @param aEx Ошибка при получении доступа к счету.
     * @param request HTTP запрос.
     * @return ResponseEntity, содержащий информацию об ошибке.
     */
    @ExceptionHandler(AccountAccessException.class)
    public ResponseEntity<ErrorResponse> handleAccountAccessException(AccountAccessException aEx, HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.FORBIDDEN,
                aEx.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * Позволяет обработать ошибки связанные с созданием банковского счета.
     * @param acEx Ошибка при создании банковского счета.
     * @param request HTTP запрос.
     * @return ResponseEntity, содержащий информацию об ошибке.
     */
    @ExceptionHandler(AccountCreatedException.class)
    public ResponseEntity<ErrorResponse> handleAccountCreatedException(AccountCreatedException acEx, HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                acEx.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * Позволяет обработать ошибки связанные с поиском банковского счета.
     * @param anfEx Ошибка при поиске банковского счета.
     * @param request HTTP запрос.
     * @return ResponseEntity, содержащий информацию об ошибке.
     */
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFoundException(AccountNotFoundException anfEx, HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND,
                anfEx.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    /**
     * Позволяет обработать ошибки связанные со списанием средств с банковского счета.
     * @param awEx Ошибка при списании средств с банковского счета.
     * @param request HTTP запрос.
     * @return ResponseEntity, содержащий информацию об ошибке.
     */
    @ExceptionHandler(AccountWithdrawException.class)
    public ResponseEntity<ErrorResponse> handleAccountWithdrawException(AccountWithdrawException awEx, HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                awEx.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * Позволяет обработать ошибки связанные с дублированием названия банковского счета.
     * @param awnEx Ошибка при дублировании названия банковского счета.
     * @param request HTTP запрос.
     * @return ResponseEntity, содержащий информацию об ошибке.
     */
    @ExceptionHandler(AccountWithNameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAccountWithNameAlreadyExistsException(AccountWithNameAlreadyExistsException awnEx, HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                awnEx.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * Позволяет обработать прочие ошибки при взаимодействии с банковским счетом.
     * @param ex Прочие ошибки при взаимодействии с банковским счетом.
     * @param request HTTP запрос.
     * @return ResponseEntity, содержащий информацию об ошибке.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
