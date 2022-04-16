package ru.azor.core.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.azor.api.core.OrderDetailsDto;
import ru.azor.api.core.OrderDto;
import ru.azor.api.dto.StringResponseRequestDto;
import ru.azor.api.exceptions.ResourceNotFoundException;
import ru.azor.core.converters.OrderConverter;
import ru.azor.core.services.OrderService;
import ru.azor.core.services.OrderStatisticService;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrdersController {
    private final OrderService orderService;
    private final OrderStatisticService orderStatisticService;
    private final OrderConverter orderConverter;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestHeader String username, @RequestBody @Valid OrderDetailsDto orderDetailsDto,
                                         BindingResult bindingResult) {
        String response;
        HttpStatus httpStatus;
        List<String> errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        if (bindingResult.hasErrors()) {
            response = String.join(" ,", errors);
            httpStatus = HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(StringResponseRequestDto.builder()
                    .value(response).build(), httpStatus);
        }
        orderService.createOrder(username, orderDetailsDto);
        httpStatus = HttpStatus.CREATED;
        return new ResponseEntity<>(StringResponseRequestDto.builder()
                .value("Order created").build(), httpStatus);
    }

    @GetMapping
    public List<OrderDto> getCurrentUserOrders(@RequestHeader String username) {
        return orderService.findOrdersByUsername(username).stream()
                .map(orderConverter::entityToDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public OrderDto getOrderById(@PathVariable Long id) {
        return orderConverter.entityToDto(orderService.findById(id).orElseThrow(() -> new ResourceNotFoundException("ORDER 404")));
    }

    @GetMapping("/stat/{quantity}")
    public StringResponseRequestDto getDailyStatistic(@PathVariable Integer quantity) {
        return StringResponseRequestDto.builder()
                .list(new CopyOnWriteArrayList<>(orderStatisticService.getRangeStatistic(quantity).keySet()))
                .httpStatus(HttpStatus.OK)
                .build();
    }
}
