package com.example.everguide.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private static final int TIMEOUT = 60000; // 30초
    private static final int MAX_MEMORY_SIZE = 16 * 1024 * 1024; // 16MB
    private final ExternalApiProperties apiProperties;

    @Bean("policyWebClient")
    public WebClient policyWebClient() {
        List<MediaType> supportedMediaTypes = List.of(
                MediaType.TEXT_XML,
                MediaType.APPLICATION_XML,
                MediaType.valueOf("application/xml;charset=UTF-8"),
                MediaType.valueOf("text/xml;charset=UTF-8")
        );

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> {
                    configurer.defaultCodecs().maxInMemorySize(MAX_MEMORY_SIZE);
                    
                    // XML 디코더/인코더 설정
                    configurer.defaultCodecs().jaxb2Decoder(new Jaxb2XmlDecoder());
                    configurer.defaultCodecs().jaxb2Encoder(new Jaxb2XmlEncoder());
                })
                .build();

        return WebClient.builder()
                .baseUrl(apiProperties.getPolicy().getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
                .exchangeStrategies(strategies)
                .defaultHeaders(headers -> {
                    headers.setAccept(supportedMediaTypes);
                    headers.setContentType(MediaType.APPLICATION_XML);
                })
                .build();
    }

    @Bean
    public WebClient educationWebClient() {
        return createWebClient(apiProperties.getEducation().getBaseUrl());
    }

    @Bean
    public WebClient jobWebClient() {
        return createWebClient(apiProperties.getJob().getBaseUrl());
    }

    private WebClient createWebClient(String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(MAX_MEMORY_SIZE))
                .build();
    }

    private HttpClient createHttpClient() {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT)
                .responseTimeout(Duration.ofMillis(TIMEOUT))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS)));
    }
}