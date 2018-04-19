package com.library.app.category.resource;

import com.library.app.category.exception.CategoryExistentException;
import com.library.app.category.model.Category;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.json.JsonUtils;
import com.library.app.common.json.OperationResultJsonWriter;
import com.library.app.common.model.HttpStatusCode;
import com.library.app.common.model.OperationResult;
import com.library.app.common.model.ResourceMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;

import static com.library.app.common.model.StandardsOperationResults.getOperationResultExistent;
import static com.library.app.common.model.StandardsOperationResults.getOperationResultInvalidField;

/**
 * @author gabriel.freitas
 */
public class CategoryResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryResource.class);
    private static final ResourceMessage RESOURCE_MESSAGE = new ResourceMessage("category");

    protected CategoryServices categoryServices;
    protected CategoryJsonConverter categoryJsonConverter;

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
}
