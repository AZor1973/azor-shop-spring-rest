package ru.azor.api.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Schema(description = "Ошибки 5XX")
public class ServerException extends RuntimeException {
    private final HttpStatus httpStatus;
    public ServerException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
