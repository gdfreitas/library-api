package com.library.app.order.resource;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.library.app.book.model.Book;
import com.library.app.common.json.EntityJsonConverter;
import com.library.app.common.json.JsonReader;
import com.library.app.common.utils.DateUtils;
import com.library.app.order.model.Order;
import com.library.app.order.model.OrderHistoryEntry;
import com.library.app.order.model.OrderItem;
import com.library.app.user.model.User;

/**
 * @author gabriel.freitas
 */
@ApplicationScoped
public class OrderJsonConverter implements EntityJsonConverter<Order> {

    @Override
    public Order convertFrom(final String json) {
        final JsonObject jsonObject = JsonReader.readAsJsonObject(json);

        final Order order = new Order();
        final JsonArray itemsJsonArray = jsonObject.getAsJsonArray("items");
        if (itemsJsonArray != null) {
            for (final JsonElement itemJsonElement : itemsJsonArray) {
                final Book book = new Book(JsonReader.getLongOrNull(itemJsonElement.getAsJsonObject(), "bookId"));
                final Integer quantity = JsonReader.getIntegerOrNull(itemJsonElement.getAsJsonObject(), "quantity");
                order.addItem(book, quantity);
            }
        }

        return order;
    }

    @Override
    public JsonElement convertToJsonElement(final Order order) {
        return getOrderAsJsonElement(order, true);
    }

    @Override
    public JsonElement convertToJsonElement(final List<Order> orders) {
        final JsonArray jsonArray = new JsonArray();

        for (final Order order : orders) {
            jsonArray.add(getOrderAsJsonElement(order, false));
        }

        return jsonArray;
    }

    private JsonElement getOrderAsJsonElement(final Order order, final boolean addItemsAndHistory) {
        final JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", order.getId());
        jsonObject.addProperty("createdAt", DateUtils.formatDateTime(order.getCreatedAt()));
        jsonObject.add("customer", getCustomerAsJsonElement(order.getCustomer()));

        if (addItemsAndHistory) {
            final JsonArray jsonArrayItems = new JsonArray();
            for (final OrderItem orderItem : order.getItems()) {
                jsonArrayItems.add(getOrderItemAsJsonElement(orderItem));
            }
            jsonObject.add("items", jsonArrayItems);

            final JsonArray jsonArrayHistoryEntries = new JsonArray();
            for (final OrderHistoryEntry orderHistoryEntry : order.getHistoryEntries()) {
                jsonArrayHistoryEntries.add(getHistoryEntryAsJsonElement(orderHistoryEntry));
            }
            jsonObject.add("historyEntries", jsonArrayHistoryEntries);
        }

        jsonObject.addProperty("total", order.getTotal());
        jsonObject.addProperty("currentStatus", order.getCurrentStatus().name());

        return jsonObject;
    }

    private JsonElement getCustomerAsJsonElement(final User user) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", user.getId());
        jsonObject.addProperty("name", user.getName());

        return jsonObject;
    }

    private JsonElement getOrderItemAsJsonElement(final OrderItem orderItem) {
        final JsonObject jsonObject = new JsonObject();

        final JsonObject jsonObjectBook = new JsonObject();
        jsonObjectBook.addProperty("id", orderItem.getBook().getId());
        jsonObjectBook.addProperty("title", orderItem.getBook().getTitle());

        jsonObject.add("book", jsonObjectBook);
        jsonObject.addProperty("quantity", orderItem.getQuantity());
        jsonObject.addProperty("price", orderItem.getPrice());

        return jsonObject;
    }

    private JsonElement getHistoryEntryAsJsonElement(final OrderHistoryEntry orderHistoryEntry) {
        final JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("createdAt", DateUtils.formatDateTime(orderHistoryEntry.getCreatedAt()));
        jsonObject.addProperty("status", orderHistoryEntry.getStatus().name());

        return jsonObject;
    }

}
