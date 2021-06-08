package com.github.eltonsandre.sample.reactive.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Company {

    private String id;
    private String name;
    private String ceoName;

}
