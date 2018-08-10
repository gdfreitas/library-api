package com.library.app.logaudit.resource;

import com.library.app.common.model.filter.PaginationData;
import com.library.app.common.model.filter.PaginationData.OrderMode;
import com.library.app.common.utils.DateUtils;
import com.library.app.logaudit.model.filter.LogAuditFilter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.UriInfo;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.library.app.commontests.utils.FilterExtractorTestUtils.assertActualPaginationDataWithExpected;
import static com.library.app.commontests.utils.FilterExtractorTestUtils.setUpUriInfoWithMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class LogAuditFilterExtractorFromUrlUTest {

    @Mock
    private UriInfo uriInfo;

    @Before
    public void initTestCase() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void onlyDefaultValues() {
        setUpUriInfo(null, null, null, null, null, null);

        final LogAuditFilterExtractorFromUrl extractor = new LogAuditFilterExtractorFromUrl(uriInfo);
        final LogAuditFilter logAuditFilter = extractor.getFilter();

        assertActualPaginationDataWithExpected(logAuditFilter.getPaginationData(), new PaginationData(0, 10,
                "createdAt", OrderMode.DESCENDING));
        assertFieldsOnFilter(logAuditFilter, null, null, null);
    }

    @Test
    public void withPaginationAndStartDateAndEndDateAndCustomerIdAndStatusAndSortAscending() {
        setUpUriInfo("2", "5", "2015-01-04T10:10:34Z", "2015-01-05T10:10:34Z", "10", "createdAt");

        final LogAuditFilterExtractorFromUrl extractor = new LogAuditFilterExtractorFromUrl(uriInfo);
        final LogAuditFilter logAuditFilter = extractor.getFilter();

        assertActualPaginationDataWithExpected(logAuditFilter.getPaginationData(), new PaginationData(10, 5,
                "createdAt", OrderMode.ASCENDING));
        assertFieldsOnFilter(logAuditFilter, DateUtils.getAsDateTime("2015-01-04T10:10:34Z"),
                DateUtils.getAsDateTime("2015-01-05T10:10:34Z"), 10L);
    }

    private void setUpUriInfo(final String page, final String perPage, final String startDate, final String endDate,
                              final String userId, final String sort) {
        final Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("page", page);
        parameters.put("per_page", perPage);
        parameters.put("startDate", startDate);
        parameters.put("endDate", endDate);
        parameters.put("userId", userId);
        parameters.put("sort", sort);

        setUpUriInfoWithMap(uriInfo, parameters);
    }

    private void assertFieldsOnFilter(final LogAuditFilter logAuditFilter, final Date startDate, final Date endDate,
                                      final Long userId) {
        assertThat(logAuditFilter.getStartDate(), is(equalTo(startDate)));
        assertThat(logAuditFilter.getEndDate(), is(equalTo(endDate)));
        assertThat(logAuditFilter.getUserId(), is(equalTo(userId)));
    }

}
