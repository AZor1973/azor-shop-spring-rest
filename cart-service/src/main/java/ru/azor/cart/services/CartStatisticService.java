package ru.azor.cart.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.azor.api.core.ProductDto;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class CartStatisticService {
    private final RedisTemplate<String, ConcurrentHashMap<ProductDto, Integer>> statisticalRedisTemplate;
    @Value("${utils.statistic.key}")
    private String statisticKey;
    private final ConcurrentHashMap<ProductDto, Integer> currentStatistic = new ConcurrentHashMap<>();
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

    public void addStatistic(ProductDto productDto) {
        if (currentStatistic.containsKey(productDto)) {
            currentStatistic.put(productDto, currentStatistic.get(productDto) + 1);
        } else {
            currentStatistic.put(productDto, 1);
        }
    }

    @Scheduled(cron = "${utils.statistic.add-cron}")
    @Async
    public void loadStatisticToRedis(){
        statisticalRedisTemplate.opsForValue().set(statisticKey, currentStatistic);
    }

    @Scheduled(cron = "${utils.statistic.clear-cron}")
    @Async
    public void clearStatistic() {
        ConcurrentHashMap<ProductDto, Integer> statistic = getAllStatistic();
        statistic.clear();
        statisticalRedisTemplate.opsForValue().set(statisticKey, statistic);
    }
}
