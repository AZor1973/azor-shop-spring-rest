package ru.azor.recom.integrations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.azor.api.common.StringResponseRequestDto;
import ru.azor.api.exceptions.AppError;
import ru.azor.api.exceptions.ClientException;
import ru.azor.api.exceptions.ServerException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoreServiceIntegration {
    private final WebClient coreServiceWebClient;

    public StringResponseRequestDto getStatisticFromCoreService(Integer quantity) {
        return coreServiceWebClient.get()
                .uri("/api/v1/orders/stat/" + quantity)
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
                            throw new ServerException("Сервис заказов недоступен", clientResponse.statusCode());
                        }
                )
                .bodyToMono(StringResponseRequestDto.class)
                .block();
    }
}
