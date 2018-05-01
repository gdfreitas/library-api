package com.library.app.author.resource;

import com.library.app.author.model.filter.AuthorFilter;
import com.library.app.common.model.filter.PaginationData;
import com.library.app.common.model.filter.PaginationData.OrderMode;

import javax.ws.rs.core.UriInfo;

/**
 * @author gabriel.freitas
 */
public class AuthorFilterExtractorFromUrl {
    private UriInfo uriInfo;

    public AuthorFilterExtractorFromUrl(final UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public AuthorFilter getFilter() {
        final AuthorFilter authorFilter = new AuthorFilter();
        authorFilter.setPaginationData(extractPaginationData());
        authorFilter.setName(uriInfo.getQueryParameters().getFirst("name"));

        return authorFilter;
    }

    private PaginationData extractPaginationData() {
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
            return "name";
        }
        return sortField;
    }

    private Integer getPage() {
        final String page = uriInfo.getQueryParameters().getFirst("page");
        if (page == null) {
            return 0;
        }
        return Integer.parseInt(page);
    }

    private Integer getPerPage() {
        final String perPage = uriInfo.getQueryParameters().getFirst("per_page");
        if (perPage == null) {
            return 10;
        }
        return Integer.parseInt(perPage);
    }

}
