package com.library.app.category.resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.library.app.category.exception.CategoryExistentException;
import com.library.app.category.exception.CategoryNotFoundException;
import com.library.app.category.model.Category;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.json.JsonUtils;
import com.library.app.common.json.JsonWriter;
import com.library.app.common.json.OperationResultJsonWriter;
import com.library.app.common.model.HttpStatusCode;
import com.library.app.common.model.OperationResult;
import com.library.app.common.model.ResourceMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static com.library.app.common.model.StandardsOperationResults.*;

/**
 * @author gabriel.freitas
 */
@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryResource.class);
    private static final ResourceMessage RESOURCE_MESSAGE = new ResourceMessage("category");

    @Inject
    protected CategoryServices categoryServices;

    @Inject
    protected CategoryJsonConverter categoryJsonConverter;

    @POST
    public Response add(final String body) {
        LOGGER.debug(String.format("body {%s}", body));

        Category category = categoryJsonConverter.convertFrom(body);

        OperationResult result = null;
        HttpStatusCode status = null;

        try {
            category = categoryServices.add(category);
            result = OperationResult.success(JsonUtils.getJsonElementWithId(category.getId()));
            status = HttpStatusCode.CREATED;
        } catch (final FieldNotValidException ex) {
            LOGGER.error("One of the fields of category is not valid", ex);
            status = HttpStatusCode.UNPROCESSABLE_ENTITY;
            result = getOperationResultInvalidField(RESOURCE_MESSAGE, ex);
        } catch (final CategoryExistentException ex) {
            LOGGER.error("There's already a category for the given name", ex);
            status = HttpStatusCode.UNPROCESSABLE_ENTITY;
            result = getOperationResultExistent(RESOURCE_MESSAGE, "name");
        }

        LOGGER.debug(String.format("after {%s}", body));

        return Response
                .status(status.getStatusCode())
                .entity(OperationResultJsonWriter.toJson(result))
                .build();
    }

    @PUT
    @Path("/{id: [0-9]+}")
    public Response update(final Long id, final String body) {
        LOGGER.debug("Updating the category {} with body {}", id, body);
        final Category category = categoryJsonConverter.convertFrom(body);
        category.setId(id);

        HttpStatusCode status = HttpStatusCode.OK;
        OperationResult result;
        try {
            categoryServices.update(category);
            result = OperationResult.success();
        } catch (final FieldNotValidException e) {
            LOGGER.error("One of the field of the category is not valid", e);
            status = HttpStatusCode.UNPROCESSABLE_ENTITY;
            result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);
        } catch (final CategoryExistentException e) {
            LOGGER.error("There is already a category for the given name", e);
            status = HttpStatusCode.UNPROCESSABLE_ENTITY;
            result = getOperationResultExistent(RESOURCE_MESSAGE, "name");
        } catch (final CategoryNotFoundException e) {
            LOGGER.error("No category found for the given id", e);
            status = HttpStatusCode.NOT_FOUND;
            result = getOperationResultNotFound(RESOURCE_MESSAGE);
        }

        LOGGER.debug("Returning the operation result after updating category: {}", result);
        return Response.status(status.getStatusCode()).entity(OperationResultJsonWriter.toJson(result)).build();
    }

    @GET
    @Path("/{id: [0-9]+}")
    public Response findById(@PathParam("id") final Long id) {
        LOGGER.debug("Find category: {}", id);
        Response.ResponseBuilder responseBuilder;
        try {
            final Category category = categoryServices.findById(id);
            final OperationResult result = OperationResult.success(categoryJsonConverter.convertToJsonElement(category));
            responseBuilder = Response.status(HttpStatusCode.OK.getStatusCode()).entity(OperationResultJsonWriter.toJson(result));
            LOGGER.debug("Category found: {}", category);
        } catch (final CategoryNotFoundException e) {
            LOGGER.error("No category found for id", id);
            responseBuilder = Response.status(HttpStatusCode.NOT_FOUND.getStatusCode());
        }

        return responseBuilder.build();
    }

    @GET
    public Response findAll() {
        LOGGER.debug("Find all categories");

        final List<Category> categories = categoryServices.findAll();

        LOGGER.debug("Found {} categories", categories.size());

        final JsonElement jsonWithPagingAndEntries = getJsonElementWithPagingAndEntries(categories);

        return Response.status(HttpStatusCode.OK.getStatusCode()).entity(JsonWriter.writeToString(jsonWithPagingAndEntries))
                .build();
    }

    private JsonElement getJsonElementWithPagingAndEntries(final List<Category> categories) {
        final JsonObject jsonWithEntriesAndPaging = new JsonObject();

        final JsonObject jsonPaging = new JsonObject();
        jsonPaging.addProperty("totalRecords", categories.size());

        jsonWithEntriesAndPaging.add("paging", jsonPaging);
        jsonWithEntriesAndPaging.add("entries", categoryJsonConverter.convertToJsonElement(categories));

        return jsonWithEntriesAndPaging;
    }
}
