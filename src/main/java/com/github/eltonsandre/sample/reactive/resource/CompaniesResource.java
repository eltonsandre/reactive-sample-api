package com.github.eltonsandre.sample.reactive.resource;

import com.github.eltonsandre.sample.reactive.client.company.CompanyClient;
import com.github.eltonsandre.sample.reactive.model.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author eltonsandre
 */
@RestController
@RequestMapping("companies")
@RequiredArgsConstructor
public class CompaniesResource {

    private final CompanyClient companyClient;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<Company> getAll() {
        return this.companyClient.getCompanies();
    }


}
