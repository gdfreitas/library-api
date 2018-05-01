package com.library.app.author.resource;

import com.google.gson.JsonElement;
import com.library.app.author.exception.AuthorNotFoundException;
import com.library.app.author.model.Author;
import com.library.app.author.model.filter.AuthorFilter;
import com.library.app.author.services.AuthorServices;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.json.JsonUtils;
import com.library.app.common.json.JsonWriter;
import com.library.app.common.json.OperationResultJsonWriter;
import com.library.app.common.model.HttpStatusCode;
import com.library.app.common.model.OperationResult;
import com.library.app.common.model.PaginatedData;
import com.library.app.common.model.ResourceMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import static com.library.app.common.model.StandardsOperationResults.getOperationResultInvalidField;
import static com.library.app.common.model.StandardsOperationResults.getOperationResultNotFound;

/**
 * @author gabriel.freitas
 */
@Path("/authors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthorResource {

    private static final ResourceMessage RESOURCE_MESSAGE = new ResourceMessage("author");

    @Inject
    AuthorServices authorServices;

    @Inject
    AuthorJsonConverter authorJsonConverter;

    @Context
    UriInfo uriInfo;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @POST
    public Response add(final String body) {
        logger.debug("Adding a new author with body {}", body);
        Author author = authorJsonConverter.convertFrom(body);

        HttpStatusCode httpCode = HttpStatusCode.CREATED;
        OperationResult result;
        try {
            author = authorServices.add(author);
            result = OperationResult.success(JsonUtils.getJsonElementWithId(author.getId()));
        } catch (final FieldNotValidException e) {
            httpCode = HttpStatusCode.UNPROCESSABLE_ENTITY;
            logger.error("One of the fields of the author is not valid", e);
            result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);
        }

        logger.debug("Returning the operation result after adding author: {}", result);
        return Response.status(httpCode.getStatusCode()).entity(OperationResultJsonWriter.toJson(result)).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") final Long id, final String body) {
        logger.debug("Updating the author {} with body {}", id, body);
        final Author author = authorJsonConverter.convertFrom(body);
        author.setId(id);

        HttpStatusCode httpCode = HttpStatusCode.OK;
        OperationResult result;
        try {
            authorServices.update(author);
            result = OperationResult.success();
        } catch (final FieldNotValidException e) {
            httpCode = HttpStatusCode.UNPROCESSABLE_ENTITY;
            logger.error("One of the fields of the author is not valid", e);
            result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);
        } catch (final AuthorNotFoundException e) {
            httpCode = HttpStatusCode.NOT_FOUND;
            logger.error("No author found for the given id", e);
            result = getOperationResultNotFound(RESOURCE_MESSAGE);
        }

        logger.debug("Returning the operation result after updating author: {}", result);
        return Response.status(httpCode.getStatusCode()).entity(OperationResultJsonWriter.toJson(result)).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") final Long id) {
        logger.debug("Find author: {}", id);
        ResponseBuilder responseBuilder;
        try {
            final Author author = authorServices.findById(id);
            final OperationResult result = OperationResult.success(authorJsonConverter.convertToJsonElement(author));
            responseBuilder = Response.status(HttpStatusCode.OK.getStatusCode()).entity(OperationResultJsonWriter.toJson(result));
            logger.debug("Author found: {}", author);
        } catch (final AuthorNotFoundException e) {
            logger.error("No author found for id", id);
            responseBuilder = Response.status(HttpStatusCode.NOT_FOUND.getStatusCode());
        }

        return responseBuilder.build();
    }

    @GET
    public Response findByFilter() {
        final AuthorFilter authorFilter = new AuthorFilterExtractorFromUrl(uriInfo).getFilter();
        logger.debug("Finding authors using filter: {}", authorFilter);

        final PaginatedData<Author> authors = authorServices.findByFilter(authorFilter);

        logger.debug("Found {} authors", authors.getNumberOfRows());

        final JsonElement jsonWithPagingAndEntries = JsonUtils.getJsonElementWithPagingAndEntries(authors,
                authorJsonConverter);

        return Response.status(HttpStatusCode.OK.getStatusCode()).entity(JsonWriter.writeToString(jsonWithPagingAndEntries))
                .build();
    }

}