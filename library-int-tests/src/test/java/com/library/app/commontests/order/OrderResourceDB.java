package com.library.app.commontests.order;

import static com.library.app.commontests.order.OrderForTestsRepository.*;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.library.app.common.utils.DateUtils;
import com.library.app.order.model.Order;
import com.library.app.order.model.Order.OrderStatus;
import com.library.app.order.services.OrderServices;

/**
 * @author gabriel.freitas
 */
@Path("/DB/orders")
@Produces(MediaType.APPLICATION_JSON)
public class OrderResourceDB {

    @Inject
    private OrderServices orderServices;

    @PersistenceContext
    private EntityManager em;

    @POST
    public void addAll() throws Exception {
        final Order order1 = normalizeDependencies(orderReserved(), em);
        order1.setCreatedAt(DateUtils.getAsDateTime("2015-01-04T10:10:34Z"));
        orderServices.add(order1);

        final Order order2 = normalizeDependencies(orderReserved(), em);
        order2.setCreatedAt(DateUtils.getAsDateTime("2015-01-05T10:10:34Z"));
        orderServices.add(order2);
        orderServices.updateStatus(order2.getId(), OrderStatus.CANCELLED);
    }

}