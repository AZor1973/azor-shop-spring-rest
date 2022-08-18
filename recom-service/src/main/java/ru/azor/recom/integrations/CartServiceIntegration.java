package ru.azor.recom.integrations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.azor.api.exceptions.AppError;
import ru.azor.api.exceptions.ClientException;
import ru.azor.api.exceptions.ServerException;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartServiceIntegration {
    private final WebClient cartServiceWebClient;

    public Set<?> getStatisticFromCartService(Integer quantity) {
        return cartServiceWebClient.get()
                .uri("/api/v1/carts/stat/" + quantity)
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
                .bodyToMono(Set.class)
                .block();
    }
}
