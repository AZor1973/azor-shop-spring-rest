package ru.azor.api.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ошибки 5XX")
public class ServerException extends RuntimeException {
    public ServerException(String message) {
        super(message);
    }
}
