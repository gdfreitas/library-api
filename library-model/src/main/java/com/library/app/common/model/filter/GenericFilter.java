package com.library.app.common.model.filter;

import java.util.Objects;

/**
 * @author gabriel.freitas
 */
public class GenericFilter {

    private PaginationData paginationData;

    public GenericFilter() {
    }

    public GenericFilter(final PaginationData paginationData) {
        this.paginationData = paginationData;
    }

    public PaginationData getPaginationData() {
        return paginationData;
    }

    public void setPaginationData(final PaginationData paginationData) {
        this.paginationData = paginationData;
    }

    public boolean hasPaginationData() {
        return Objects.nonNull(getPaginationData());
    }

    public boolean hasOrderField() {
        return hasPaginationData() && Objects.nonNull(getPaginationData().getOrderField());
    }

    @Override
    public String toString() {
        return "GenericFilter [paginationData=" + paginationData + "]";
    }

}
