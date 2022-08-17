package ru.azor.core.integrations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.azor.api.carts.CartDto;
import ru.azor.api.exceptions.AppError;
import ru.azor.api.exceptions.ClientException;
import ru.azor.api.exceptions.ServerException;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartServiceIntegration {
    private final WebClient cartServiceWebClient;
    private final ConcurrentHashMap<String, CartDto> identityMap = new ConcurrentHashMap<>();


    public void clearUserCart(String username) {
        cartServiceWebClient.get()
                .uri("/api/v1/carts/0/clear")
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
                .uri("/api/v1/carts/0")
                .header("username", username)
                // .bodyValue(body) // for POST
                .retrieve()
                .onStatus(
                        HttpStatus::is4xxClientError,
                        clientResponse -> clientResponse.bodyToMono(AppError.class).map(
                                body -> {
                                    log.error(body.getMessage());
                                    return new ClientException(body.getMessage(), clientResponse.statusCode());
                                }
                        )
                )
                .onStatus(
                        HttpStatus::is5xxServerError,
                        clientResponse -> {
                            throw new ServerException("Сервис корзины недоступен", clientResponse.statusCode());
                        }
                )
                .bodyToMono(CartDto.class)
                .block();
        if (cartDto != null) {
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
