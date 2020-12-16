package com.github.eltonsandre.sample.reactive.configs.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.time.Duration;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties
public class CacheConfigProperties {

    private Map<String, Config> caches;

    @Value
    @ConstructorBinding
    public static class Config {

        Duration timeToLive;

    }

}
