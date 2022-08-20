package ru.azor.api.core;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Модель элемента заказа")
public class OrderItemDto {
    @Schema(description = "ID элемента заказа", required = true, example = "1")
    private Long productId;
    @Schema(description = "Название продукта", required = true, example = "Хлеб")
    private String productTitle;
    @Schema(description = "Количество продуктов", required = true, example = "3")
    private int quantity;
    @Schema(description = "Цена за еденицу", required = true, example = "30.00")
    private BigDecimal pricePerProduct;
    @Schema(description = "Стоимость элемента заказа", required = true, example = "120.00")
    private BigDecimal price;
}
