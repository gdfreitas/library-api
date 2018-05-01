package com.library.app.common.json;

import com.google.gson.Gson;

/**
 * @author gabriel.freitas
 */
public final class JsonWriter {

    private JsonWriter() {
    }

    public static String writeToString(final Object object) {
        if (object == null) {
            return "";
        }

        return new Gson().toJson(object);
    }

}