package ru.azor.core.converters;

import org.springframework.stereotype.Component;
import ru.azor.api.core.OrderDto;
import ru.azor.api.core.OrderItemDto;
import ru.azor.core.entities.Order;
import ru.azor.core.entities.OrderItem;

import java.util.stream.Collectors;

@Component
public class OrderConverter {

    public OrderDto entityToDto(Order order) {
        OrderDto out = new OrderDto();
        out.setId(order.getId());
        out.setAddress(order.getAddress());
        out.setPhone(order.getPhone());
        out.setTotalPrice(order.getTotalPrice());
        out.setUsername(order.getUsername());
        out.setItems(order.getItems().stream().map(this::itemToDto).collect(Collectors.toList()));
        out.setOrderStatus(order.getOrderStatus());
        return out;
    }

    private OrderItemDto itemToDto(OrderItem orderItem) {
        return new OrderItemDto(orderItem.getProduct().getId(),
                orderItem.getProduct().getTitle(), orderItem.getQuantity(),
                orderItem.getPricePerProduct(), orderItem.getPrice());
    }
}
