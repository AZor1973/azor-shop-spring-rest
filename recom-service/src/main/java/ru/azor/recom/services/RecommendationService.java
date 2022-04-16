package ru.azor.recom.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.azor.api.core.ProductDto;
import ru.azor.recom.integrations.CartServiceIntegration;
import ru.azor.recom.integrations.CoreServiceIntegration;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final CartServiceIntegration cartServiceIntegration;
    private final CoreServiceIntegration coreServiceIntegration;

    public List<ProductDto> getStatisticFromCartService(Integer quantity) {
        return (List<ProductDto>) cartServiceIntegration.getStatisticFromCartService(quantity)
                .getList();
    }

    public List<ProductDto> getStatisticFromCoreService(Integer quantity) {
        return (List<ProductDto>) coreServiceIntegration.getStatisticFromCoreService(quantity)
                .getList();
    }
}
