package ru.azor.core.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import ru.azor.api.carts.CartDto;
import ru.azor.api.core.OrderDetailsDto;
import ru.azor.api.enums.OrderStatus;
import ru.azor.api.exceptions.ClientException;
import ru.azor.api.exceptions.ValidationException;
import ru.azor.core.entities.Order;
import ru.azor.core.entities.OrderItem;
import ru.azor.core.integrations.CartServiceIntegration;
import ru.azor.core.repositories.OrdersRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdersService {
    private final OrdersRepository ordersRepository;
    private final CartServiceIntegration cartServiceIntegration;
    private final ProductsService productsService;
    private final OrderStatisticService orderStatisticService;


    public Order save(String username, OrderDetailsDto orderDetailsDto, BindingResult bindingResult) {
        if (username == null) {
            throw new ClientException("Невалидный параметр: username", HttpStatus.BAD_REQUEST);
        }
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            System.out.println(bindingResult.getModel());
            for (ObjectError error : errors) {
                System.out.println(error);
                System.out.println(error.getDefaultMessage());
            }
            throw new ValidationException("Ошибка валидации", errors, HttpStatus.BAD_REQUEST);
        }
        CartDto currentCart = cartServiceIntegration.getUserCart(username);
        Order order = new Order();
        order.setFullName(orderDetailsDto.getFullName());
        order.setAddress(orderDetailsDto.getAddress());
        order.setPhone(orderDetailsDto.getPhone());
        order.setUsername(username);
        order.setTotalPrice(currentCart.getTotalPrice());
        order.setOrderStatus(OrderStatus.CREATED);
        Set<OrderItem> items = currentCart.getItems().stream()
                .map(i -> {
                    OrderItem item = new OrderItem();
                    item.setOrder(order);
                    item.setQuantity(i.getQuantity());
                    item.setPricePerProduct(i.getPricePerProduct());
                    item.setPrice(i.getPrice());
                    item.setProduct(productsService.findById(i.getProductId())
                            .orElseThrow(() -> new ClientException("Товар не найден", HttpStatus.NOT_FOUND)));
                    return item;
                }).collect(Collectors.toSet());
        order.setItems(items);
        ordersRepository.save(order);
        cartServiceIntegration.clearUserCart(username);
        log.info("Saved order: " + order.getId());
        orderStatisticService.addStatistic(items);
        return order;
    }

    public boolean isOrderStatusPresent(OrderStatus orderStatus, Long orderId) {
        return ordersRepository.countByStatusAndId(orderStatus, orderId) > 0;
    }

    public List<Order> findAll() {
        return ordersRepository.findAll();
    }

    public List<Order> findByUsername(String username) {
        if (username == null) {
            log.error("Find by username: username = null");
            return Collections.emptyList();
        }
        return ordersRepository.findAllByUsername(username);
    }

    public Optional<Order> findById(Long id) {
        if (id == null) {
            log.error("Find by id: id = null");
            return Optional.empty();
        }
        Optional<Order> optionalOrder = ordersRepository.findById(id);
        log.info("Find by id: id = " + id);
        return optionalOrder;
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new ClientException("Невалидный параметр, идентификатор:" + null, HttpStatus.BAD_REQUEST);
        }
        try {
            ordersRepository.deleteById(id);
            log.info("Deleted: id = " + id);
        } catch (Exception ex) {
            throw new ClientException("Ошибка удаления заказа. Заказ " + id + "не существует", HttpStatus.NOT_FOUND);
        }
    }

    public void changeOrderStatus(OrderStatus orderStatus, Long orderId) {
        try {
            ordersRepository.changeOrderStatus(orderStatus, orderId);
        } catch (ClientException ex) {
            throw new ClientException("Ошибка изменения статуса заказа. Заказ " + orderId + "не существует", HttpStatus.NOT_FOUND);
        }
    }
}
