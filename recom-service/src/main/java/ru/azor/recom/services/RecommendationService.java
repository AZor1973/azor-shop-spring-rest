package ru.azor.recom.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.azor.api.core.ProductDto;
import ru.azor.recom.integrations.CartServiceIntegration;
import ru.azor.recom.integrations.CoreServiceIntegration;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final CartServiceIntegration cartServiceIntegration;
    private final CoreServiceIntegration coreServiceIntegration;

    public Set<ProductDto> getStatisticFromCartService(Integer quantity) {
        return (Set<ProductDto>) cartServiceIntegration.getStatisticFromCartService(quantity);
    }

    public Set<ProductDto> getStatisticFromCoreService(Integer quantity) {
        return (Set<ProductDto>) coreServiceIntegration.getStatisticFromCoreService(quantity);
    }
}
