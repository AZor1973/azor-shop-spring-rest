package ru.azor.api.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ошибки 4XX")
public class ClientException extends RuntimeException {
    public ClientException(String message) {
        super(message);
    }
}
