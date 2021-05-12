package com.github.eltonsandre.sample.reactive;


import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

public class ReactiveRequestContextHolder {

    public static final Class<ServerHttpRequest> CONTEXT_KEY = ServerHttpRequest.class;

        //deferContextual(Function) or transformDeferredContextual(BiFunction)
    public static Mono<ServerHttpRequest> getRequest() {
        return Mono.deferContextual(Mono::just)
//        return Mono.subscriberContext()
                .filter(ctx -> {
                    try {
                        ctx.get(CONTEXT_KEY);
                        return true;
                    } catch (java.util.NoSuchElementException e) {
                        return false;
                    }
                })
                .map(ctx -> ctx.get(CONTEXT_KEY))
                .switchIfEmpty(Mono.empty());
    }

}
