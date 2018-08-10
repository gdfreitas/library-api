package com.library.app.order.services.impl;

import com.library.app.book.model.Book;
import com.library.app.book.services.BookServices;
import com.library.app.common.exception.UserNotAuthorizedException;
import com.library.app.common.model.PaginatedData;
import com.library.app.common.utils.DateUtils;
import com.library.app.common.utils.ValidationUtils;
import com.library.app.order.exception.OrderNotFoundException;
import com.library.app.order.exception.OrderStatusCannotBeChangedException;
import com.library.app.order.model.Order;
import com.library.app.order.model.Order.OrderStatus;
import com.library.app.order.model.OrderItem;
import com.library.app.order.model.filter.OrderFilter;
import com.library.app.order.repository.OrderRepository;
import com.library.app.order.services.OrderServices;
import com.library.app.user.model.Customer;
import com.library.app.user.model.User;
import com.library.app.user.model.User.Roles;
import com.library.app.user.services.UserServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.Validator;

/**
 * @author gabriel.freitas
 */
@Stateless
public class OrderServicesImpl implements OrderServices {

    @Inject
    OrderRepository orderRepository;

    @Inject
    UserServices userServices;

    @Inject
    BookServices bookServices;

    @Inject
    Validator validator;

    @Resource
    SessionContext sessionContext;

    @Inject
    Event<Order> orderEvent;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Order add(final Order order) {
        checkCustomerAndSetItOnOrder(order);
        checkBooksForItemsAndSetThem(order);

        order.setInitialStatus();
        order.calculateTotal();

        ValidationUtils.validateEntityFields(validator, order);

        sendEvent(order);

        return orderRepository.add(order);
    }

    @Override
    public Order findById(final Long id) {
        final Order order = orderRepository.findById(id);
        if (order == null) {
            throw new OrderNotFoundException();
        }
        return order;
    }

    @Override
    public void updateStatus(final Long id, final OrderStatus newStatus) {
        final Order order = findById(id);

        if (newStatus == OrderStatus.DELIVERED) {
            if (!sessionContext.isCallerInRole(Roles.EMPLOYEE.name())) {
                throw new UserNotAuthorizedException();
            }
        }

        if (newStatus == OrderStatus.CANCELLED) {
            if (sessionContext.isCallerInRole(Roles.CUSTOMER.name())) {
                if (!order.getCustomer().getEmail().equals(sessionContext.getCallerPrincipal().getName())) {
                    throw new UserNotAuthorizedException();
                }
            }
        }

        try {
            order.addHistoryEntry(newStatus);
        } catch (final IllegalArgumentException e) {
            throw new OrderStatusCannotBeChangedException(e.getMessage());
        }

        sendEvent(order);

        orderRepository.update(order);
    }

    @Override
    public PaginatedData<Order> findByFilter(final OrderFilter orderFilter) {
        return orderRepository.findByFilter(orderFilter);
    }

    @Override
    public void changeStatusOfExpiredOrders(final int daysBeforeOrderExpiration) {
        logger.debug("Finding order to be expired that are reserved with more than {} days", daysBeforeOrderExpiration);

        final OrderFilter orderFilter = new OrderFilter();
        orderFilter.setEndDate(DateUtils.currentDatePlusDays(-daysBeforeOrderExpiration));
        orderFilter.setStatus(OrderStatus.RESERVED);

        final PaginatedData<Order> ordersToBeExpired = findByFilter(orderFilter);
        logger.debug("Found {} orders to be expired", ordersToBeExpired.getNumberOfRows());
        for (final Order order : ordersToBeExpired.getRows()) {
            updateStatus(order.getId(), OrderStatus.RESERVATION_EXPIRED);
        }
        logger.debug("Orders expired!");
    }

    private void checkCustomerAndSetItOnOrder(final Order order) {
        final User user = userServices.findByEmail(sessionContext.getCallerPrincipal().getName());
        order.setCustomer((Customer) user);
    }

    private void checkBooksForItemsAndSetThem(final Order order) {
        for (final OrderItem item : order.getItems()) {
            if (item.getBook() != null) {
                final Book book = bookServices.findById(item.getBook().getId());
                item.setBook(book);
            }
        }
    }

    private void sendEvent(Order order) {
        orderEvent.fire(order);
    }

}