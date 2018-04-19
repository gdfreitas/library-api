package com.library.app.common.model;

/**
 * @author gabriel.freitas
 */
public enum HttpStatusCode {

    CREATED(201),
    UNPROCESSABLE_ENTITY(422);

    private int statusCode;

    private HttpStatusCode(final int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

}
