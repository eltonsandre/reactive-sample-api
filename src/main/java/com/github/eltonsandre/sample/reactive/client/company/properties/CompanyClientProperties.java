package com.github.eltonsandre.sample.reactive.client.company.properties;


import com.github.eltonsandre.sample.reactive.client.base.AbstractClientProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("clients.companies")
public class CompanyClientProperties extends AbstractClientProperties {

    private String pathCompanies;

}
