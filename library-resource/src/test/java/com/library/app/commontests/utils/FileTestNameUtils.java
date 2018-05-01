package com.library.app.commontests.utils;

import org.junit.Ignore;

/**
 * @author gabriel.freitas
 */
@Ignore
public class FileTestNameUtils {

    private static final String PATH_REQUEST = "/request/";
    private static final String PATH_RESPONSE = "/response/";

    private FileTestNameUtils() {
    }

    public static String getPathFileRequest(final String mainFolder, final String fileName) {
        return mainFolder + PATH_REQUEST + fileName;
    }

    public static String getPathFileResponse(final String mainFolder, final String fileName) {
        return mainFolder + PATH_RESPONSE + fileName;
    }

}