package ru.azor.api.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.azor.api.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Long id;
    private String username;
    private List<OrderItemDto> items;
    private BigDecimal totalPrice;
    private String address;
    private String phone;
    private OrderStatus orderStatus;
}
