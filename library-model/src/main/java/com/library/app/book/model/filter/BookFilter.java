package com.library.app.book.model.filter;

import com.library.app.common.model.filter.GenericFilter;

/**
 * @author gabriel.freitas
 */
public class BookFilter extends GenericFilter {

    private String title;
    private Long categoryId;

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(final Long categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "BookFilter [title=" + title + ", categoryId=" + categoryId + ", toString()=" + super.toString() + "]";
    }

}
