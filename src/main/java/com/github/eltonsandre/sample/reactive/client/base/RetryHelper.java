package com.github.eltonsandre.sample.reactive.client.base;

import com.github.eltonsandre.sample.reactive.client.base.exception.ClientExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.function.Predicate;

@Slf4j
public final class RetryHelper {

    private static final String LOG_BUILD_RETRY_BACKOFF = "RetryHelper -> buildRetryBackoff fixedDelay: {} - Retry {} de {} - exception: {}";

    public static Retry buildRetryBackoff(final AbstractClientProperties clientProperties) {
        return buildRetryBackoff(
                clientProperties.getMaxAttempts(),
                clientProperties.getFixedDelay(),
                ClientExceptionHandler::is5xxServerError);
    }

    public static Retry buildRetryBackoff(final AbstractClientProperties clientProperties, Predicate<? super Throwable> errorFilter) {
        return buildRetryBackoff(
                clientProperties.getMaxAttempts(),
                clientProperties.getFixedDelay(),
                errorFilter
        );
    }

    public static RetryBackoffSpec buildRetryBackoff(final long maxAttempts, final Duration fixedDelay, Predicate<? super Throwable> errorFilter) {
        return reactor.util.retry.Retry
                .fixedDelay(maxAttempts, fixedDelay)
                .filter(errorFilter)
                .doAfterRetry(retrySignal ->
                        log.info(LOG_BUILD_RETRY_BACKOFF, fixedDelay.toMillis(), retrySignal.totalRetries(), maxAttempts));
    }

}
