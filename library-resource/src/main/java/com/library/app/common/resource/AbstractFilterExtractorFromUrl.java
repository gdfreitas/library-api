package com.library.app.common.resource;

import com.library.app.common.model.filter.PaginationData;
import com.library.app.common.model.filter.PaginationData.OrderMode;

import javax.ws.rs.core.UriInfo;

/**
 * @author gabriel.freitas
 */
public abstract class AbstractFilterExtractorFromUrl {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PER_PAGE = 10;
    private UriInfo uriInfo;

    public AbstractFilterExtractorFromUrl(final UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    protected abstract String getDefaultSortField();

    protected UriInfo getUriInfo() {
        return uriInfo;
    }

    protected PaginationData extractPaginationData() {
        final int perPage = getPerPage();
        final int firstResult = getPage() * perPage;

        String orderField;
        OrderMode orderMode;
        final String sortField = getSortField();

        if (sortField.startsWith("+")) {
            orderField = sortField.substring(1);
            orderMode = OrderMode.ASCENDING;
        } else if (sortField.startsWith("-")) {
            orderField = sortField.substring(1);
            orderMode = OrderMode.DESCENDING;
        } else {
            orderField = sortField;
            orderMode = OrderMode.ASCENDING;
        }

        return new PaginationData(firstResult, perPage, orderField, orderMode);
    }

    protected String getSortField() {
        final String sortField = uriInfo.getQueryParameters().getFirst("sort");
        if (sortField == null) {
            return getDefaultSortField();
        }
        return sortField;
    }

    private Integer getPage() {
        final String page = uriInfo.getQueryParameters().getFirst("page");
        if (page == null) {
            return DEFAULT_PAGE;
        }
        return Integer.parseInt(page);
    }

    private Integer getPerPage() {
        final String perPage = uriInfo.getQueryParameters().getFirst("per_page");
        if (perPage == null) {
            return DEFAULT_PER_PAGE;
        }
        return Integer.parseInt(perPage);
    }

}
