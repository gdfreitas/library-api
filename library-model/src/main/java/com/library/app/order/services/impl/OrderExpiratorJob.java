package com.library.app.order.services.impl;

import com.library.app.common.appproperties.PropertyValue;
import com.library.app.order.services.OrderServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

@Singleton
public class OrderExpiratorJob {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private OrderServices orderServices;

    @PropertyValue(name = "days-before-order-expiration")
    private Integer daysBeforeOrderExpiration;

    @Schedule(hour = "*/1", persistent = false)
    public void run() {
        logger.debug("Executing order expirator job");
        orderServices.changeStatusOfExpiredOrders(daysBeforeOrderExpiration);
    }

}
