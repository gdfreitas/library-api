package com.library.app.common.exception;

/**
 * @author gabriel.freitas
 */
public class InvalidJsonException extends RuntimeException {

    public InvalidJsonException(final String message) {
        super(message);
    }

    public InvalidJsonException(final Throwable throwable) {
        super(throwable);
    }

}
