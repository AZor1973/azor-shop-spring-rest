package ru.azor.api.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ошибки api продуктового сервиса")
public class CoreServiceIntegrationException extends RuntimeException {
    public CoreServiceIntegrationException(String message) {
        super(message);
    }
}
