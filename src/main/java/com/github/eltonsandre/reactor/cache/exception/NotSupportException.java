package com.github.eltonsandre.reactor.cache.exception;

/**
 * Not Support Exception
 *
 * @author Minkiu Kim
 */
public class NotSupportException extends RuntimeException {

    /**
     * Constructs a NotSupportException.
     *
     * @param message Message explaining the exception condition
     */
    public NotSupportException(final String message) {
        super(message);
    }
}
