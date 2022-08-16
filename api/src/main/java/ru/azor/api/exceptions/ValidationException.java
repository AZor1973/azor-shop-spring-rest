package ru.azor.api.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;

import java.util.List;

@Getter
@Schema(description = "Ошибки валидации")
public class ValidationException extends RuntimeException {
    private final List<ObjectError> validationErrors;
    private final HttpStatus httpStatus;

    public ValidationException(String message, List<ObjectError> validationErrors, HttpStatus httpStatus) {
        super(message);
        this.validationErrors = validationErrors;
        this.httpStatus = httpStatus;
    }
}
