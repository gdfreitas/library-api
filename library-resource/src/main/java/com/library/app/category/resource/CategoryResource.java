package com.library.app.category.resource;

import com.library.app.category.model.Category;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.json.JsonUtils;
import com.library.app.common.json.OperationResultJsonWriter;
import com.library.app.common.model.OperationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;

/**
 * @author gabriel.freitas
 */
public class CategoryResource {

    protected CategoryServices categoryServices;

    protected CategoryJsonConverter categoryJsonConverter;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public Response add(final String body) {
        logger.debug(String.format("body {%s}", body));

        Category category = categoryJsonConverter.convertFrom(body);

        category = categoryServices.add(category);
        final OperationResult result = OperationResult.success(JsonUtils.getJsonElementWithId(category.getId()));

        logger.debug(String.format("after {%s}", body));

        return Response
                .status(Response.Status.CREATED)
                .entity(OperationResultJsonWriter.toJson(result))
                .build();
    }
}
