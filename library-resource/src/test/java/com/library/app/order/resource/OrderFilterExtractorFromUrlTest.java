package com.library.app.order.resource;

import static com.library.app.commontests.utils.FilterExtractorTestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;

import com.library.app.common.model.filter.PaginationData;
import com.library.app.common.model.filter.PaginationData.OrderMode;
import com.library.app.common.utils.DateUtils;
import com.library.app.order.model.Order.OrderStatus;
import com.library.app.order.model.filter.OrderFilter;

/**
 * @author gabriel.freitas
 */
public class OrderFilterExtractorFromUrlTest {

    private UriInfo uriInfo;

    @Before
    public void initTestCase() {
        uriInfo = mock(UriInfo.class);
    }

    @Test
    public void onlyDefaultValues() {
        setUpUriInfo(null, null, null, null, null, null, null);

        final OrderFilterExtractorFromUrl extractor = new OrderFilterExtractorFromUrl(uriInfo);
        final OrderFilter orderFilter = extractor.getFilter();

        assertActualPaginationDataWithExpected(orderFilter.getPaginationData(), new PaginationData(0, 10, "createdAt",
                OrderMode.DESCENDING));
        assertFieldsOnFilter(orderFilter, null, null, null, null);
    }

    @Test
    public void withPaginationAndStartDateAndEndDateAndCustomerIdAndStatusAndSortAscending() {
        setUpUriInfo("2", "5", "2015-01-04T10:10:34Z", "2015-01-05T10:10:34Z", "10", OrderStatus.CANCELLED.name(),
                "createdAt");

        final OrderFilterExtractorFromUrl extractor = new OrderFilterExtractorFromUrl(uriInfo);
        final OrderFilter orderFilter = extractor.getFilter();

        assertActualPaginationDataWithExpected(orderFilter.getPaginationData(), new PaginationData(10, 5, "createdAt",
                OrderMode.ASCENDING));
        assertFieldsOnFilter(orderFilter, DateUtils.getAsDateTime("2015-01-04T10:10:34Z"),
                DateUtils.getAsDateTime("2015-01-05T10:10:34Z"), 10L, OrderStatus.CANCELLED);
    }

    private void setUpUriInfo(final String page, final String perPage, final String startDate, final String endDate,
                              final String customerId, final String status, final String sort) {
        final Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("page", page);
        parameters.put("per_page", perPage);
        parameters.put("startDate", startDate);
        parameters.put("endDate", endDate);
        parameters.put("customerId", customerId);
        parameters.put("status", status);
        parameters.put("sort", sort);

        setUpUriInfoWithMap(uriInfo, parameters);
    }

    private void assertFieldsOnFilter(final OrderFilter orderFilter, final Date startDate, final Date endDate,
                                      final Long customerId,
                                      final OrderStatus status) {
        assertThat(orderFilter.getStartDate(), is(equalTo(startDate)));
        assertThat(orderFilter.getEndDate(), is(equalTo(endDate)));
        assertThat(orderFilter.getCustomerId(), is(equalTo(customerId)));
        assertThat(orderFilter.getStatus(), is(equalTo(status)));
    }

}
