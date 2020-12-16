package com.github.eltonsandre.sample.reactive.client.base;


import com.github.eltonsandre.sample.reactive.client.company.properties.CompanyClientProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebClientConfiguration {

    public static final String WEB_CLIENT_API_COMPANY_QUALIFIER = "webClientApiCompany";

    private static final String ORIGIN = "x-request-origin";

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.ssl.enabled:false}")
    private boolean sslEnabled;

    private final CompanyClientProperties companyClientProperties;

    @Bean(WEB_CLIENT_API_COMPANY_QUALIFIER)
    public WebClient webClientApiCompany() {
        return this.build(this.companyClientProperties);
    }

    private WebClient build(final AbstractClientProperties clientProperties) {
        return WebClient.builder()
                .clientConnector(this.reactoHttpClientWithConnectAndReadTimeOut(clientProperties))
                .baseUrl(clientProperties.getBaseUrl())
                .defaultHeader(ORIGIN, this.applicationName)
                .filter(this.logRequestFilter(clientProperties.getName()))
                .build();
    }

    private ReactorClientHttpConnector reactoHttpClientWithConnectAndReadTimeOut(final AbstractClientProperties clientProperties) {
        final var httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, clientProperties.getConnectionTimeout())
                .doOnConnected(connection ->
                        connection.addHandlerLast(new ReadTimeoutHandler(clientProperties.getReadTimeout(), TimeUnit.MILLISECONDS)));

        if (this.sslEnabled) {
            log.info("enabled HTTP/2.0 support with TLS");
//            httpClient.protocol(HttpProtocol.H2).secure();
        }

        return new ReactorClientHttpConnector(httpClient);
    }

    private ExchangeFilterFunction logRequestFilter(@NonNull final String client) {
        return ExchangeFilterFunction.ofRequestProcessor(it -> {
            log.info("client: {}, method: {}, uri: {}, headers: {}", client, it.method(), it.url(), it.headers());
            return Mono.just(it);
        });
    }

}
