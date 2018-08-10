package com.library.app.order.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Schedule;
import javax.ejb.Singleton;

@Singleton
public class OrderExpiratorJob {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Schedule(hour = "*/1", persistent = false)
    public void run() {
        logger.debug("Executing order expirator job");

        // TODO: Logic
    }

}
