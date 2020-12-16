package com.github.eltonsandre.sample.reactive.resource.adviceerror;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorWrapper {

    @Singular
    private List<Error> errors;

}