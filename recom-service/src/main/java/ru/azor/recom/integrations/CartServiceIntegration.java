package ru.azor.recom.integrations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.azor.api.common.StringResponseRequestDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartServiceIntegration {
    private final WebClient cartServiceWebClient;

    public StringResponseRequestDto getStatisticFromCartService(Integer quantity) {
        return cartServiceWebClient.get()
                .uri("/api/v1/cart/stat/" + quantity)
                .retrieve()
                .onStatus(
                        HttpStatus::is4xxClientError, // HttpStatus::is4xxClientError
                        clientResponse -> clientResponse.bodyToMono(CartServiceAppError.class).map(
                                body -> {
                                    if (body.getCode().equals(CartServiceAppError.CartServiceErrors.STATISTIC_NOT_FOUND)) {
                                        log.error("Выполнен некорректный запрос к сервису корзин: статистика не найдена");
                                        return new CartServiceIntegrationException("Выполнен некорректный запрос к сервису корзин: статистика не найдена");
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
                .bodyToMono(StringResponseRequestDto.class)
                .block();
    }
}
