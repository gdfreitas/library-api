package com.library.app.book.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.library.app.author.model.Author;
import com.library.app.book.model.Book;
import com.library.app.category.model.Category;
import com.library.app.common.json.JsonReader;
import com.library.app.common.json.JsonWriter;
import com.library.app.common.model.HttpCode;
import com.library.app.commontests.utils.*;
import com.library.app.logaudit.model.LogAudit;
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

import static com.library.app.commontests.book.BookForTestsRepository.*;
import static com.library.app.commontests.logaudit.LogAuditTestUtils.assertAuditLogs;
import static com.library.app.commontests.user.UserForTestsRepository.admin;
import static com.library.app.commontests.user.UserForTestsRepository.johnDoe;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileResponse;
import static com.library.app.commontests.utils.JsonTestUtils.assertJsonMatchesFileContent;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author gabriel.freitas
 */
@RunWith(Arquillian.class)
public class BookResourceIntTest {

    private static final String PATH_RESOURCE = ResourceDefinitions.BOOK.getResourceName();
    private static final String ELEMENT_NAME = Book.class.getSimpleName();

    @ArquillianResource
    private URL deploymentUrl;

    private ResourceClient resourceClient;

    @Deployment
    public static WebArchive createDeployment() {
        return ArquillianTestUtils.createDeploymentArchive();
    }

    @Before
    public void initTestCase() {
        resourceClient = new ResourceClient(deploymentUrl);

        resourceClient.resourcePath("DB/").delete();

        resourceClient.resourcePath("DB/" + ResourceDefinitions.USER.getResourceName()).postWithContent("");
        resourceClient.resourcePath("DB/" + ResourceDefinitions.CATEGORY.getResourceName()).postWithContent("");
        resourceClient.resourcePath("DB/" + ResourceDefinitions.AUTHOR.getResourceName()).postWithContent("");

        resourceClient.user(admin());
    }

    @Test
    @RunAsClient
    public void addValidBookAndFindIt() {
        final Long bookId = addBookAndGetId(normalizeDependenciesWithRest(designPatterns()));
        findBookAndAssertResponseWithBook(bookId, designPatterns());

        assertAuditLogs(resourceClient, 1, new LogAudit(admin(), LogAudit.Action.ADD, ELEMENT_NAME));
    }

    @Test
    @RunAsClient
    public void addBookWithNullTitle() {
        final Book book = normalizeDependenciesWithRest(cleanCode());
        book.setTitle(null);
        addBookWithValidationError(book, "bookErrorNullTitle.json");

        assertAuditLogs(resourceClient, 0);
    }

    @Test
    @RunAsClient
    public void addBookWithInexistentCategory() {
        final Book book = normalizeDependenciesWithRest(cleanCode());
        book.getCategory().setId(999L);
        addBookWithValidationError(book, "bookErrorInexistentCategory.json");

        assertAuditLogs(resourceClient, 0);
    }

    @Test
    @RunAsClient
    public void addBookWithInexistentAuthor() {
        final Book book = normalizeDependenciesWithRest(cleanCode());
        book.getAuthors().get(0).setId(999L);
        addBookWithValidationError(book, "bookErrorInexistentAuthor.json");

        assertAuditLogs(resourceClient, 0);
    }

    @Test
    @RunAsClient
    public void updateValidBook() {
        final Long bookId = addBookAndGetId(normalizeDependenciesWithRest(designPatterns()));
        findBookAndAssertResponseWithBook(bookId, designPatterns());

        final Book book = normalizeDependenciesWithRest(designPatterns());
        book.setPrice(10D);
        book.getAuthors().remove(0);

        final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/" + bookId).putWithContent(
                getJsonForBook(book));
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));

        findBookAndAssertResponseWithBook(bookId, book);

        assertAuditLogs(resourceClient, 2, new LogAudit(admin(), LogAudit.Action.ADD, ELEMENT_NAME), new LogAudit(admin(),
                LogAudit.Action.UPDATE, ELEMENT_NAME));
    }

    @Test
    @RunAsClient
    public void updateBookNotFound() {
        final Book book = normalizeDependenciesWithRest(cleanCode());
        final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/" + 999).putWithContent(
                getJsonForBook(book));
        assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));

        assertAuditLogs(resourceClient, 0);
    }

    @Test
    @RunAsClient
    public void findBookNotFound() {
        final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/" + 999).get();
        assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));

        assertAuditLogs(resourceClient, 0);
    }

    @Test
    @RunAsClient
    public void findByFilterPaginatingAndOrderingDescendingByTitle() {
        resourceClient.resourcePath("DB/" + PATH_RESOURCE).postWithContent("");

        // first page
        Response response = resourceClient.resourcePath(PATH_RESOURCE + "?page=0&per_page=3&sort=-title").get();
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertResponseContainsTheBooks(response, 5, refactoring(), peaa(), effectiveJava());

        // second page
        response = resourceClient.resourcePath(PATH_RESOURCE + "?page=1&per_page=3&sort=-title").get();
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertResponseContainsTheBooks(response, 5, designPatterns(), cleanCode());
    }

    @Test
    @RunAsClient
    public void findByFilterWithNoUser() {
        final Response response = resourceClient.user(null).resourcePath(PATH_RESOURCE).get();
        assertThat(response.getStatus(), is(equalTo(HttpCode.UNAUTHORIZED.getCode())));

        assertAuditLogs(resourceClient, 0);
    }

    @Test
    @RunAsClient
    public void findByFilterWithUserCustomer() {
        final Response response = resourceClient.user(johnDoe()).resourcePath(PATH_RESOURCE).get();
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));

        assertAuditLogs(resourceClient, 0);
    }

    @Test
    @RunAsClient
    public void findByIdIdWithUserCustomer() {
        final Response response = resourceClient.user(johnDoe()).resourcePath(PATH_RESOURCE + "/999").get();
        assertThat(response.getStatus(), is(equalTo(HttpCode.FORBIDDEN.getCode())));

        assertAuditLogs(resourceClient, 0);
    }

    private void addBookWithValidationError(final Book bookToAdd, final String responseFileName) {
        final Response response = resourceClient.resourcePath(PATH_RESOURCE).postWithContent(getJsonForBook(bookToAdd));
        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, responseFileName);
    }

    private void assertJsonResponseWithFile(final Response response, final String fileName) {
        assertJsonMatchesFileContent(response.readEntity(String.class), getPathFileResponse(PATH_RESOURCE, fileName));
    }

    private void assertResponseContainsTheBooks(final Response response, final int expectedTotalRecords,
                                                final Book... expectedBooks) {

        final JsonArray booksList = IntTestUtils.assertJsonHasTheNumberOfElementsAndReturnTheEntries(response,
                expectedTotalRecords, expectedBooks.length);

        for (int i = 0; i < expectedBooks.length; i++) {
            final Book expectedBook = expectedBooks[i];
            assertThat(booksList.get(i).getAsJsonObject().get("title").getAsString(),
                    is(equalTo(expectedBook.getTitle())));
        }
    }

    private Long addBookAndGetId(final Book book) {
        return IntTestUtils.addElementWithContentAndGetId(resourceClient, PATH_RESOURCE, getJsonForBook(book));
    }

    private String getJsonForBook(final Book book) {
        final JsonObject bookJson = new JsonObject();
        bookJson.addProperty("title", book.getTitle());
        bookJson.addProperty("description", book.getDescription());
        bookJson.addProperty("categoryId", book.getCategory().getId());

        final JsonArray authorsIds = new JsonArray();
        for (final Author author : book.getAuthors()) {
            authorsIds.add(new JsonPrimitive(author.getId()));
        }
        bookJson.add("authorsIds", authorsIds);
        bookJson.addProperty("price", book.getPrice());
        return JsonWriter.writeToString(bookJson);
    }

    private Book normalizeDependenciesWithRest(final Book book) {
        book.getCategory().setId(loadCategoryFromRest(book.getCategory()).getId());
        for (final Author author : book.getAuthors()) {
            author.setId(loadAuthorFromRest(author).getId());
        }
        return book;
    }

    private Category loadCategoryFromRest(final Category category) {
        final Response response = resourceClient.resourcePath(
                "DB/" + ResourceDefinitions.CATEGORY.getResourceName() + "/" + category.getName()).get();
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));

        final String bodyResponse = response.readEntity(String.class);
        return new Category(JsonTestUtils.getIdFromJson(bodyResponse));
    }

    private Author loadAuthorFromRest(final Author author) {
        final Response response = resourceClient.resourcePath(
                "DB/" + ResourceDefinitions.AUTHOR.getResourceName() + "/" + author.getName()).get();
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));

        final String bodyResponse = response.readEntity(String.class);
        return new Author(JsonTestUtils.getIdFromJson(bodyResponse));
    }

    private void findBookAndAssertResponseWithBook(final Long bookIdToBeFound, final Book expectedBook) {
        final String bodyResponse = IntTestUtils.findById(resourceClient, PATH_RESOURCE, bookIdToBeFound);

        final JsonObject bookJson = JsonReader.readAsJsonObject(bodyResponse);
        assertThat(bookJson.get("id").getAsLong(), is(notNullValue()));
        assertThat(bookJson.get("title").getAsString(), is(equalTo(expectedBook.getTitle())));
        assertThat(bookJson.get("description").getAsString(), is(equalTo(expectedBook.getDescription())));
        assertThat(bookJson.getAsJsonObject("category").get("name").getAsString(), is(equalTo(expectedBook
                .getCategory().getName())));

        final JsonArray authors = bookJson.getAsJsonArray("authors");
        assertThat(authors.size(), is(equalTo(expectedBook.getAuthors().size())));
        for (int i = 0; i < authors.size(); i++) {
            final String actualAuthorName = authors.get(i).getAsJsonObject().get("name").getAsString();
            final String expectedAuthorName = expectedBook.getAuthors().get(i).getName();
            assertThat(actualAuthorName, is(equalTo(expectedAuthorName)));
        }

        assertThat(bookJson.get("price").getAsDouble(), is(equalTo(expectedBook.getPrice())));
    }

}