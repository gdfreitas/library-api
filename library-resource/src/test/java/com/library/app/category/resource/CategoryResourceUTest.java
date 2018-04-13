package com.library.app.category.resource;

import com.library.app.category.services.CategoryServices;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;

import static com.library.app.commontests.category.CategoryForTestsRepository.categoryWithId;
import static com.library.app.commontests.category.CategoryForTestsRepository.java;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileRequest;
import static com.library.app.commontests.utils.JsonTestUtils.assertJsonMatchesExpectedJson;
import static com.library.app.commontests.utils.JsonTestUtils.readJsonFile;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author gabriel.freitas
 */
public class CategoryResourceUTest {

    private static final String PATH_RESOURCE = "categories";
    private CategoryResource categoryResource;

    @Mock
    private CategoryServices categoryServices;

    @Before
    public void initTestCase() {
        MockitoAnnotations.initMocks(this);
        categoryResource = new CategoryResource();

        categoryResource.categoryJsonConverter = new CategoryJsonConverter();
        categoryResource.categoryServices = categoryServices;
    }

    @Test
    public void addValidCategory() {
        when(categoryServices.add(java())).thenReturn(categoryWithId(java(), 1L));

        final Response response = categoryResource.add(readJsonFile(getPathFileRequest(PATH_RESOURCE, "newCategory.json")));
        assertThat(response.getStatus(), is(equalTo(Response.Status.CREATED.getStatusCode())));
        assertJsonMatchesExpectedJson(response.getEntity().toString(), "{\"id\": 1}");
    }

}
