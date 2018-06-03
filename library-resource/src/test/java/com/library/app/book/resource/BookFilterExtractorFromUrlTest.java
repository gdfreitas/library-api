package com.library.app.book.resource;

import com.library.app.book.model.filter.BookFilter;
import com.library.app.common.model.filter.PaginationData;
import com.library.app.common.model.filter.PaginationData.OrderMode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.UriInfo;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.library.app.commontests.utils.FilterExtractorTestUtils.assertActualPaginationDataWithExpected;
import static com.library.app.commontests.utils.FilterExtractorTestUtils.setUpUriInfoWithMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BookFilterExtractorFromUrlTest {

    @Mock
    private UriInfo uriInfo;

    @Before
    public void initTestCase() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void onlyDefaultValues() {
        setUpUriInfo(null, null, null, null, null);

        final BookFilterExtractorFromUrl extractor = new BookFilterExtractorFromUrl(uriInfo);
        final BookFilter bookFilter = extractor.getFilter();

        assertActualPaginationDataWithExpected(bookFilter.getPaginationData(), new PaginationData(0, 10, "title",
                OrderMode.ASCENDING));
        assertFieldsOnFilter(bookFilter, null, null);
    }

    @Test
    public void withPaginationAndTitleAndCategoryIdAndSortAscending() {
        setUpUriInfo("2", "5", "Design", "1", "id");

        final BookFilterExtractorFromUrl extractor = new BookFilterExtractorFromUrl(uriInfo);
        final BookFilter bookFilter = extractor.getFilter();

        assertActualPaginationDataWithExpected(bookFilter.getPaginationData(), new PaginationData(10, 5, "id",
                OrderMode.ASCENDING));
        assertFieldsOnFilter(bookFilter, "Design", 1L);
    }

    @Test
    public void withPaginationAndTitleAndSortAscendingWithPrefix() {
        setUpUriInfo("2", "5", "Design", null, "+id");

        final BookFilterExtractorFromUrl extractor = new BookFilterExtractorFromUrl(uriInfo);
        final BookFilter bookFilter = extractor.getFilter();

        assertActualPaginationDataWithExpected(bookFilter.getPaginationData(), new PaginationData(10, 5, "id",
                OrderMode.ASCENDING));
        assertFieldsOnFilter(bookFilter, "Design", null);
    }

    @Test
    public void withPaginationAndTitleAndCategoryIdAndSortDescending() {
        setUpUriInfo("2", "5", "Design", "10", "-id");

        final BookFilterExtractorFromUrl extractor = new BookFilterExtractorFromUrl(uriInfo);
        final BookFilter bookFilter = extractor.getFilter();

        assertActualPaginationDataWithExpected(bookFilter.getPaginationData(), new PaginationData(10, 5, "id",
                OrderMode.DESCENDING));
        assertFieldsOnFilter(bookFilter, "Design", 10L);
    }

    private void setUpUriInfo(final String page, final String perPage, final String title, final String categoryId,
                              final String sort) {
        final Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("page", page);
        parameters.put("per_page", perPage);
        parameters.put("title", title);
        parameters.put("categoryId", categoryId);
        parameters.put("sort", sort);

        setUpUriInfoWithMap(uriInfo, parameters);
    }

    private void assertFieldsOnFilter(final BookFilter bookFilter, final String title, final Long categoryId) {
        assertThat(bookFilter.getTitle(), is(equalTo(title)));
        assertThat(bookFilter.getCategoryId(), is(equalTo(categoryId)));
    }

}