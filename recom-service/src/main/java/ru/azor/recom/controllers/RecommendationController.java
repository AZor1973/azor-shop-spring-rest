package ru.azor.recom.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.azor.api.core.ProductDto;
import ru.azor.api.exceptions.CoreServiceAppError;
import ru.azor.recom.services.RecommendationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recom")
@RequiredArgsConstructor
@Tag(name = "Рекомендации", description = "Методы работы с рекомендациями")
public class RecommendationController {
    private final RecommendationService recommendationService;
    @Value("${utils.statistic.quantity}")
    private Integer quantity;

    @Operation(
            summary = "Запрос на получение ежедневной статистики",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = List.class))
                    )
            }
    )
    @GetMapping("/daily")
    public List<ProductDto> getDailyStatistic() {
        return recommendationService.getStatisticFromCartService(quantity);
    }

    @Operation(
            summary = "Запрос на получение ежемесячной статистики",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = List.class))
                    )
            }
    )
    @GetMapping("/monthly")
    public List<ProductDto> getMonthlyStatistic() {
        return recommendationService.getStatisticFromCoreService(quantity);
    }
}
