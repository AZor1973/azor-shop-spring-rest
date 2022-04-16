package ru.azor.api.exceptions;

public class RecomServiceAppError extends AppError{

    public enum RecomServiceErrors{
        RECOM_NOT_FOUND, RECOM_SERVICE_IS_BROKEN
    }

    public RecomServiceAppError(String code, String message) {
        super(code, message);
    }
}
