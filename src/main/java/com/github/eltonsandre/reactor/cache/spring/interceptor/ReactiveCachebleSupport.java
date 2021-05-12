package com.github.eltonsandre.reactor.cache.spring.interceptor;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cache.interceptor.CacheAspectSupport;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.Signal;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.time.Duration;

class ReactiveCachebleSupport {

    public static final String DELIMITER = "::";
    protected final ReactiveRedisTemplate<String, Object> redis;

    public ReactiveCachebleSupport(final ReactiveRedisTemplate<String, Object> redis) {
        super();
        this.redis = redis;
    }

    protected Method getMethod(final ProceedingJoinPoint joinPoint) {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }


    protected Class<?> getCacheType(final Method method) {
        try {
            final ParameterizedType parameterizedType = (ParameterizedType) method.getGenericReturnType();
            return (Class<?>) parameterizedType.getActualTypeArguments()[0];
        } catch (final Exception e) {
            throw new IllegalArgumentException("Invalid return type");
        }
    }

    private String key(final String expressionString, @Nullable final Object rootObject) {
        final Expression expression = new SpelExpressionParser().parseExpression(expressionString);
        final EvaluationContext context = new StandardEvaluationContext(rootObject);
        return (String) expression.getValue(context);
    }

    protected Runnable verifyExpireKey(final String key, final Signal<?> value, final Duration ttl) {
        return ttl == null ? this.expireKey(key, value) : this.expireKey(key, value, ttl);
    }

    private Runnable expireKey(final String key, final Signal<?> value) {
        Assert.notNull(key, "key not null");
        return () -> this.redis.opsForValue()
                .setIfAbsent(key, value.get())
                .subscribe();
    }

    private Runnable expireKey(final String key, final Signal<?> value, final Duration ttl) {
        return () -> this.redis.opsForValue()
                .setIfAbsent(key, value.get(), ttl)
                .subscribe();
    }

}
