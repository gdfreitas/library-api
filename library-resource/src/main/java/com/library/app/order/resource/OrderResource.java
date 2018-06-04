package com.library.app.order.resource;

import static com.library.app.common.model.StandardsOperationResults.*;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.library.app.book.exception.BookNotFoundException;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.json.JsonReader;
import com.library.app.common.json.JsonUtils;
import com.library.app.common.json.JsonWriter;
import com.library.app.common.json.OperationResultJsonWriter;
import com.library.app.common.model.HttpCode;
import com.library.app.common.model.OperationResult;
import com.library.app.common.model.PaginatedData;
import com.library.app.common.model.ResourceMessage;
import com.library.app.order.exception.OrderNotFoundException;
import com.library.app.order.exception.OrderStatusCannotBeChangedException;
import com.library.app.order.model.Order;
import com.library.app.order.model.Order.OrderStatus;
import com.library.app.order.model.filter.OrderFilter;
import com.library.app.order.services.OrderServices;
import com.library.app.user.exception.UserNotFoundException;

/**
 * @author gabriel.freitas
 */
@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    private static final ResourceMessage RESOURCE_MESSAGE = new ResourceMessage("order");

    @Inject
    OrderServices orderServices;

    @Inject
    OrderJsonConverter orderJsonConverter;

    @Context
    SecurityContext securityContext;

    @Context
    UriInfo uriInfo;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @POST
    @RolesAllowed({"CUSTOMER"})
    public Response add(final String body) {
        logger.debug("Adding a new order with body {}", body);
        Order order = orderJsonConverter.convertFrom(body);

        HttpCode httpCode = HttpCode.CREATED;
        OperationResult result;
        try {
            order = orderServices.add(order);
            result = OperationResult.success(JsonUtils.getJsonElementWithId(order.getId()));
        } catch (final FieldNotValidException e) {
            httpCode = HttpCode.VALIDATION_ERROR;
            logger.error("One of the fields of the order is not valid", e);
            result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);
        } catch (final UserNotFoundException e) {
            httpCode = HttpCode.VALIDATION_ERROR;
            logger.error("Customer not found for order", e);
            result = getOperationResultDependencyNotFound(RESOURCE_MESSAGE, "customer");
        } catch (final BookNotFoundException e) {
            httpCode = HttpCode.VALIDATION_ERROR;
            logger.error("Book not found for order", e);
            result = getOperationResultDependencyNotFound(RESOURCE_MESSAGE, "items[].book");
        }

        logger.debug("Returning the operation result after adding order: {}", result);
        return Response.status(httpCode.getCode()).entity(OperationResultJsonWriter.toJson(result)).build();
    }

    @POST
    @Path("/{id}/status")
    @PermitAll
    public Response addStatus(@PathParam("id") final Long id, final String body) {
        logger.debug("Adding the new status {} for order {}", body, id);

        final OrderStatus status = getStatusFromJson(body);
        if (status == OrderStatus.RESERVATION_EXPIRED) {
            return Response.status(HttpCode.FORBIDDEN.getCode()).build();
        }

        try {
            orderServices.updateStatus(id, status);
        } catch (final OrderNotFoundException e) {
            logger.error("Order {} not found to add a new status", id);
            return Response.status(HttpCode.NOT_FOUND.getCode()).build();
        } catch (final OrderStatusCannotBeChangedException e) {
            logger.error("Error while changing order status {}", e.getMessage());
            return Response.status(HttpCode.VALIDATION_ERROR.getCode()).build();
        }

        return Response.status(HttpCode.OK.getCode()).build();
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public Response findById(@PathParam("id") final Long id) {
        logger.debug("Find order by id: {}", id);
        ResponseBuilder responseBuilder;
        try {
            final Order order = orderServices.findById(id);
            final OperationResult result = OperationResult.success(orderJsonConverter.convertToJsonElement(order));
            responseBuilder = Response.status(HttpCode.OK.getCode()).entity(OperationResultJsonWriter.toJson(result));
            logger.debug("Order found by id: {}", order);
        } catch (final OrderNotFoundException e) {
            logger.error("No order found for id", id);
            responseBuilder = Response.status(HttpCode.NOT_FOUND.getCode());
        }
        return responseBuilder.build();
    }

    @GET
    @RolesAllowed({"EMPLOYEE"})
    public Response findByFilter() {
        final OrderFilter orderFilter = new OrderFilterExtractorFromUrl(uriInfo).getFilter();
        logger.debug("Finding orders using filter: {}", orderFilter);

        final PaginatedData<Order> orders = orderServices.findByFilter(orderFilter);

        logger.debug("Found {} orders", orders.getNumberOfRows());

        final JsonElement jsonWithPagingAndEntries = JsonUtils.getJsonElementWithPagingAndEntries(orders,
                orderJsonConverter);
        return Response.status(HttpCode.OK.getCode()).entity(JsonWriter.writeToString(jsonWithPagingAndEntries))
                .build();
    }

    private OrderStatus getStatusFromJson(final String body) {
        final JsonObject jsonObject = JsonReader.readAsJsonObject(body);
        return OrderStatus.valueOf(JsonReader.getStringOrNull(jsonObject, "status"));
    }

}