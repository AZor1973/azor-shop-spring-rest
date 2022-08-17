package ru.azor.cart.integrations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.azor.api.core.ProductDto;
import ru.azor.api.exceptions.AppError;
import ru.azor.api.exceptions.ClientException;
import ru.azor.api.exceptions.ServerException;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoreServiceIntegration {
    private final WebClient coreServiceWebClient;
    private final ConcurrentHashMap<Long, ProductDto> identityMap = new ConcurrentHashMap<>();

    public ProductDto findById(Long id) {
        if (identityMap.containsKey(id)) {
            return identityMap.get(id);
        }
        ProductDto productDto = coreServiceWebClient.get()
                .uri("/api/v1/products/" + id)
                .header("productId", String.valueOf(id))
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
                            throw new ServerException("Сервис продуктов недоступен", clientResponse.statusCode());
                        }
                )
                .bodyToMono(ProductDto.class)
                .block();
        if (productDto != null) {
            identityMap.put(id, productDto);
        }
        return productDto;
    }

    @Scheduled(cron = "${utils.identity-map.clear-cron}")
    @Async
    public void clearIdentityMap() {
        identityMap.clear();
    }
}
