package com.github.eltonsandre.sample.reactive.service;

import com.github.eltonsandre.sample.reactive.client.company.CompanyClient;
import com.github.eltonsandre.sample.reactive.model.Company;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@DisplayName("Company service")
class CompanyServiceTest {

    @Mock
    private CompanyClient companyClient;

    @InjectMocks
    private CompanyService companyService;

    @Test
    void findProducts() {
        final var company = Company.builder().build();
        Mockito.when(this.companyClient.getCompanies()).thenReturn(Flux.just(company));

        StepVerifier.create(companyService.findProducts())
                .expectSubscription()
                .expectNext(company)
                .verifyComplete();
    }

}
