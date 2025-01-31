package com.example.everguide.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private static final int TIMEOUT = 5000;
    private static final int MAX_MEMORY_SIZE = 2 * 1024 * 1024; // 2MB
    private final ExternalApiProperties apiProperties;

    /*
     * 각 API(정책, 교육, 일자리)별로 별도의 WebClient Bean을 생성
     * 각 WebClient는 해당 API의 baseUrl을 사용하여 초기화
     */

    // 정책 API WebClient 생성
    @Bean
    public WebClient policyWebClient() {
        return WebClient.builder()
                .baseUrl(apiProperties.getPolicy().getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
                .codecs(configurer -> {
                    configurer.defaultCodecs().maxInMemorySize(MAX_MEMORY_SIZE);
                    configurer.defaultCodecs().jaxb2Decoder(new Jaxb2XmlDecoder());
                    configurer.defaultCodecs().jaxb2Encoder(new Jaxb2XmlEncoder());
                })
                .build();
    }

    @Bean
    public WebClient educationWebClient() {
        return WebClient.builder()
                .baseUrl(apiProperties.getEducation().getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
                .codecs(configurer -> {
                    configurer.defaultCodecs().maxInMemorySize(MAX_MEMORY_SIZE);
                })
                .build();
    }

    @Bean
    public WebClient jobWebClient() {
        return WebClient.builder()
                .baseUrl(apiProperties.getJob().getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
                .codecs(configurer -> {
                    configurer.defaultCodecs().maxInMemorySize(MAX_MEMORY_SIZE);
                })
                .build();
    }

    /* 공통 설정 메서드 */
    private WebClient createWebClient(String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(MAX_MEMORY_SIZE))
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
