package ru.azor.cart.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.azor.api.exceptions.ClientException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<CartServiceAppError> catchResourceNotFoundException(ClientException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new CartServiceAppError(CartServiceAppError.CartServiceErrors.CART_NOT_FOUND, e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<CoreServiceAppError> catchCoreServiceIntegrationException(CoreServiceIntegrationException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new CoreServiceAppError(CoreServiceAppError.CoreServiceErrors.CORE_SERVICE_IS_BROKEN, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
