package com.library.app.common.model;

/**
 * @author gabriel.freitas
 */
public enum HttpStatusCode {

    CREATED(201),
    UNPROCESSABLE_ENTITY(422),
    OK(200),
    NOT_FOUND(404);

    private int statusCode;

    private HttpStatusCode(final int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

}
