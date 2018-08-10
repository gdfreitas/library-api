package com.library.app.logaudit.resource;

import com.library.app.common.resource.AbstractFilterExtractorFromUrl;
import com.library.app.common.utils.DateUtils;
import com.library.app.logaudit.model.filter.LogAuditFilter;

import javax.ws.rs.core.UriInfo;

public class LogAuditFilterExtractorFromUrl extends AbstractFilterExtractorFromUrl {

    public LogAuditFilterExtractorFromUrl(final UriInfo uriInfo) {
        super(uriInfo);
    }

    public LogAuditFilter getFilter() {
        final LogAuditFilter logAuditFilter = new LogAuditFilter();

        logAuditFilter.setPaginationData(extractPaginationData());

        final String startDateStr = getUriInfo().getQueryParameters().getFirst("startDate");
        if (startDateStr != null) {
            logAuditFilter.setStartDate(DateUtils.getAsDateTime(startDateStr));
        }

        final String endDateStr = getUriInfo().getQueryParameters().getFirst("endDate");
        if (endDateStr != null) {
            logAuditFilter.setEndDate(DateUtils.getAsDateTime(endDateStr));
        }

        final String userIdStr = getUriInfo().getQueryParameters().getFirst("userId");
        if (userIdStr != null) {
            logAuditFilter.setUserId(Long.valueOf(userIdStr));
        }

        return logAuditFilter;
    }

    @Override
    protected String getDefaultSortField() {
        return "-createdAt";
    }

}