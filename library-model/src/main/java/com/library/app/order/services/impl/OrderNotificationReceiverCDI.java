package com.library.app.order.services.impl;

import com.library.app.order.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Asynchronous;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class OrderNotificationReceiverCDI {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private void onReceiveEvent(@Observes Order order) {
        logger.debug("Order notification received from event {}", order);
    }

}