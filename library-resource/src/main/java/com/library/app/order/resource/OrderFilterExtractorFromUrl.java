package com.library.app.order.resource;

import javax.ws.rs.core.UriInfo;

import com.library.app.common.resource.AbstractFilterExtractorFromUrl;
import com.library.app.common.utils.DateUtils;
import com.library.app.order.model.Order.OrderStatus;
import com.library.app.order.model.filter.OrderFilter;

/**
 * @author gabriel.freitas
 */
public class OrderFilterExtractorFromUrl extends AbstractFilterExtractorFromUrl {

    public OrderFilterExtractorFromUrl(final UriInfo uriInfo) {
        super(uriInfo);
    }

    public OrderFilter getFilter() {
        final OrderFilter orderFilter = new OrderFilter();

        orderFilter.setPaginationData(extractPaginationData());

        final String startDateStr = getUriInfo().getQueryParameters().getFirst("startDate");
        if (startDateStr != null) {
            orderFilter.setStartDate(DateUtils.getAsDateTime(startDateStr));
        }

        final String endDateStr = getUriInfo().getQueryParameters().getFirst("endDate");
        if (endDateStr != null) {
            orderFilter.setEndDate(DateUtils.getAsDateTime(endDateStr));
        }

        final String customerIdStr = getUriInfo().getQueryParameters().getFirst("customerId");
        if (customerIdStr != null) {
            orderFilter.setCustomerId(Long.valueOf(customerIdStr));
        }

        final String status = getUriInfo().getQueryParameters().getFirst("status");
        if (status != null) {
            orderFilter.setStatus(OrderStatus.valueOf(status));
        }

        return orderFilter;
    }

    @Override
    protected String getDefaultSortField() {
        return "-createdAt";
    }

}
