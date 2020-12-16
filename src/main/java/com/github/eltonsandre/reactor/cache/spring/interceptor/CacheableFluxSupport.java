package com.github.eltonsandre.reactor.cache.spring.interceptor;

import com.github.eltonsandre.reactor.cache.spring.annotation.CacheableFlux;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.cache.CacheFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.core.publisher.SignalType;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Configuration
public class CacheableFluxSupport extends ReactiveCachebleSupport {

    @Autowired
    RedisCacheManager redisCacheManager;

    public CacheableFluxSupport(final ReactiveRedisTemplate<String, Object> redis) {
        super(redis);
    }

    @Around("execution(public reactor.core.publisher.Flux *(..)) && " +
            "@annotation(com.github.eltonsandre.reactor.cache.spring.annotation.CacheableFlux)")
    public Flux<Object> cacheableFluxExecution(final ProceedingJoinPoint joinPoint) {
        final Method method = this.getMethod(joinPoint);

        final CacheableFlux annotation = method.getAnnotation(CacheableFlux.class);

        final String cacheName = annotation.cacheName();

        final String key = cacheName + "::" + Arrays.hashCode(joinPoint.getArgs());

        final Class<?> aClass = method.getReturnType();


        return CacheFlux.lookup(k -> this.readList(key), key)
                .onCacheMissResume(() -> this.process(joinPoint))
                .andWriteWith((itKey, itSignals) -> this.writeWith(itKey, itSignals, Duration.parse(annotation.ttl())));

    }

    private Mono<Void> writeWith(final String key, final List<Signal<Object>> itSignals, final Duration ttl) {
        return Mono.fromRunnable(
                () -> this.redis.opsForValue()
                        .setIfAbsent(key, itSignals.stream()
                                .filter(Signal::hasValue)
                                .map(Signal::get)
                                .collect(Collectors.toList()), ttl)
                        .log("[Flux: WriteWith]")
                        .subscribe());
    }

    private Mono<List<Signal<Object>>> readList(final String key) {
        return this.redis.opsForValue()
                .get(key)
                .map(Signal::next)
                .log("[readRedisList:ON_NEXT]", Level.INFO, SignalType.ON_COMPLETE)
                .map(Collections::singletonList);
    }


    @SneakyThrows
    private Flux<Object> process(final ProceedingJoinPoint joinPoint) {
        return ((Flux<Object>) joinPoint.proceed()).log("[Flux: no-cache]", Level.INFO, SignalType.ON_COMPLETE);
    }

}
