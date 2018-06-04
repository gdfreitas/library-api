package com.library.app.order.resource;

import static com.library.app.commontests.order.OrderForTestsRepository.*;
import static com.library.app.commontests.order.OrderTestUtils.*;
import static com.library.app.commontests.user.UserForTestsRepository.*;
import static com.library.app.commontests.utils.FileTestNameUtils.*;
import static com.library.app.commontests.utils.JsonTestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.net.URL;

import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.library.app.book.model.Book;
import com.library.app.common.json.JsonReader;
import com.library.app.common.json.JsonWriter;
import com.library.app.common.model.HttpCode;
import com.library.app.commontests.utils.ArquillianTestUtils;
import com.library.app.commontests.utils.IntTestUtils;
import com.library.app.commontests.utils.JsonTestUtils;
import com.library.app.commontests.utils.ResourceClient;
import com.library.app.commontests.utils.ResourceDefinitions;
import com.library.app.order.model.Order;
import com.library.app.order.model.Order.OrderStatus;
import com.library.app.order.model.OrderHistoryEntry;
import com.library.app.order.model.OrderItem;

/**
 * @author gabriel.freitas
 */
@RunWith(Arquillian.class)
public class OrderResourceIntTest {

    private static final String PATH_RESOURCE = ResourceDefinitions.ORDER.getResourceName();

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
        resourceClient.resourcePath("DB/" + ResourceDefinitions.BOOK.getResourceName()).postWithContent("");

        resourceClient.user(admin());
    }

    @Test
    @RunAsClient
    public void addValidOrderAndFindIt() {
        final Long orderId = addOrderAndGetId(normalizeDependenciesWithRest(orderReserved()));
        findOrderAndAssertResponseWithOrder(orderId, orderReserved());
    }

    @Test
    @RunAsClient
    public void addOrderWithInexistentBook() {
        final Order order = orderReserved();
        order.getItems().iterator().next().getBook().setId(999L);

        final Response response = resourceClient.user(johnDoe()).resourcePath(PATH_RESOURCE)
                .postWithContent(getJsonForOrder(order));
        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, "orderErrorInexistentBook.json");
    }

    @Test
    @RunAsClient
    public void addOrderAsAdmin() {
        final Order order = normalizeDependenciesWithRest(orderReserved());

        final Response response = resourceClient.user(admin()).resourcePath(PATH_RESOURCE)
                .postWithContent(getJsonForOrder(order));
        assertThat(response.getStatus(), is(equalTo(HttpCode.FORBIDDEN.getCode())));
    }

    @Test
    @RunAsClient
    public void addStatusDeliveredAsNotEmployee() {
        final Long orderId = addOrderAndGetId(normalizeDependenciesWithRest(orderReserved()));

        final Response response = resourceClient.user(johnDoe())
                .resourcePath(PATH_RESOURCE + "/" + orderId + "/status")
                .postWithContent(getStatusAsJson(OrderStatus.DELIVERED));

        assertThat(response.getStatus(), is(equalTo(HttpCode.FORBIDDEN.getCode())));
    }

    @Test
    @RunAsClient
    public void addStatusDeliveredAsEmployee() {
        final Long orderId = addOrderAndGetId(normalizeDependenciesWithRest(orderReserved()));

        final Response response = resourceClient.user(admin()).resourcePath(PATH_RESOURCE + "/" + orderId + "/status")
                .postWithContent(getStatusAsJson(OrderStatus.DELIVERED));
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));

        final Order expectedOrder = orderReserved();
        expectedOrder.addHistoryEntry(OrderStatus.DELIVERED);
        findOrderAndAssertResponseWithOrder(orderId, expectedOrder);
    }

    @Test
    @RunAsClient
    public void findByIdNotFound() {
        final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/" + 999).get();
        assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
    }

    @Test
    @RunAsClient
    public void findByFilterFilteringByDate() {
        resourceClient.user(johnDoe()).resourcePath("DB/" + PATH_RESOURCE).postWithContent("");

        final Response response = resourceClient.user(admin()).resourcePath(
                PATH_RESOURCE + "?page=0&per_page=3&startDate=2015-01-04T10:10:34Z&endDate=2015-01-05T10:10:30Z").get();
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertResponseContainsTheOrdersStatus(response, 1, OrderStatus.RESERVED);
    }

    private void assertResponseContainsTheOrdersStatus(final Response response, final int expectedTotalRecords,
                                                       final OrderStatus... expectedOrdersStatus) {

        final JsonArray ordersList = IntTestUtils.assertJsonHasTheNumberOfElementsAndReturnTheEntries(response,
                expectedTotalRecords, expectedOrdersStatus.length);

        for (int i = 0; i < expectedOrdersStatus.length; i++) {
            final OrderStatus expectedStatus = expectedOrdersStatus[i];
            assertThat(ordersList.get(i).getAsJsonObject().get("currentStatus").getAsString(),
                    is(equalTo(expectedStatus.name())));
        }
    }

    private Long addOrderAndGetId(final Order order) {
        return IntTestUtils.addElementWithContentAndGetId(resourceClient.user(johnDoe()), PATH_RESOURCE,
                getJsonForOrder(order));
    }

    private String getJsonForOrder(final Order order) {
        final JsonObject orderJson = new JsonObject();

        final JsonArray itemsJson = new JsonArray();
        for (final OrderItem item : order.getItems()) {
            final JsonObject itemJson = new JsonObject();
            itemJson.addProperty("bookId", item.getBook().getId());
            itemJson.addProperty("quantity", item.getQuantity());

            itemsJson.add(itemJson);
        }
        orderJson.add("items", itemsJson);

        return JsonWriter.writeToString(orderJson);
    }

    private Order normalizeDependenciesWithRest(final Order order) {
        for (final OrderItem item : order.getItems()) {
            item.getBook().setId(loadBookIdFromRest(item.getBook()));
        }

        return order;
    }

    private Long loadBookIdFromRest(final Book book) {
        final Response response = resourceClient.resourcePath("DB/" + ResourceDefinitions.BOOK.getResourceName() + "/" + book.getTitle()).get();
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));

        final String bodyResponse = response.readEntity(String.class);
        return JsonTestUtils.getIdFromJson(bodyResponse);
    }

    private void findOrderAndAssertResponseWithOrder(final Long orderIdToBeFound, final Order expectedOrder) {
        final String bodyResponse = IntTestUtils.findById(resourceClient, PATH_RESOURCE, orderIdToBeFound);

        final JsonObject orderJson = JsonReader.readAsJsonObject(bodyResponse);
        assertThat(orderJson.get("id").getAsLong(), is(notNullValue()));
        assertThat(orderJson.get("createdAt").getAsString(), is(notNullValue()));
        assertThat(orderJson.getAsJsonObject("customer").get("name").getAsString(), is(equalTo(expectedOrder
                .getCustomer().getName())));

        final JsonArray itemsJson = orderJson.getAsJsonArray("items");
        assertThat(itemsJson.size(), is(equalTo(expectedOrder.getItems().size())));
        int numberOfItemsChecked = 0;
        for (final OrderItem expectedOrderItem : expectedOrder.getItems()) {
            for (int i = 0; i < itemsJson.size(); i++) {
                final JsonObject actualItemJson = itemsJson.get(i).getAsJsonObject();
                final String actualBookTitle = actualItemJson.getAsJsonObject("book").get("title").getAsString();
                if (actualBookTitle.equals(expectedOrderItem.getBook().getTitle())) {
                    numberOfItemsChecked++;
                    assertThat(actualItemJson.get("quantity").getAsInt(), is(equalTo(expectedOrderItem.getQuantity())));
                    assertThat(actualItemJson.get("price").getAsDouble(), is(equalTo(expectedOrderItem.getPrice())));
                }
            }
        }
        assertThat(numberOfItemsChecked, is(equalTo(expectedOrder.getItems().size())));

        final JsonArray historyEntriesJson = orderJson.getAsJsonArray("historyEntries");
        assertThat(historyEntriesJson.size(), is(equalTo(expectedOrder.getHistoryEntries().size())));
        for (int i = 0; i < historyEntriesJson.size(); i++) {
            final JsonObject actualHistoryEntryJson = historyEntriesJson.get(i).getAsJsonObject();
            final OrderStatus actualHistoryStatus = OrderStatus.valueOf(actualHistoryEntryJson.get("status")
                    .getAsString());
            assertThat(expectedOrder.getHistoryEntries().contains(new OrderHistoryEntry(actualHistoryStatus)),
                    is(equalTo(true)));
        }

        assertThat(orderJson.get("total").getAsDouble(), is(equalTo(expectedOrder.getTotal())));
        assertThat(orderJson.get("currentStatus").getAsString(), is(equalTo(expectedOrder.getCurrentStatus().name())));
    }

    private void assertJsonResponseWithFile(final Response response, final String fileName) {
        assertJsonMatchesFileContent(response.readEntity(String.class), getPathFileResponse(PATH_RESOURCE, fileName));
    }

}
