package ru.azor.api.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ошибки сервиса рекомендаций")
public class RecomServiceAppError extends AppError{

    @Schema(description = "Коды ошибок сервиса рекомендаций", example = "RECOM_NOT_FOUND")
    public enum RecomServiceErrors{
        RECOM_NOT_FOUND, RECOM_SERVICE_IS_BROKEN
    }

    public RecomServiceAppError(Enum<?> code, String message) {
        super(code, message);
    }
}
