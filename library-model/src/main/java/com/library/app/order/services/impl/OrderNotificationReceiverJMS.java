package com.library.app.order.services.impl;

import com.library.app.order.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

@MessageDriven(name = "OrderNotificationReceiverJMS", activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/Orders"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
})
public class OrderNotificationReceiverJMS implements MessageListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onMessage(final Message message) {
        try {
            logger.debug("Order notification received from queue {}", message.getBody(Order.class));
        } catch (JMSException e) {
            logger.error("Could not read the body of message");
        }
    }

}
