package ru.azor.cart.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.azor.api.carts.CartDto;
import ru.azor.api.common.StringResponseRequestDto;
import ru.azor.api.core.ProductDto;
import ru.azor.api.exceptions.AppError;
import ru.azor.cart.converters.CartConverter;
import ru.azor.cart.services.CartService;
import ru.azor.cart.services.CartStatisticService;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
@Tag(name = "Корзина", description = "Методы работы с корзиной")
public class CartsController {
    private final CartService cartService;
    private final CartStatisticService cartStatisticService;
    private final CartConverter cartConverter;

    @Operation(
            summary = "Запрос на получение корзины",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = CartDto.class))
                    ),
                    @ApiResponse(
                            description = "Ошибка", responseCode = "4XX",
                            content = @Content(schema = @Schema(implementation = AppError.class))
                    )
            }
    )
    @GetMapping("/{uuid}")
    public CartDto getCart(@RequestHeader(required = false) @Parameter(description = "Имя пользователя") String username,
                           @PathVariable @Parameter(description = "Идентификатор корзины", required = true) String uuid) {
        return cartConverter.modelToDto(cartService.getCurrentCart(getCurrentCartUuid(username, uuid)));
    }

    @Operation(
            summary = "Генерация UUID корзины",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = StringResponseRequestDto.class))
                    ),
                    @ApiResponse(
                            description = "Ошибка", responseCode = "4XX",
                            content = @Content(schema = @Schema(implementation = AppError.class))
                    )
            }
    )
    @GetMapping("/generate")
    public StringResponseRequestDto getCart() {
        return StringResponseRequestDto.builder().value(cartService.generateCartUuid()).build();
    }

    @Operation(
            summary = "Добавление продукта в корзину",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Ошибка", responseCode = "4XX",
                            content = @Content(schema = @Schema(implementation = AppError.class))
                    )
            }
    )
    @GetMapping("/{uuid}/add/{productId}")
    public void add(@RequestHeader(required = false) @Parameter(description = "Имя пользователя") String username,
                    @PathVariable @Parameter(description = "Идентификатор корзины", required = true) String uuid,
                    @PathVariable @Parameter(description = "Идентификатор продукта", required = true) Long productId) {
        cartService.addToCart(getCurrentCartUuid(username, uuid), productId);
    }

    @Operation(
            summary = "Уменьшение количества продукта в корзине",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Ошибка", responseCode = "4XX",
                            content = @Content(schema = @Schema(implementation = AppError.class))
                    )
            }
    )
    @GetMapping("/{uuid}/decrement/{productId}")
    public void decrement(@RequestHeader(required = false) @Parameter(description = "Имя пользователя") String username,
                          @PathVariable @Parameter(description = "Идентификатор корзины", required = true) String uuid,
                          @PathVariable @Parameter(description = "Идентификатор продукта", required = true) Long productId) {
        cartService.decrementItem(getCurrentCartUuid(username, uuid), productId);
    }

    @Operation(
            summary = "Увеличение количества продукта в корзине",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Ошибка", responseCode = "4XX",
                            content = @Content(schema = @Schema(implementation = AppError.class))
                    )
            }
    )
    @GetMapping("/{uuid}/increment/{productId}")
    public void increment(@RequestHeader(required = false) @Parameter(description = "Имя пользователя") String username,
                          @PathVariable @Parameter(description = "Идентификатор корзины", required = true) String uuid,
                          @PathVariable @Parameter(description = "Идентификатор продукта", required = true) Long productId) {
        cartService.incrementItem(getCurrentCartUuid(username, uuid), productId);
    }

    @Operation(
            summary = "Удаление продукта из корзины",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Ошибка", responseCode = "4XX",
                            content = @Content(schema = @Schema(implementation = AppError.class))
                    )
            }
    )
    @GetMapping("/{uuid}/remove/{productId}")
    public void remove(@RequestHeader(required = false) @Parameter(description = "Имя пользователя") String username,
                       @PathVariable @Parameter(description = "Идентификатор корзины", required = true) String uuid,
                       @PathVariable @Parameter(description = "Идентификатор продукта", required = true) Long productId) {
        cartService.removeItemFromCart(getCurrentCartUuid(username, uuid), productId);
    }

    @Operation(
            summary = "Очистка корзины",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Ошибка", responseCode = "4XX",
                            content = @Content(schema = @Schema(implementation = AppError.class))
                    )
            }
    )
    @GetMapping("/{uuid}/clear")
    public void clear(@RequestHeader(required = false) @Parameter(description = "Имя пользователя") String username,
                      @PathVariable @Parameter(description = "Идентификатор корзины", required = true) String uuid) {
        cartService.clearCart(getCurrentCartUuid(username, uuid));
    }

    @Operation(
            summary = "Слияние корзин",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Ошибка", responseCode = "4XX",
                            content = @Content(schema = @Schema(implementation = AppError.class))
                    )
            }
    )
    @GetMapping("/{uuid}/merge")
    public void merge(@RequestHeader(required = false) @Parameter(description = "Имя пользователя") String username,
                      @PathVariable @Parameter(description = "Идентификатор корзины", required = true) String uuid) {
        cartService.merge(
                getCurrentCartUuid(username, null),
                getCurrentCartUuid(null, uuid)
        );
    }

    private String getCurrentCartUuid(String username, String uuid) {
        if (username != null) {
            return cartService.getCartUuidFromSuffix(username);
        }
        return cartService.getCartUuidFromSuffix(uuid);
    }

    @Operation(
            summary = "Запрос на получение статистики",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = StringResponseRequestDto.class))
                    ),
                    @ApiResponse(
                            description = "Ошибка", responseCode = "4XX",
                            content = @Content(schema = @Schema(implementation = AppError.class))
                    )
            }
    )
    @GetMapping("/stat/{quantity}")
    public Set<ProductDto> getStatistic(@PathVariable @Parameter(description = "Диапазон отбора статистики", required = true) Integer quantity) {
        return cartStatisticService.getRangeStatistic(quantity).keySet();
    }
}
