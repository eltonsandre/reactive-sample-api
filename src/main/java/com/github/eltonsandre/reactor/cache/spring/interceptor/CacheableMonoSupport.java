package com.github.eltonsandre.reactor.cache.spring.interceptor;

import com.github.eltonsandre.reactor.cache.spring.annotation.CacheableMono;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.cache.CacheMono;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.core.publisher.SignalType;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.logging.Level;

@Slf4j
@Aspect
@Configuration
public class CacheableMonoSupport extends ReactiveCachebleSupport {

    public CacheableMonoSupport(final ReactiveRedisTemplate<String, Object> redis) {
        super(redis);
    }

    @Around("execution(public reactor.core.publisher.Mono *(..)) && " +
            "@annotation(com.github.eltonsandre.reactor.cache.spring.annotation.CacheableMono) ")
    public Object cacheableMonoExecution(final ProceedingJoinPoint joinPoint) {
        final Method method = this.getMethod(joinPoint);

        final CacheableMono annotation = method.getAnnotation(CacheableMono.class);

        final String name = annotation.cacheName();
        final String key = name + "::" + Arrays.hashCode(joinPoint.getArgs());

        return CacheMono.lookup(k -> this.redis.opsForValue().get(key)
                .log("[redis] cacheable mono", Level.SEVERE, SignalType.ON_ERROR)
                .map(Signal::next), key)
                .onCacheMissResume(() -> this.process(joinPoint))
                .andWriteWith((k, v) -> Mono.fromRunnable(this.verifyExpireKey(k, v, Duration.parse(annotation.ttl()))));
    }

    @SneakyThrows
    private Mono<Object> process(final ProceedingJoinPoint joinPoint) {
        return (Mono<Object>) joinPoint.proceed();
    }

}
