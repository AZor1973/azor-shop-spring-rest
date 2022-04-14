package ru.azor.cart.integrations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.azor.api.core.ProductDto;
import ru.azor.api.exceptions.CoreServiceAppError;
import ru.azor.cart.exceptions.ProductServiceIntegrationException;

@Component
@RequiredArgsConstructor
public class ProductsServiceIntegration {
    private final WebClient coreServiceWebClient;

    public ProductDto findById(Long id) {
        return coreServiceWebClient.get()
                .uri("/api/v1/products/" + id)
                .header("productId", String.valueOf(id))
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.is4xxClientError(), // HttpStatus::is4xxClientError
                        clientResponse -> clientResponse.bodyToMono(CoreServiceAppError.class).map(
                                body -> {
                                    if (body.getCode().equals(CoreServiceAppError.CoreServiceErrors.PRODUCT_NOT_FOUND.name())) {
                                        return new ProductServiceIntegrationException("Выполнен некорректный запрос к сервису продуктов: продукт не найден");
                                    }
                                    if (body.getCode().equals(CoreServiceAppError.CoreServiceErrors.CORE_SERVICE_IS_BROKEN.name())) {
                                        return new ProductServiceIntegrationException("Выполнен некорректный запрос к сервису продуктов: сервис сломан");
                                    }
                                    return new ProductServiceIntegrationException("Выполнен некорректный запрос к сервису продуктов: причина неизвестна");
                                }
                        )
                )
                .bodyToMono(ProductDto.class)
                .block();
    }
}
