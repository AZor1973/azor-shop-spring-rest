package ru.azor.api.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ошибки api сервиса корзин")
public class CartServiceIntegrationException extends RuntimeException {
    public CartServiceIntegrationException(String message) {
        super(message);
    }
}
