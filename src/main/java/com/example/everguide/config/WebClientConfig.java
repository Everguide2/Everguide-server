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
                    configurer.defaultCodecs().jaxb2Decoder(new Jaxb2XmlDecoder()); //xml 디코더 설정
                    configurer.defaultCodecs().jaxb2Encoder(new Jaxb2XmlEncoder()); //xml 인코더 설정
                    configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024); // 최대 메모리 크기 16MB
                })
                .build();

        return webClientBuilder
                .baseUrl("http://apis.data.go.kr/B552474/JobBsnInfoService/getJobBsnRecruitList")
                .exchangeStrategies(strategies) //위에서 정한 설정 추가
                .filter(logRequest()) // 요청 로그 기록 필터
                .filter(logResponse()) // 응답 로그 기록 필터
                .filter(ExchangeFilterFunction.ofRequestProcessor(request -> { //요청 전처리
                    String originalUrl = request.url().toString(); //요청의 url을 문자열로 변환

                    // URL을 두 번 디코딩하여 중복 인코딩 해결
                    String decodedUrl = URLDecoder.decode(
                            URLDecoder.decode(originalUrl, StandardCharsets.UTF_8),
                            StandardCharsets.UTF_8
                    );

                    log.info("Final Request URL: {}", decodedUrl); //디코딩 된 url 기록

                    return Mono.just(
                            ClientRequest.from(request)
                                    .url(URI.create(decodedUrl))
                                    .build()
                    ); // 최종 url로 요청 재구성
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