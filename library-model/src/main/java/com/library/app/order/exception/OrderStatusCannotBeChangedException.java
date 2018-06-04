package com.library.app.order.exception;

import javax.ejb.ApplicationException;

/**
 * @author gabriel.freitas
 */
@ApplicationException
public class OrderStatusCannotBeChangedException extends RuntimeException {

    public OrderStatusCannotBeChangedException(final String message) {
        super(message);
    }

}
