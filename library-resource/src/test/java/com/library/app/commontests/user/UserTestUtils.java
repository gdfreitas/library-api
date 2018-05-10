package com.library.app.commontests.user;

/**
 * @author gabriel.freitas
 */
public class UserTestUtils {

    private UserTestUtils() {
    }

    public static String getJsonWithPassword(final String password) {
        return String.format("{\"password\":\"%s\"}", password);
    }

    public static String getJsonWithEmailAndPassword(final String email, final String password) {
        return String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
    }

}
