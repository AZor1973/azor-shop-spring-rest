package ru.azor.core.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ru.azor.api.core.ProductDto;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableRedisRepositories
public class RedisConfig {
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, ConcurrentHashMap<ProductDto, Integer>> statisticalRedisTemplate(){
        RedisTemplate<String, ConcurrentHashMap<ProductDto, Integer>> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }
}
