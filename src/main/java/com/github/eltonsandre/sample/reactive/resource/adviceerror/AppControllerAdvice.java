package com.github.eltonsandre.sample.reactive.resource.adviceerror;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class AppControllerAdvice {

    private final Map<String, String> messages;
    private static final String PLACEHOLDER_PATTERN = ".*\\{\\d+}.*";

    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ErrorWrapper> handleWebClientResponseException(final WebClientResponseException e) {
        log.error(e.getMessage(), e);
        return this.buildError(e.getStatusCode().toString());
    }

    private Mono<ErrorWrapper> buildError(@NonNull final String... errorCodes) {
        return Mono.just(ErrorWrapper.builder()
                .errors(Stream.of(errorCodes)
                        .map(errorCode -> Error.builder()
                                .code(errorCode)
                                .message(this.errorMessage(errorCode))
                                .build())
                        .collect(Collectors.toList()))
                .build());
    }

    private String errorMessage(@NonNull final String code, final Object... args) {
        final var message = Optional.ofNullable(this.messages.get(code))
                .orElse(StringUtils.EMPTY);

        return Optional.of(message)
                .filter(s -> s.matches(PLACEHOLDER_PATTERN))
                .map(s -> MessageFormat.format(s, args))
                .orElse(message);
    }

//    @ExceptionHandler(IllegalArgumentException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public Mono<ErrorWrapper> handleIllegalArgumentException(final IllegalArgumentException e) {
//        log.error(e.getMessage(), e);
//        return this.buildError(HttpStatus.BAD_REQUEST.toString());
//    }
//
//    @ExceptionHandler(HttpClientErrorException.NotFound.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public Mono<ErrorWrapper> handleNotFound(final HttpClientErrorException.NotFound e) {
//        log.error(e.getMessage(), e);
//        return this.buildError(HttpStatus.NOT_FOUND.toString());
//    }
//
//    @ExceptionHandler({InternalServerErrorException.class})
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public Mono<ErrorWrapper> handleInternalServerErrorException(final Throwable e) {
//        log.error(e.getMessage(), e);
//        return this.buildError(HttpStatus.INTERNAL_SERVER_ERROR.toString());
//    }
}
