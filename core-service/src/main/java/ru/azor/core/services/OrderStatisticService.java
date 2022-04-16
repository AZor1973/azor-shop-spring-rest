package ru.azor.core.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.azor.api.core.ProductDto;
import ru.azor.core.converters.ProductConverter;
import ru.azor.core.entities.OrderItem;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OrderStatisticService {
    private final RedisTemplate<String, ConcurrentHashMap<ProductDto, Integer>> statisticalRedisTemplate;
    private final ProductConverter productConverter;
    @Value("${utils.statistic.key}")
    private String statisticKey;

    public ConcurrentHashMap<ProductDto, Integer> getAllStatistic() {
        if (Boolean.FALSE.equals(statisticalRedisTemplate.hasKey(statisticKey))) {
            statisticalRedisTemplate.opsForValue().set(statisticKey, new ConcurrentHashMap<>());
        }
        return statisticalRedisTemplate.opsForValue().get(statisticKey);
    }

    public ConcurrentHashMap<ProductDto, Integer> getRangeStatistic(Integer quantity) {
        if (Boolean.FALSE.equals(statisticalRedisTemplate.hasKey(statisticKey))) {
            statisticalRedisTemplate.opsForValue().set(statisticKey, new ConcurrentHashMap<>());
            return statisticalRedisTemplate.opsForValue().get(statisticKey);
        }
        ConcurrentHashMap<ProductDto, Integer> result = new ConcurrentHashMap<>();
        Objects.requireNonNull(statisticalRedisTemplate.opsForValue().get(statisticKey))
                .entrySet().stream().sorted(Map.Entry.<ProductDto, Integer>comparingByValue().reversed()).limit(quantity)
                .forEach(entry -> result.put(entry.getKey(), entry.getValue()));
        return result;
    }

    public void addStatistic(List<OrderItem> orderItems) {
        ConcurrentHashMap<ProductDto, Integer> result = new ConcurrentHashMap<>();
        orderItems.stream().map(i -> productConverter.productToProductDto(i.getProduct()))
                .forEach(p -> {
                    if (result.containsKey(p)) {
                        result.put(p, result.get(p) + 1);
                    } else {
                        result.put(p, 1);
                    }
                });
        statisticalRedisTemplate.opsForValue().set(statisticKey, result);
    }

    @Scheduled(cron = "${utils.statistic.clear-cron}")
    @Async
    public void clearStatistic() {
        ConcurrentHashMap<ProductDto, Integer> statistic = getAllStatistic();
        statistic.clear();
        statisticalRedisTemplate.opsForValue().set(statisticKey, statistic);
    }
}
