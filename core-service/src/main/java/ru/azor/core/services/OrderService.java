package ru.azor.core.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.azor.api.carts.CartDto;
import ru.azor.api.core.OrderDetailsDto;
import ru.azor.api.enums.OrderStatus;
import ru.azor.api.exceptions.ResourceNotFoundException;
import ru.azor.core.entities.Order;
import ru.azor.core.entities.OrderItem;
import ru.azor.core.integrations.CartServiceIntegration;
import ru.azor.core.repositories.OrdersRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrdersRepository ordersRepository;
    private final CartServiceIntegration cartServiceIntegration;
    private final ProductsService productsService;

    @Transactional
    public void createOrder(String username, OrderDetailsDto orderDetailsDto) {
        CartDto currentCart = cartServiceIntegration.getUserCart(username);
        Order order = new Order();
        order.setAddress(orderDetailsDto.getAddress());
        order.setPhone(orderDetailsDto.getPhone());
        order.setUsername(username);
        order.setTotalPrice(currentCart.getTotalPrice());
        order.setOrderStatus(OrderStatus.CREATED);
        List<OrderItem> items = currentCart.getItems().stream()
                .map(o -> {
                    OrderItem item = new OrderItem();
                    item.setOrder(order);
                    item.setQuantity(o.getQuantity());
                    item.setPricePerProduct(o.getPricePerProduct());
                    item.setPrice(o.getPrice());
                    item.setProduct(productsService.findById(o.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product not found")));
                    return item;
                }).collect(Collectors.toList());
        order.setItems(items);
        ordersRepository.save(order);
        cartServiceIntegration.clearUserCart(username);
    }

    public boolean isOrderStatusPresent(OrderStatus orderStatus, Long orderId){
        return ordersRepository.isOrderStatusPresent(orderStatus, orderId) > 0;
    }

    public List<Order> findOrdersByUsername(String username) {
        return ordersRepository.findAllByUsername(username);
    }

    public Optional<Order> findById(Long id) {
        return ordersRepository.findById(id);
    }

    public void deleteOrder(Long orderId) {
        try{
            ordersRepository.deleteById(orderId);
        }catch (ResourceNotFoundException ex){
           throw new ResourceNotFoundException("Ошибка удаления заказа. Заказ " + orderId + "не существует");
        }
    }

    public void changeOrderStatus(OrderStatus orderStatus, Long orderId) {
        try{
            ordersRepository.changeOrderStatus(orderStatus, orderId);
        }catch (ResourceNotFoundException ex){
            throw new ResourceNotFoundException("Ошибка удаления заказа. Заказ " + orderId + "не существует");
        }
    }
}
