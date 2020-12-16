package com.github.eltonsandre.sample.reactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@EnableReactiveMongoRepositories
@SpringBootApplication
@ConfigurationPropertiesScan(basePackageClasses = ReactiveApiSampleApplication.class)
public class ReactiveApiSampleApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ReactiveApiSampleApplication.class, args);
    }

}
