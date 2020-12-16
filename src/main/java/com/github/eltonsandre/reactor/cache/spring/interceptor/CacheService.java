package com.github.eltonsandre.reactor.cache.spring.interceptor;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.cache.CacheMono;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class CacheService {

    public static final String DELIMITER = "::";
    private final ReactiveRedisOperations<String, Object> redisOps;

    public <T> Mono<T> getFromCacheOrSupplier(final String key, final Class<T> clazz, final Supplier<Mono<T>> orElseGet) {
        return CacheMono.lookup(k ->
                this.redisOps.opsForValue()
                        .get(this.buildKey(key, clazz))
                        .map(w -> Signal.next(clazz.cast(w))), key)
                .onCacheMissResume(orElseGet)
                .andWriteWith((k, v) ->
                        Mono.fromRunnable(() -> this.redisOps.opsForValue()
                                .setIfAbsent(this.buildKey(k, clazz), v.get())
                                .subscribe()));
    }

    private <T> String buildKey(final String key, final Class<T> clazz) {
        return String.join(DELIMITER, clazz.getName(), key);
    }

}
