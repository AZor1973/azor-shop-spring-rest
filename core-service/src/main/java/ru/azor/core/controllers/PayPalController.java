package ru.azor.core.controllers;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.Order;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersCaptureRequest;
import com.paypal.orders.OrdersCreateRequest;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.azor.api.common.StringResponseRequestDto;
import ru.azor.api.enums.OrderStatus;
import ru.azor.core.services.OrderService;
import ru.azor.core.services.PayPalService;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/v1/paypal")
@RequiredArgsConstructor
@Tag(name = "PayPal", description = "Методы работы с PayPal")
public class PayPalController {
    private final PayPalHttpClient payPalClient;
    private final OrderService orderService;
    private final PayPalService payPalService;

    @Operation(
            summary = "Создание заказа для оплаты",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "201",
                            content = @Content(schema = @Schema(implementation = ResponseEntity.class))
                    ),
                    @ApiResponse(
                            description = "Ошибка", responseCode = "409",
                            content = @Content(schema = @Schema(implementation = ResponseEntity.class))
                    )
            }
    )
    @PostMapping("/create/{orderId}")
    public ResponseEntity<?> createOrder(@PathVariable @Parameter(description = "Идентификатор заказа", required = true) Long orderId) throws IOException {
        if (orderService.isOrderStatusPresent(OrderStatus.PAID, orderId)) {
            log.error("Оплата невозможна. Заказ уже оплачен");
            return new ResponseEntity<>(StringResponseRequestDto.builder()
                    .value("Оплата невозможна. Заказ уже оплачен")
                    .build(), HttpStatus.CONFLICT);
        }
        OrdersCreateRequest request = new OrdersCreateRequest();
        request.prefer("return=representation");
        request.requestBody(payPalService.createOrderRequest(orderId));
        HttpResponse<Order> response = payPalClient.execute(request);
        return new ResponseEntity<>(response.result().id(), HttpStatus.valueOf(response.statusCode()));
    }

    @Operation(
            summary = "Оплата заказа",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = ResponseEntity.class))
                    )
            }
    )
    @PostMapping("/capture/{payPalId}")
    public ResponseEntity<?> captureOrder(@PathVariable @Parameter(description = "Идентификатор PayPal", required = true) String payPalId) throws IOException {
        OrdersCaptureRequest request = new OrdersCaptureRequest(payPalId);
        request.requestBody(new OrderRequest());

        HttpResponse<Order> response = payPalClient.execute(request);
        Order payPalOrder = response.result();
        if ("COMPLETED".equals(payPalOrder.status())) {
            long orderId = Long.parseLong(payPalOrder.purchaseUnits().get(0).referenceId());
            orderService.changeOrderStatus(OrderStatus.PAID, orderId);
            log.info("Order completed!");
            return new ResponseEntity<>("Order completed!", HttpStatus.valueOf(response.statusCode()));
        }
        return new ResponseEntity<>(payPalOrder, HttpStatus.valueOf(response.statusCode()));
    }
}
