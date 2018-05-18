package com.library.app.commontests.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;

/**
 * @author gabriel.freitas
 */
public class HttpBasicAuthenticator implements ClientRequestFilter {

    private final String user;
    private final String password;

    public HttpBasicAuthenticator(final String user, final String password) {
        this.user = user;
        this.password = password;
    }

    @Override
    public void filter(final ClientRequestContext requestContext) throws IOException {
        final MultivaluedMap<String, Object> headers = requestContext.getHeaders();
        headers.add("Authorization", getBasicAuthentication());
    }

    private String getBasicAuthentication() {
        final String userAndPassword = this.user + ":" + this.password;
        try {
            return "Basic " + Base64.getMimeEncoder().encodeToString(userAndPassword.getBytes("UTF-8"));
        } catch (final UnsupportedEncodingException e) {
            throw new IllegalStateException("Error while converting using UTF-8", e);
        }
    }

}
