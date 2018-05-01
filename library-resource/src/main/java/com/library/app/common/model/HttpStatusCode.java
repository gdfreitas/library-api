package com.library.app.common.model;

/**
 * @author gabriel.freitas
 */
public enum HttpStatusCode {

    CREATED(201),
    UNPROCESSABLE_ENTITY(422),
    OK(200),
    NOT_FOUND(404);

    private int code;

    private HttpStatusCode(final int code) {
        this.code = code;
    }

    public int getStatusCode() {
        return code;
    }

}