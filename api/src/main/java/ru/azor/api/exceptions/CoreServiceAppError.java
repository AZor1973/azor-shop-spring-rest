package ru.azor.api.exceptions;

public class CoreServiceAppError extends AppError{

    public enum CoreServiceErrors{
        PRODUCT_NOT_FOUND, CORE_SERVICE_IS_BROKEN, STATISTIC_NOT_FOUND
    }

    public CoreServiceAppError(String code, String message) {
        super(code, message);
    }
}
