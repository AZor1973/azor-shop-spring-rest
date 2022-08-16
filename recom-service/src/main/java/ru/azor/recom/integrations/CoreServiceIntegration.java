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
                                    if (body.getCode().equals(CoreServiceAppError.CoreServiceErrors.STATISTIC_NOT_FOUND)) {
                                        log.error("Выполнен некорректный запрос к основному сервису: статистика не найдена");
                                        return new CoreServiceIntegrationException("Выполнен некорректный запрос к основному сервису: статистика не найдена");
                                    }
                                    if (body.getCode().equals(CoreServiceAppError.CoreServiceErrors.CORE_SERVICE_IS_BROKEN)) {
                                        log.error("Выполнен некорректный запрос к основному сервису: основной сервис сломан");
                                        return new CoreServiceIntegrationException("Выполнен некорректный запрос к основному сервису: основной сервис сломан");
                                    }
                                    log.error("Выполнен некорректный запрос к основному сервису: причина неизвестна");
                                    return new CoreServiceIntegrationException("Выполнен некорректный запрос к основному сервису: причина неизвестна");
                                }
                        )
                )
                .bodyToMono(StringResponseRequestDto.class)
                .block();
    }
}
