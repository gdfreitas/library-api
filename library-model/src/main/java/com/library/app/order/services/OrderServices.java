package com.library.app.order.services;

import javax.ejb.Local;

import com.library.app.book.exception.BookNotFoundException;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.exception.UserNotAuthorizedException;
import com.library.app.common.model.PaginatedData;
import com.library.app.order.exception.OrderNotFoundException;
import com.library.app.order.exception.OrderStatusCannotBeChangedException;
import com.library.app.order.model.Order;
import com.library.app.order.model.Order.OrderStatus;
import com.library.app.order.model.filter.OrderFilter;
import com.library.app.user.exception.UserNotFoundException;

/**
 * @author gabriel.freitas
 */
@Local
public interface OrderServices {

    Order add(Order order) throws UserNotFoundException, BookNotFoundException, FieldNotValidException;

    Order findById(Long id) throws OrderNotFoundException;

    void updateStatus(Long id, OrderStatus newStatus) throws OrderNotFoundException,
            OrderStatusCannotBeChangedException, UserNotAuthorizedException;

    PaginatedData<Order> findByFilter(OrderFilter orderFilter);

    void changeStatusOfExpiredOrders(int daysBeforeOrderExpiration);

}

