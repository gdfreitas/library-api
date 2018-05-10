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

}
