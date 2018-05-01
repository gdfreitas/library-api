package com.library.app.author.resource;

import com.library.app.author.exception.AuthorNotFoundException;
import com.library.app.author.model.Author;
import com.library.app.author.model.filter.AuthorFilter;
import com.library.app.author.services.AuthorServices;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.model.HttpStatusCode;
import com.library.app.common.model.PaginatedData;
import com.library.app.commontests.utils.ResourceDefinitions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Arrays;
import java.util.List;

import static com.library.app.commontests.author.AuthorForTestsRepository.*;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileRequest;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileResponse;
import static com.library.app.commontests.utils.JsonTestUtils.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

/**
 * @author gabriel.freitas
 */
public class AuthorResourceUTest {

    private static final String PATH_RESOURCE = ResourceDefinitions.AUTHOR.getResourceName();

    private AuthorResource authorResource;

    @Mock
    private AuthorServices authorServices;

    @Mock
    private UriInfo uriInfo;

    @Before
    public void initTestCase() {
        MockitoAnnotations.initMocks(this);
        authorResource = new AuthorResource();

        authorResource.authorServices = authorServices;
        authorResource.authorJsonConverter = new AuthorJsonConverter();
        authorResource.uriInfo = uriInfo;
    }

    @Test
    public void addValidAuthor() {
        when(authorServices.add(robertMartin())).thenReturn(authorWithId(robertMartin(), 1L));

        final Response response = authorResource
                .add(readJsonFile(getPathFileRequest(PATH_RESOURCE, "robertMartin.json")));
        assertThat(response.getStatus(), is(equalTo(HttpStatusCode.CREATED.getStatusCode())));
        assertJsonMatchesExpectedJson(response.getEntity().toString(), "{\"id\": 1}");
    }

    @Test
    public void addAuthorWithNullName() throws Exception {
        when(authorServices.add((Author) anyObject())).thenThrow(new FieldNotValidException("name", "may not be null"));

        final Response response = authorResource
                .add(readJsonFile(getPathFileRequest(PATH_RESOURCE, "authorWithNullName.json")));
        assertThat(response.getStatus(), is(equalTo(HttpStatusCode.UNPROCESSABLE_ENTITY.getStatusCode())));
        assertJsonResponseWithFile(response, "authorErrorNullName.json");
    }

    @Test
    public void updateValidAuthor() throws Exception {
        final Response response = authorResource.update(1L,
                readJsonFile(getPathFileRequest(PATH_RESOURCE, "robertMartin.json")));
        assertThat(response.getStatus(), is(equalTo(HttpStatusCode.OK.getStatusCode())));
        assertThat(response.getEntity().toString(), is(equalTo("")));

        verify(authorServices).update(authorWithId(robertMartin(), 1L));
    }

    @Test
    public void updateAuthorWithNullName() throws Exception {
        doThrow(new FieldNotValidException("name", "may not be null")).when(authorServices).update(
                (Author) anyObject());

        final Response response = authorResource.update(1L,
                readJsonFile(getPathFileRequest(PATH_RESOURCE, "authorWithNullName.json")));
        assertThat(response.getStatus(), is(equalTo(HttpStatusCode.UNPROCESSABLE_ENTITY.getStatusCode())));
        assertJsonResponseWithFile(response, "authorErrorNullName.json");
    }

    @Test
    public void updateAuthorNotFound() throws Exception {
        doThrow(new AuthorNotFoundException()).when(authorServices).update(authorWithId(robertMartin(), 2L));

        final Response response = authorResource.update(2L,
                readJsonFile(getPathFileRequest(PATH_RESOURCE, "robertMartin.json")));
        assertThat(response.getStatus(), is(equalTo(HttpStatusCode.NOT_FOUND.getStatusCode())));
    }

    @Test
    public void findAuthor() throws AuthorNotFoundException {
        when(authorServices.findById(1L)).thenReturn(authorWithId(robertMartin(), 1L));

        final Response response = authorResource.findById(1L);
        assertThat(response.getStatus(), is(equalTo(HttpStatusCode.OK.getStatusCode())));
        assertJsonResponseWithFile(response, "robertMartinFound.json");
    }

    @Test
    public void findAuthorNotFound() throws AuthorNotFoundException {
        when(authorServices.findById(1L)).thenThrow(new AuthorNotFoundException());

        final Response response = authorResource.findById(1L);
        assertThat(response.getStatus(), is(equalTo(HttpStatusCode.NOT_FOUND.getStatusCode())));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findByFilterNoFilter() {
        final List<Author> authors = Arrays.asList(authorWithId(erichGamma(), 2L), authorWithId(jamesGosling(), 3L),
                authorWithId(martinFowler(), 4L), authorWithId(robertMartin(), 1L));

        final MultivaluedMap<String, String> multiMap = mock(MultivaluedMap.class);
        when(uriInfo.getQueryParameters()).thenReturn(multiMap);

        when(authorServices.findByFilter((AuthorFilter) anyObject())).thenReturn(
                new PaginatedData<Author>(authors.size(), authors));

        final Response response = authorResource.findByFilter();
        assertThat(response.getStatus(), is(equalTo(HttpStatusCode.OK.getStatusCode())));
        assertJsonResponseWithFile(response, "authorsAllInOnePage.json");
    }

    private void assertJsonResponseWithFile(final Response response, final String fileName) {
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE, fileName));
    }

}