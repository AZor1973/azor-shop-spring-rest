package ru.azor.api.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Статус заказа")
public enum OrderStatus {
    CREATED, PAID, NOT_PAID
}
