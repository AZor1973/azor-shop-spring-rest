package ru.azor.api.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.azor.api.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Модель заказа")
public class OrderDto {
    @Schema(description = "ID заказа", required = true, example = "1")
    private Long id;
    @Schema(description = "Ник получателя", required = true, example = "user")
    private String username;
    @Schema(description = "Ф.И.О. получателя", required = true, example = "Иванов Иван Иванович")
    private String fullName;
    @Schema(description = "Список покупок", required = true, example = "{1, Хлеб, 4, 30.00, 120.00}")
    private Set<OrderItemDto> items;
    @Schema(description = "Стоимость заказа", required = true, example = "1000.00")
    private BigDecimal totalPrice;
    @Schema(description = "Адрес доставки", required = true, example = "603000, г. Москва, ул. Прямая, д.10, кв.1")
    private String address;
    @Schema(description = "Телефон получателя", required = true, example = "222-22-22")
    private String phone;
    @JsonProperty("order_status")
    @Schema(description = "Статус заказа", required = true, example = "CREATED")
    private OrderStatus orderStatus;
}
