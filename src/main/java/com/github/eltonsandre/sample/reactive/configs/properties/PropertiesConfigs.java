package com.github.eltonsandre.sample.reactive.configs.properties;

import com.github.eltonsandre.sample.reactive.client.company.properties.CompanyClientProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan(basePackageClasses = {CompanyClientProperties.class})
public class PropertiesConfigs {
}
