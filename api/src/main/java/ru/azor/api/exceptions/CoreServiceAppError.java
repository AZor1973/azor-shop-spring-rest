package ru.azor.api.exceptions;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ошибки продуктового сервиса")
public class CoreServiceAppError extends AppError{

    @Schema(description = "Коды ошибок продуктового сервиса", example = "PRODUCT_NOT_FOUND")
    public enum CoreServiceErrors{
        PRODUCT_NOT_FOUND, CORE_SERVICE_IS_BROKEN, STATISTIC_NOT_FOUND
    }

    public CoreServiceAppError(Enum<?> code, String message) {
        super(code, message);
    }
}
