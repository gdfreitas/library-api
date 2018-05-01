package com.library.app.commontests.utils;

import org.junit.Ignore;

/**
 * @author gabriel.freitas
 */
@Ignore
public enum ResourceDefinitions {

    CATEGORY("categories"),
    AUTHOR("authors");

    private String resourceName;

    private ResourceDefinitions(final String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceName() {
        return resourceName;
    }
}