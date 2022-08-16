package ru.azor.core.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.azor.api.core.OrderDetailsDto;
import ru.azor.api.core.OrderDto;
import ru.azor.api.exceptions.AppError;
import ru.azor.api.exceptions.ClientException;
import ru.azor.core.converters.OrderConverter;
import ru.azor.core.entities.Order;
import ru.azor.core.services.OrdersService;
import ru.azor.core.services.OrderStatisticService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Заказы", description = "Методы работы с заказами")
public class OrdersController {
    private final OrdersService ordersService;
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
                            description = "Ошибка", responseCode = "4XX",
                            content = @Content(schema = @Schema(implementation = AppError.class))
                    )
            }
    )
    @PostMapping
    public ResponseEntity<?> save(@RequestHeader @Parameter(description = "Имя пользователя", required = true) String username, @RequestBody @Valid @Parameter(description = "Детали заказа", required = true) OrderDetailsDto orderDetailsDto,
                                  BindingResult bindingResult) {
        Order order = ordersService.save(username, orderDetailsDto, bindingResult);
        return new ResponseEntity<>(orderConverter.entityToDto(order), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Все заказы",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = ResponseEntity.class))
                    ),
                    @ApiResponse(
                            description = "Ошибка", responseCode = "4XX",
                            content = @Content(schema = @Schema(implementation = AppError.class))
                    )
            }
    )
    @GetMapping
    public ResponseEntity<?> getAll() {
        List<OrderDto> orders = ordersService.findAll().stream()
                .map(orderConverter::entityToDto).collect(Collectors.toList());
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @Operation(
            summary = "Заказы текущего пользователя",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = ResponseEntity.class))
                    ),
                    @ApiResponse(
                            description = "Ошибка", responseCode = "4XX",
                            content = @Content(schema = @Schema(implementation = AppError.class))
                    )
            }
    )
    @GetMapping("/username")
    public ResponseEntity<?> getCurrentUserOrders(@RequestHeader @Parameter(description = "Имя пользователя", required = true) String username) {
        List<OrderDto> orders = ordersService.findByUsername(username).stream()
                .map(orderConverter::entityToDto).collect(Collectors.toList());
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @Operation(
            summary = "Запрос на получение заказа по id",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = OrderDto.class))
                    ),
                    @ApiResponse(
                            description = "Ошибка", responseCode = "4XX",
                            content = @Content(schema = @Schema(implementation = AppError.class))
                    )
            }
    )
    @GetMapping("/{id}")
    public OrderDto getById(@PathVariable @Parameter(description = "Идентификатор заказа", required = true) Long id) {
        return orderConverter.entityToDto(ordersService.findById(id).orElseThrow(() -> new ClientException("Заказ не найден, id: " + id, HttpStatus.NOT_FOUND)));
    }

    @Operation(
            summary = "Запрос на получение статистики",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = ResponseEntity.class))
                    ),
                    @ApiResponse(
                            description = "Ошибка", responseCode = "4XX",
                            content = @Content(schema = @Schema(implementation = AppError.class))
                    )
            }
    )
    @GetMapping("/stat/{quantity}")
    public ResponseEntity<?> getStatistic(@PathVariable @Parameter(description = "Диапазон отбора статистики", required = true) Integer quantity) {
        return new ResponseEntity<>(orderStatisticService.getRangeStatistic(quantity).keySet(), HttpStatus.OK);
    }
}
