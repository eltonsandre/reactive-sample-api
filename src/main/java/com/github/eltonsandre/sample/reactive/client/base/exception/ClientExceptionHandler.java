package com.github.eltonsandre.sample.reactive.client.base.exception;


import com.github.eltonsandre.sample.reactive.exception.BadRequestException;
import com.github.eltonsandre.sample.reactive.exception.InternalServerErrorException;
import com.github.eltonsandre.sample.reactive.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
public final class ClientExceptionHandler {

    public static Throwable error(final Throwable throwable) {
        return throwable;
    }

    public static boolean is5xxServerError(final Throwable throwable) {
        return throwable instanceof WebClientResponseException &&
                ((WebClientResponseException) throwable).getStatusCode().is5xxServerError();
    }

    public static Mono<Throwable> error(final ClientResponse clientResponse) {
        return error(clientResponse.statusCode());
    }

    private static Mono<Throwable> error(final HttpStatus status) {
        return Mono.just(status.is4xxClientError() ? error4xx(status) : error5xx(status));
    }

    private static Throwable error4xx(final HttpStatus clientError) {
        Throwable throwable = switch (clientError) {
            case NOT_FOUND -> new NotFoundException();
            default -> new BadRequestException();
        };

        log.error(throwable.getMessage(), throwable);
        return throwable;
    }

    private static Throwable error5xx(final HttpStatus serverError) {
        return new InternalServerErrorException();
    }
}
