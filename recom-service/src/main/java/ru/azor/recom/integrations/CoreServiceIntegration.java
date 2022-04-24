package ru.azor.recom.integrations;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.azor.api.common.StringResponseRequestDto;
import ru.azor.api.exceptions.CoreServiceAppError;
import ru.azor.api.exceptions.CoreServiceIntegrationException;


@Component
@RequiredArgsConstructor
public class CoreServiceIntegration {
    private final WebClient coreServiceWebClient;

    public StringResponseRequestDto getStatisticFromCoreService(Integer quantity) {
        return coreServiceWebClient.get()
                .uri("/api/v1/orders/stat/" + quantity)
                .retrieve()
                .onStatus(
                        HttpStatus::is4xxClientError, // HttpStatus::is4xxClientError
                        clientResponse -> clientResponse.bodyToMono(CoreServiceAppError.class).map(
                                body -> {
                                    if (body.getCode().equals(CoreServiceAppError.CoreServiceErrors.STATISTIC_NOT_FOUND.name())) {
                                        return new CoreServiceIntegrationException("Выполнен некорректный запрос к основному сервису: статистика не найдена");
                                    }
                                    if (body.getCode().equals(CoreServiceAppError.CoreServiceErrors.CORE_SERVICE_IS_BROKEN.name())) {
                                        return new CoreServiceIntegrationException("Выполнен некорректный запрос к основному сервису: основной сервис сломан");
                                    }
                                    return new CoreServiceIntegrationException("Выполнен некорректный запрос к основному сервису: причина неизвестна");
                                }
                        )
                )
                .bodyToMono(StringResponseRequestDto.class)
                .block();
    }
}
