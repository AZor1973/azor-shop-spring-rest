package ru.azor.recom.configs;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import ru.azor.recom.properties.CartServiceIntegrationProperties;
import ru.azor.recom.properties.CoreServiceIntegrationProperties;


import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(
        {CartServiceIntegrationProperties.class,
                CoreServiceIntegrationProperties.class}
)
@RequiredArgsConstructor
public class WebClientConfig {
    private final CartServiceIntegrationProperties cartServiceIntegrationProperties;
    private final CoreServiceIntegrationProperties coreServiceIntegrationProperties;

    @Bean
    public WebClient cartServiceWebClient() {
        return getWebClient(cartServiceIntegrationProperties.getConnectTimeout(), cartServiceIntegrationProperties.getReadTimeout(), cartServiceIntegrationProperties.getWriteTimeout(), cartServiceIntegrationProperties.getResponseTimeout(), cartServiceIntegrationProperties.getUrl());
    }

    @Bean
    public WebClient coreServiceWebClient() {
        return getWebClient(coreServiceIntegrationProperties.getConnectTimeout(), coreServiceIntegrationProperties.getReadTimeout(), coreServiceIntegrationProperties.getWriteTimeout(), coreServiceIntegrationProperties.getResponseTimeout(), coreServiceIntegrationProperties.getUrl());
    }

    private WebClient getWebClient(Integer connectTimeout, Integer readTimeout, Integer writeTimeout, Integer responseTimeout, String url) {
        HttpClient httpClient = reactor.netty.http.client.HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .responseTimeout(Duration.ofMillis(responseTimeout))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(writeTimeout, TimeUnit.MILLISECONDS)));

        return WebClient
                .builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
