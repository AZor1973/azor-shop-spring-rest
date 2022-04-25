package ru.azor.core.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.azor.api.core.OrderDetailsDto;
import ru.azor.api.core.OrderDto;
import ru.azor.api.common.StringResponseRequestDto;
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
@Tag(name = "Заказы", description = "Методы работы с заказами")
public class OrdersController {
    private final OrderService orderService;
    private final OrderStatisticService orderStatisticService;
    private final OrderConverter orderConverter;

    @Operation(
            summary = "Создание заказа",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "201",
                            content = @Content(schema = @Schema(implementation = ResponseEntity.class))
                    ),
                    @ApiResponse(
                            description = "Ошибка", responseCode = "400",
                            content = @Content(schema = @Schema(implementation = ResponseEntity.class))
                    )
            }
    )
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestHeader @Parameter(description = "Имя пользователя", required = true) String username, @RequestBody @Valid @Parameter(description = "Детали заказа", required = true) OrderDetailsDto orderDetailsDto,
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

    @Operation(
            summary = "Заказы текущего пользователя",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = List.class))
                    )
            }
    )
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderDto> getCurrentUserOrders(@RequestHeader @Parameter(description = "Имя пользователя", required = true) String username) {
        return orderService.findOrdersByUsername(username).stream()
                .map(orderConverter::entityToDto).collect(Collectors.toList());
    }

    @Operation(
            summary = "Запрос на получение заказа по id",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = OrderDto.class))
                    )
            }
    )
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OrderDto getOrderById(@PathVariable @Parameter(description = "Идентификатор заказа", required = true) Long id) {
        return orderConverter.entityToDto(orderService.findById(id).orElseThrow(() -> new ResourceNotFoundException("ORDER 404")));
    }

    @Operation(
            summary = "Запрос на получение статистики",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = StringResponseRequestDto.class))
                    )
            }
    )
    @GetMapping("/stat/{quantity}")
    @ResponseStatus(HttpStatus.OK)
    public StringResponseRequestDto getStatistic(@PathVariable @Parameter(description = "Диапазон отбора статистики", required = true) Integer quantity) {
        return StringResponseRequestDto.builder()
                .list(new CopyOnWriteArrayList<>(orderStatisticService.getRangeStatistic(quantity).keySet()))
                .httpStatus(HttpStatus.OK)
                .build();
    }
}
