package ru.azor.api.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ошибки сервиса корзин")
public class CartServiceAppError extends AppError {
    @Schema(description = "Коды ошибок сервиса корзин", example = "CART_NOT_FOUND")
    public enum CartServiceErrors {
        CART_IS_BROKEN, CART_ID_GENERATOR_DISABLED, CART_NOT_FOUND, STATISTIC_NOT_FOUND
    }

    public CartServiceAppError(Enum<?> code, String message) {
        super(code, message);
    }
}
