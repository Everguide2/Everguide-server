package com.example.everguide.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> {
                    configurer.defaultCodecs().jaxb2Decoder(new Jaxb2XmlDecoder());
                    configurer.defaultCodecs().jaxb2Encoder(new Jaxb2XmlEncoder());
                    configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024); // 16MB
                })
                .build();

        return webClientBuilder
                .baseUrl("https://apis.data.go.kr/B554287/LocalGovernmentWelfareInformations")
                .exchangeStrategies(strategies)
                .filter(logRequest())
                .filter(logResponse())
                .filter(ExchangeFilterFunction.ofRequestProcessor(request -> {
                    String originalUrl = request.url().toString();
                    
                    // URL을 두 번 디코딩하여 중복 인코딩 해결
                    String decodedUrl = URLDecoder.decode(
                            URLDecoder.decode(originalUrl, StandardCharsets.UTF_8),
                            StandardCharsets.UTF_8
                    );
                    
                    log.info("Final Request URL: {}", decodedUrl);
                    
                    return Mono.just(
                            ClientRequest.from(request)
                                    .url(URI.create(decodedUrl))
                                    .build()
                    );
                }))
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Original Request URL: {}", clientRequest.url().toString());
            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("Response Status: {}", clientResponse.statusCode());
            return clientResponse.bodyToMono(String.class)
                    .doOnNext(body -> log.info("Response Body: {}", body))
                    .map(body -> clientResponse.mutate().body(body).build());
        });
    }
}
