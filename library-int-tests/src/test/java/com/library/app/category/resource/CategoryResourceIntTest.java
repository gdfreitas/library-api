package com.library.app.category.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.library.app.category.model.Category;
import com.library.app.common.json.JsonReader;
import com.library.app.common.model.HttpCode;
import com.library.app.commontests.utils.ArquillianTestUtils;
import com.library.app.commontests.utils.IntTestUtils;
import com.library.app.commontests.utils.ResourceClient;
import com.library.app.commontests.utils.ResourceDefinitions;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import java.net.URL;

import static com.library.app.commontests.category.CategoryForTestsRepository.*;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileRequest;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileResponse;
import static com.library.app.commontests.utils.JsonTestUtils.assertJsonMatchesFileContent;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author gabriel.freitas
 */
@RunWith(Arquillian.class)
public class CategoryResourceIntTest {

    private static final String PATH_RESOURCE = ResourceDefinitions.CATEGORY.getResourceName();

    @ArquillianResource
    private URL url;

    private ResourceClient resourceClient;

    @Deployment
    public static WebArchive createDeployment() {
        return ArquillianTestUtils.createDeploymentArchive();
    }

    @Before
    public void initTestCase() {
        this.resourceClient = new ResourceClient(url);
        resourceClient.resourcePath("/DB").delete();
    }

    @Test
    @RunAsClient
    public void addValidCategoryAndFindIt() {
        final Long id = addCategoryAndGetId("category.json");
        findCategoryAndAssertResponseWithCategory(id, java());
    }

    @Test
    @RunAsClient
    public void addCategoryWithNullName() {
        final Response response = resourceClient
                .resourcePath(PATH_RESOURCE)
                .postWithFile(getPathFileRequest(PATH_RESOURCE, "categoryWithNullName.json"));

        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, "categoryErrorNullName.json");
    }

    @Test
    @RunAsClient
    public void addExistentCategory() {
        resourceClient
                .resourcePath(PATH_RESOURCE)
                .postWithFile(getPathFileRequest(PATH_RESOURCE, "category.json"));

        final Response response = resourceClient
                .resourcePath(PATH_RESOURCE)
                .postWithFile(getPathFileRequest(PATH_RESOURCE, "category.json"));

        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, "categoryAlreadyExists.json");
    }

    @Test
    @RunAsClient
    public void updateValidCategory() {
        final Long id = addCategoryAndGetId("category.json");
        findCategoryAndAssertResponseWithCategory(id, java());

        final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/" + id)
                .putWithFile(getPathFileRequest(PATH_RESOURCE, "categoryCleanCode.json"));

        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));

        findCategoryAndAssertResponseWithCategory(id, cleanCode());
    }

    @Test
    @RunAsClient
    public void updateCategoryWithNameBelongingToOtherCategory() {
        final Long javaCategoryId = addCategoryAndGetId("category.json");
        addCategoryAndGetId("categoryCleanCode.json");

        final Response response = resourceClient
                .resourcePath(PATH_RESOURCE + "/" + javaCategoryId)
                .putWithFile(getPathFileRequest(PATH_RESOURCE, "categoryCleanCode.json"));

        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, "categoryAlreadyExists.json");
    }

    @Test
    @RunAsClient
    public void updateCategoryNotFound() {
        final Response response = resourceClient
                .resourcePath(PATH_RESOURCE + "/999")
                .putWithFile(getPathFileRequest(PATH_RESOURCE, "category.json"));

        assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
    }

    @Test
    @RunAsClient
    public void findCategoryNotFound() {
        final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/999").get();
        assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
    }

    @Test
    @RunAsClient
    public void findAllCategories() {
        resourceClient.resourcePath("DB/" + PATH_RESOURCE).postWithContent("");

        final Response response = resourceClient
                .resourcePath(PATH_RESOURCE)
                .get();

        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertResponseContainsTheCategories(response, 4, architecture(), cleanCode(), java(), networks());
    }

    private void assertResponseContainsTheCategories(final Response response,
                                                     final int expectedTotalRecords,
                                                     final Category... expectedCategories) {

        final JsonObject result = JsonReader.readAsJsonObject(response.readEntity(String.class));

        final int totalRecords = result.getAsJsonObject("paging").get("totalRecords").getAsInt();
        assertThat(totalRecords, is(equalTo(expectedTotalRecords)));

        final JsonArray categoriesList = result.getAsJsonArray("entries");
        assertThat(categoriesList.size(), is(equalTo(expectedCategories.length)));

        for (int i = 0; i < expectedCategories.length; i++) {
            final Category expectedCategory = expectedCategories[i];
            assertThat(categoriesList.get(i).getAsJsonObject().get("name").getAsString(),
                    is(equalTo(expectedCategory.getName())));
        }

    }

    private Long addCategoryAndGetId(final String fileName) {
        return IntTestUtils.addElementWithFileAndGetId(resourceClient, PATH_RESOURCE, PATH_RESOURCE, fileName);
    }

    private void assertJsonResponseWithFile(final Response response, final String fileName) {
        assertJsonMatchesFileContent(response.readEntity(String.class), getPathFileResponse(PATH_RESOURCE, fileName));
    }

    private void findCategoryAndAssertResponseWithCategory(final Long categoryIdToBeFound, final Category expectedCategory) {
        final String json = IntTestUtils.findById(resourceClient, PATH_RESOURCE, categoryIdToBeFound);
        final JsonObject categoryAsJson = JsonReader.readAsJsonObject(json);

        assertThat(JsonReader.getStringOrNull(categoryAsJson, "name"), is(equalTo(expectedCategory.getName())));
    }

}
