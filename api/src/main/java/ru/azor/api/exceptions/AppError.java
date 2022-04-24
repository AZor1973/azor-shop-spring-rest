package ru.azor.api.exceptions;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ошибки сервисов")
public class AppError {

    @Schema(description = "Коды ошибок сервисов", example = "PRODUCT_NOT_FOUND")
    private Enum<?> code;
    @Schema(description = "Сообщения ошибок сервисов", example = "Продукт не найден")
    private String message;

    public Enum<?> getCode() {
        return code;
    }

    public void setCode(Enum<?> code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AppError() {
    }

    public AppError(Enum<?> code, String message) {
        this.code = code;
        this.message = message;
    }
}
