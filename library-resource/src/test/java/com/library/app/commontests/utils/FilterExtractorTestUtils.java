package com.library.app.commontests.utils;

import com.library.app.common.model.filter.PaginationData;
import org.junit.Ignore;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.Map;
import java.util.Map.Entry;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * @author gabriel.freitas
 */
@Ignore
public final class FilterExtractorTestUtils {

    private FilterExtractorTestUtils() {
    }

    public static void assertActualPaginationDataWithExpected(final PaginationData actual,
                                                              final PaginationData expected) {

        assertThat(actual.getFirstResult(), is(equalTo(expected.getFirstResult())));
        assertThat(actual.getMaxResults(), is(equalTo(expected.getMaxResults())));
        assertThat(actual.getOrderField(), is(equalTo(expected.getOrderField())));
        assertThat(actual.getOrderMode(), is(equalTo(expected.getOrderMode())));
    }

    @SuppressWarnings("unchecked")
    public static void setUpUriInfoWithMap(final UriInfo uriInfo, final Map<String, String> parameters) {
        final MultivaluedMap<String, String> multiMap = mock(MultivaluedMap.class);

        for (final Entry<String, String> keyValue : parameters.entrySet()) {
            when(multiMap.getFirst(keyValue.getKey())).thenReturn(keyValue.getValue());
        }

        when(uriInfo.getQueryParameters()).thenReturn(multiMap);
    }

}
