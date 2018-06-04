package com.library.app.commontests.order;

import com.library.app.order.model.Order;

/**
 * @author gabriel.freitas
 */
public class OrderTestUtils {

    public static String getStatusAsJson(final Order.OrderStatus status) {
        return String.format("{\"status\":\"%s\"}", status);
    }

}
