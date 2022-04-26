package ru.azor.core.integrations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.azor.api.carts.CartDto;
import ru.azor.api.core.ProductDto;
import ru.azor.api.exceptions.CartServiceAppError;
import ru.azor.api.exceptions.CartServiceIntegrationException;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartServiceIntegration {
    private final WebClient cartServiceWebClient;
    private final ConcurrentHashMap<String, CartDto> identityMap = new ConcurrentHashMap<>();


    public void clearUserCart(String username) {
        cartServiceWebClient.get()
                .uri("/api/v1/cart/0/clear")
                .header("username", username)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public CartDto getUserCart(String username) {
        if (identityMap.containsKey(username)) {
            return identityMap.get(username);
        }
        CartDto cartDto = cartServiceWebClient.get()
                .uri("/api/v1/cart/0")
                .header("username", username)
                // .bodyValue(body) // for POST
                .retrieve()
                .onStatus(
                        HttpStatus::is4xxClientError, // HttpStatus::is4xxClientError
                        clientResponse -> clientResponse.bodyToMono(CartServiceAppError.class).map(
                                body -> {
                                    if (body.getCode().equals(CartServiceAppError.CartServiceErrors.CART_NOT_FOUND)) {
                                        log.error("Выполнен некорректный запрос к сервису корзин: корзина не найдена");
                                        return new CartServiceIntegrationException("Выполнен некорректный запрос к сервису корзин: корзина не найдена");
                                    }
                                    if (body.getCode().equals(CartServiceAppError.CartServiceErrors.CART_IS_BROKEN)) {
                                        log.error("Выполнен некорректный запрос к сервису корзин: корзина сломана");
                                        return new CartServiceIntegrationException("Выполнен некорректный запрос к сервису корзин: корзина сломана");
                                    }
                                    log.error("Выполнен некорректный запрос к сервису корзин: причина неизвестна");
                                    return new CartServiceIntegrationException("Выполнен некорректный запрос к сервису корзин: причина неизвестна");
                                }
                        )
                )
                .bodyToMono(CartDto.class)
                .block();
        if (cartDto != null){
            identityMap.put(username, cartDto);
        }
        return cartDto;
    }

    @Scheduled(cron = "${utils.identity-map.clear-cron}")
    @Async
    public void clearIdentityMap() {
      identityMap.clear();
    }
}
