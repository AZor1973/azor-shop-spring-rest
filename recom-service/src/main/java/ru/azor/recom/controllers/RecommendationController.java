package ru.azor.recom.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.azor.api.core.ProductDto;
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

    @GetMapping("/daily")
    public List<ProductDto> getDailyStatistic() {
        return recommendationService.getStatisticFromCartService(quantity);
    }

    @GetMapping("/monthly")
    public List<ProductDto> getMonthlyStatistic() {
        return recommendationService.getStatisticFromCoreService(quantity);
    }
}
