package ru.azor.api.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Schema(description = "Ошибки 4XX")
public class ClientException extends RuntimeException {
    private final HttpStatus httpStatus;
    public ClientException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
