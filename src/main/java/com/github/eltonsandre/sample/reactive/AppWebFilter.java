package com.github.eltonsandre.sample.reactive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class AppWebFilter implements WebFilter {

    private static final String MDC_HEADER_PREFIX = "X-MDC-";
    private static final String CONTEXT_MAP = "context-map";


    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange ex, @NonNull WebFilterChain chain) {
        return chain.filter(ex)
                .subscriberContext(ctx -> addRequestHeadersToContext(ex.getRequest(), ctx));
    }

    private Context addRequestHeadersToContext(final ServerHttpRequest request, final Context context) {

        final Map<String, String> contextMap = request.getHeaders()
                .toSingleValueMap()
                .entrySet()
                .stream()
                .filter(x -> x.getKey().startsWith(MDC_HEADER_PREFIX))
                .collect(Collectors.toMap(v -> v.getKey().substring(MDC_HEADER_PREFIX.length()),
                        Map.Entry::getValue));

        return context.put(CONTEXT_MAP, contextMap);
    }


}
