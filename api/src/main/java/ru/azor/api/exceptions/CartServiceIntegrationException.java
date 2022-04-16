package ru.azor.api.exceptions;

public class CartServiceIntegrationException extends RuntimeException {
    public CartServiceIntegrationException(String message) {
        super(message);
    }
}
