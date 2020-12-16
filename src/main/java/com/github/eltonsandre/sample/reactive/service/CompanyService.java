package com.github.eltonsandre.sample.reactive.service;

import com.github.eltonsandre.sample.reactive.client.company.CompanyClient;
import com.github.eltonsandre.sample.reactive.model.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyClient companyClient;

    public Flux<Company> findProducts() {
        return this.companyClient.getCompanies();
    }
}
