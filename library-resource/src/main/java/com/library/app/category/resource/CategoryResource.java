package com.library.app.category.resource;

import com.google.gson.JsonElement;
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
import com.library.app.common.model.PaginatedData;
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

    private static final ResourceMessage RESOURCE_MESSAGE = new ResourceMessage("category");

    @Inject
    CategoryServices categoryServices;

    @Inject
    CategoryJsonConverter categoryJsonConverter;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @POST
    public Response add(final String body) {
        logger.debug("Adding a new category with body {}", body);
        Category category = categoryJsonConverter.convertFrom(body);

        HttpStatusCode httpCode = HttpStatusCode.CREATED;
        OperationResult result;
        try {
            category = categoryServices.add(category);
            result = OperationResult.success(JsonUtils.getJsonElementWithId(category.getId()));
        } catch (final FieldNotValidException e) {
            logger.error("One of the fields of the category is not valid", e);
            httpCode = HttpStatusCode.UNPROCESSABLE_ENTITY;
            result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);
        } catch (final CategoryExistentException e) {
            logger.error("There's already a category for the given name", e);
            httpCode = HttpStatusCode.UNPROCESSABLE_ENTITY;
            result = getOperationResultExistent(RESOURCE_MESSAGE, "name");
        }

        logger.debug("Returning the operation result after adding category: {}", result);
        return Response.status(httpCode.getStatusCode()).entity(OperationResultJsonWriter.toJson(result)).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") final Long id, final String body) {
        logger.debug("Updating the category {} with body {}", id, body);
        final Category category = categoryJsonConverter.convertFrom(body);
        category.setId(id);

        HttpStatusCode httpCode = HttpStatusCode.OK;
        OperationResult result;
        try {
            categoryServices.update(category);
            result = OperationResult.success();
        } catch (final FieldNotValidException e) {
            logger.error("One of the field of the category is not valid", e);
            httpCode = HttpStatusCode.UNPROCESSABLE_ENTITY;
            result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);
        } catch (final CategoryExistentException e) {
            logger.error("There is already a category for the given name", e);
            httpCode = HttpStatusCode.UNPROCESSABLE_ENTITY;
            result = getOperationResultExistent(RESOURCE_MESSAGE, "name");
        } catch (final CategoryNotFoundException e) {
            logger.error("No category found for the given id", e);
            httpCode = HttpStatusCode.NOT_FOUND;
            result = getOperationResultNotFound(RESOURCE_MESSAGE);
        }

        logger.debug("Returning the operation result after updating category: {}", result);
        return Response.status(httpCode.getStatusCode()).entity(OperationResultJsonWriter.toJson(result)).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") final Long id) {
        logger.debug("Find category: {}", id);
        Response.ResponseBuilder responseBuilder;
        try {
            final Category category = categoryServices.findById(id);
            final OperationResult result = OperationResult
                    .success(categoryJsonConverter.convertToJsonElement(category));
            responseBuilder = Response.status(HttpStatusCode.OK.getStatusCode()).entity(OperationResultJsonWriter.toJson(result));
            logger.debug("Category found: {}", category);
        } catch (final CategoryNotFoundException e) {
            logger.error("No category found for id", id);
            responseBuilder = Response.status(HttpStatusCode.NOT_FOUND.getStatusCode());
        }

        return responseBuilder.build();
    }

    @GET
    public Response findAll() {
        logger.debug("Find all categories");

        final List<Category> categories = categoryServices.findAll();

        logger.debug("Found {} categories", categories.size());

        final JsonElement jsonWithPagingAndEntries = JsonUtils.getJsonElementWithPagingAndEntries(
                new PaginatedData<Category>(categories.size(), categories), categoryJsonConverter
        );

        return Response.status(HttpStatusCode.OK.getStatusCode()).entity(JsonWriter.writeToString(jsonWithPagingAndEntries))
                .build();
    }

}