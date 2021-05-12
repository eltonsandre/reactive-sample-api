package com.github.eltonsandre.sample.reactive.client.base;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
public abstract class AbstractClientProperties {

    private String name;
    private String baseUrl;

    private int connectionTimeout = 3000; //default
    private int readTimeout = 3000;  //default
    private int writeTimeout = 3000;  //default

    private long maxAttempts = 0;  //default
    private Duration fixedDelay = java.time.Duration.ofMillis(300);  //default

}
