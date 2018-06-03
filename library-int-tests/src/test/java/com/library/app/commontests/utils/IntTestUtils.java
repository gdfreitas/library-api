package com.library.app.commontests.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.library.app.common.json.JsonReader;
import com.library.app.common.model.HttpCode;
import org.junit.Ignore;

import javax.ws.rs.core.Response;

import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileRequest;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author gabriel.freitas
 */
@Ignore
public class IntTestUtils {

    public static Long addElementWithFileAndGetId(final ResourceClient resourceClient, final String pathResource,
                                                  final String mainFolder, final String fileName) {
        final Response response = resourceClient.resourcePath(pathResource).postWithFile(
                getPathFileRequest(mainFolder, fileName));
        return assertResponseIsCreatedAndGetId(response);
    }

    public static Long addElementWithContentAndGetId(final ResourceClient resourceClient, final String pathResource,
                                                     final String content) {
        final Response response = resourceClient.resourcePath(pathResource).postWithContent(content);
        return assertResponseIsCreatedAndGetId(response);
    }

    public static String findById(final ResourceClient resourceClient, final String pathResource, final Long id) {
        final Response response = resourceClient.resourcePath(pathResource + "/" + id).get();
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        return response.readEntity(String.class);
    }

    private static Long assertResponseIsCreatedAndGetId(final Response response) {
        assertThat(response.getStatus(), is(equalTo(HttpCode.CREATED.getCode())));
        final Long id = JsonTestUtils.getIdFromJson(response.readEntity(String.class));
        assertThat(id, is(notNullValue()));
        return id;
    }

    public static JsonArray assertJsonHasTheNumberOfElementsAndReturnTheEntries(final Response response,
                                                                                final int expectedTotalRecords, final int expectedEntriesForThisPage) {
        final JsonObject result = JsonReader.readAsJsonObject(response.readEntity(String.class));

        final int totalRecords = result.getAsJsonObject("paging").get("totalRecords").getAsInt();
        assertThat(totalRecords, is(equalTo(expectedTotalRecords)));

        final JsonArray entries = result.getAsJsonArray("entries");
        assertThat(entries.size(), is(equalTo(expectedEntriesForThisPage)));

        return entries;
    }

}
