package com.library.app.author.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.library.app.author.model.Author;
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

import static com.library.app.commontests.author.AuthorForTestsRepository.*;
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
public class AuthorResourceIntTest {

    private static final String PATH_RESOURCE = ResourceDefinitions.AUTHOR.getResourceName();
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
    public void addValidAuthorAndFindIt() {
        final Long authorId = addAuthorAndGetId("robertMartin.json");
        findAuthorAndAssertResponseWithAuthor(authorId, robertMartin());
    }

    @Test
    @RunAsClient
    public void addAuthorWithNullName() {
        final Response response = resourceClient.resourcePath(PATH_RESOURCE).postWithFile(
                getPathFileRequest(PATH_RESOURCE, "authorWithNullName.json"));

        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, "authorErrorNullName.json");
    }

    @Test
    @RunAsClient
    public void updateValidAuthor() {
        final Long authorId = addAuthorAndGetId("robertMartin.json");
        findAuthorAndAssertResponseWithAuthor(authorId, robertMartin());

        final Response responseUpdate = resourceClient.resourcePath(PATH_RESOURCE + "/" + authorId).putWithFile(
                getPathFileRequest(PATH_RESOURCE, "uncleBob.json"));
        assertThat(responseUpdate.getStatus(), is(equalTo(HttpCode.OK.getCode())));

        final Author uncleBob = new Author();
        uncleBob.setName("Uncle Bob");
        findAuthorAndAssertResponseWithAuthor(authorId, uncleBob);
    }

    @Test
    @RunAsClient
    public void updateAuthorNotFound() {
        final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/" + 999).putWithFile(
                getPathFileRequest(PATH_RESOURCE, "robertMartin.json"));
        assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
    }

    @Test
    @RunAsClient
    public void findAuthorNotFound() {
        final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/" + 999).get();
        assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
    }

    @Test
    @RunAsClient
    public void findByFilterPaginatingAndOrderingDescendingByName() {
        resourceClient.resourcePath("DB/" + PATH_RESOURCE).postWithContent("");

        // first page
        Response response = resourceClient.resourcePath(PATH_RESOURCE + "?page=0&per_page=10&sort=-name").get();
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertResponseContainsTheAuthors(response, 12, williamOpdyke(), robertMartin(), richardHelm(), ralphJohnson(),
                martinFowler(), kentBeck(), joshuaBloch(), johnVlissides(), johnBrant(), jamesGosling());

        // second page
        response = resourceClient.resourcePath(PATH_RESOURCE + "?page=1&per_page=10&sort=-name").get();
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertResponseContainsTheAuthors(response, 12, erichGamma(), donRoberts());
    }

    private Long addAuthorAndGetId(final String fileName) {
        return IntTestUtils.addElementWithFileAndGetId(resourceClient, PATH_RESOURCE, PATH_RESOURCE, fileName);
    }

    private void findAuthorAndAssertResponseWithAuthor(final Long authorIdToBeFound, final Author expectedAuthor) {
        final String json = IntTestUtils.findById(resourceClient, PATH_RESOURCE, authorIdToBeFound);

        final JsonObject categoryAsJson = JsonReader.readAsJsonObject(json);
        assertThat(JsonReader.getStringOrNull(categoryAsJson, "name"), is(equalTo(expectedAuthor.getName())));
    }

    private void assertJsonResponseWithFile(final Response response, final String fileName) {
        assertJsonMatchesFileContent(response.readEntity(String.class), getPathFileResponse(PATH_RESOURCE, fileName));
    }

    private void assertResponseContainsTheAuthors(final Response response, final int expectedTotalRecords, final Author... expectedAuthors) {

        final JsonArray authorsList = IntTestUtils.assertJsonHasTheNumberOfElementsAndReturnTheEntries(response,
                expectedTotalRecords, expectedAuthors.length);

        for (int i = 0; i < expectedAuthors.length; i++) {
            final Author expectedAuthor = expectedAuthors[i];
            assertThat(authorsList.get(i).getAsJsonObject().get("name").getAsString(),
                    is(equalTo(expectedAuthor.getName())));
        }
    }

}
