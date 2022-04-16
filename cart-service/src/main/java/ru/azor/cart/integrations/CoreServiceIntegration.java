package ru.azor.cart.integrations;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.azor.api.core.ProductDto;
import ru.azor.api.exceptions.CoreServiceAppError;
import ru.azor.api.exceptions.CoreServiceIntegrationException;

@Component
@RequiredArgsConstructor
public class CoreServiceIntegration {
    private final WebClient coreServiceWebClient;

    public ProductDto findById(Long id) {
        return coreServiceWebClient.get()
                .uri("/api/v1/products/" + id)
                .header("productId", String.valueOf(id))
                .retrieve()
                .onStatus(
                        HttpStatus::is4xxClientError, // HttpStatus::is4xxClientError
                        clientResponse -> clientResponse.bodyToMono(CoreServiceAppError.class).map(
                                body -> {
                                    if (body.getCode().equals(CoreServiceAppError.CoreServiceErrors.PRODUCT_NOT_FOUND.name())) {
                                        return new CoreServiceIntegrationException("Выполнен некорректный запрос к сервису продуктов: продукт не найден");
                                    }
                                    if (body.getCode().equals(CoreServiceAppError.CoreServiceErrors.CORE_SERVICE_IS_BROKEN.name())) {
                                        return new CoreServiceIntegrationException("Выполнен некорректный запрос к сервису продуктов: сервис сломан");
                                    }
                                    return new CoreServiceIntegrationException("Выполнен некорректный запрос к сервису продуктов: причина неизвестна");
                                }
                        )
                )
                .bodyToMono(ProductDto.class)
                .block();
    }
}
