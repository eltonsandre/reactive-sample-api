package com.github.eltonsandre.sample.reactive.configs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.netty.http.HttpProtocol;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author eltonsandre
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnExpression("${server.ssl.enabled:false}")
public class HttpToHttpsRedirectConfig {

    @Value("${server.port.http}")
    private int serverPortHttp;

    @Value("${server.port}")
    private int serverPortHttps;

    @PostConstruct
    public void startRedirectServer() {
        new NettyReactiveWebServerFactory(this.serverPortHttp).getWebServer((request, response) -> {
            final URI uri = request.getURI();
            URI httpsUri;

            try {
                httpsUri = new URI("https", uri.getUserInfo(), uri.getHost(), this.serverPortHttps, uri.getPath(),
                        uri.getQuery(), uri.getFragment());
            } catch (final URISyntaxException e) {
                log.error(e.getMessage(), e);
                return Mono.error(e);
            }

            response.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
            response.getHeaders().setLocation(httpsUri);

            return response.setComplete();
        }).start();
    }


    @Bean
    public NettyServerCustomizer serverCustomizer() {
        return server -> server.protocol(HttpProtocol.H2);
    }


}
