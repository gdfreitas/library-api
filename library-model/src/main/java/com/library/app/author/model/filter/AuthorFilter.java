package com.library.app.author.model.filter;

import com.library.app.common.model.filter.GenericFilter;

/**
 * @author gabriel.freitas
 */
public class AuthorFilter extends GenericFilter {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "AuthorFilter [name=" + name + ", toString()=" + super.toString() + "]";
    }

}
