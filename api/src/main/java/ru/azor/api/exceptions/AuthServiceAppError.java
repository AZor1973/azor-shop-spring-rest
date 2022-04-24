package ru.azor.api.exceptions;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ошибки сервиса аутентификации")
public class AuthServiceAppError extends AppError{

    @Schema(description = "Коды ошибок сервиса аутентификации", example = "AUTH_SERVICE_INCORRECT_USERNAME_OR_PASSWORD")
    public enum AuthServiceErrors{
        AUTH_SERVICE_INCORRECT_USERNAME_OR_PASSWORD
    }

    public AuthServiceAppError(Enum<?> code, String message) {
        super(code, message);
    }
}
