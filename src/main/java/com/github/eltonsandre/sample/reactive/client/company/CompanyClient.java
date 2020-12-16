package com.github.eltonsandre.sample.reactive.client.company;

import com.github.eltonsandre.sample.reactive.client.base.RetryHelper;
import com.github.eltonsandre.sample.reactive.client.base.WebClientConfiguration;
import com.github.eltonsandre.sample.reactive.client.base.exception.ClientExceptionHandler;
import com.github.eltonsandre.sample.reactive.client.company.properties.CompanyClientProperties;
import com.github.eltonsandre.sample.reactive.model.Company;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.logging.Level;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyClient {

    @Qualifier(WebClientConfiguration.WEB_CLIENT_API_COMPANY_QUALIFIER)
    private final WebClient webClient;
    private final CompanyClientProperties companyClientProperties;

    public Flux<Company> getCompanies() {
        return this.webClient
                .get()
                .uri(this.companyClientProperties.getPathCompanies())
                .retrieve()
//                .onStatus(HttpStatus::isError, ClientExceptionHandler::error)
                .bodyToFlux(Company.class)
                .doOnError(ClientExceptionHandler::is5xxServerError, ClientExceptionHandler::error)
                .retryWhen(RetryHelper.buildRetryBackoff(this.companyClientProperties))
                .log("CompanyClient -> getCompanies", Level.INFO);
    }

}
