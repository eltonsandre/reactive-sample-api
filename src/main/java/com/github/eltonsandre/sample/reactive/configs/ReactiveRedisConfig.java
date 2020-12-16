package com.github.eltonsandre.sample.reactive.configs;

import com.github.eltonsandre.reactor.cache.spring.annotation.EnableReactiveCaching;
import com.github.eltonsandre.sample.reactive.configs.properties.CacheConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@EnableReactiveCaching
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({RedisProperties.class})
public class ReactiveRedisConfig extends CachingConfigurerSupport {

    private final RedisProperties redisProperties;
    private final CacheConfigProperties cacheConfigProperties;

    @Bean
    LettuceClientConfigurationBuilderCustomizer lettuceClientConfigurationBuilderCustomizer(final RedisProperties redisProperties) {
        return builder -> {
            if (this.redisProperties.isSsl()) {
                builder.useSsl().disablePeerVerification().build();
            }
        };
    }

    @Bean
    public ReactiveRedisConnection reactiveRedisConnection(final ReactiveRedisConnectionFactory redisConnectionFactory) {
        return redisConnectionFactory.getReactiveConnection();
    }

    @Bean
    ReactiveStringRedisTemplate reactiveRedisTemplateString(final ReactiveRedisConnectionFactory connectionFactory) {
        return new ReactiveStringRedisTemplate(connectionFactory, RedisSerializationContext.string());
    }

    @Bean
    public ReactiveRedisTemplate<String, Object> redisOperations(final ReactiveRedisConnectionFactory connectionFactory) {
        return new ReactiveRedisTemplate<>(connectionFactory,
                RedisSerializationContext.<String, Object>newSerializationContext(new StringRedisSerializer())
                        .value(new GenericJackson2JsonRedisSerializer()).build());
    }

    @Bean
    RedisCacheManagerBuilderCustomizer ttlCacheManager() {
        return builder -> builder.withInitialCacheConfigurations(this.cacheConfigProperties.getCaches()
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        value -> RedisCacheConfiguration.defaultCacheConfig().entryTtl(value.getValue().getTimeToLive()))));
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {

            @Override
            public void handleCacheGetError(final RuntimeException exception, final Cache cache, final Object key) {
                log.error("exception={}, cache={}, key={}", exception.getMessage(), cache.getName(), key);
            }

            @Override
            public void handleCachePutError(final RuntimeException exception, final Cache cache, final Object key,
                                            final Object value) {
                log.error("exception={}, cache={}, key={}, value={}", exception.getMessage(), cache.getName(), key,
                        value);
            }

            @Override
            public void handleCacheEvictError(final RuntimeException exception, final Cache cache, final Object key) {
                log.error("exception={}, cache={}, key={}", exception.getMessage(), cache.getName(), key);
            }

            @Override
            public void handleCacheClearError(final RuntimeException exception, final Cache cache) {
                log.error("exception={}, cache={}", exception.getMessage(), cache.getName());
            }
        };

    }

}
