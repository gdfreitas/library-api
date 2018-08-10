package com.library.app.order.services.impl;

import static com.library.app.commontests.book.BookForTestsRepository.*;
import static com.library.app.commontests.order.OrderArgumentMatcher.*;
import static com.library.app.commontests.order.OrderForTestsRepository.*;
import static com.library.app.commontests.user.UserForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.security.Principal;
import java.util.Arrays;

import javax.ejb.SessionContext;
import javax.enterprise.event.Event;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.library.app.book.exception.BookNotFoundException;
import com.library.app.book.services.BookServices;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.exception.UserNotAuthorizedException;
import com.library.app.common.model.PaginatedData;
import com.library.app.order.exception.OrderNotFoundException;
import com.library.app.order.exception.OrderStatusCannotBeChangedException;
import com.library.app.order.model.Order;
import com.library.app.order.model.Order.OrderStatus;
import com.library.app.order.model.filter.OrderFilter;
import com.library.app.order.repository.OrderRepository;
import com.library.app.order.services.OrderServices;
import com.library.app.user.exception.UserNotFoundException;
import com.library.app.user.model.User.Roles;
import com.library.app.user.services.UserServices;

/**
 * @author gabriel.freitas
 */
public class OrderServicesUTest {

    private Validator validator;
    private OrderServices orderServices;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserServices userServices;

    @Mock
    private BookServices bookServices;

    @Mock
    private Event<Order> orderEvent;

    @Mock
    private SessionContext sessionContext;

    private static final String LOGGED_EMAIL = "anyemail@domain.com";

    @Before
    public void initTestCase() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();

        MockitoAnnotations.initMocks(this);

        orderServices = new OrderServicesImpl();

        ((OrderServicesImpl) orderServices).validator = validator;
        ((OrderServicesImpl) orderServices).orderRepository = orderRepository;
        ((OrderServicesImpl) orderServices).userServices = userServices;
        ((OrderServicesImpl) orderServices).bookServices = bookServices;
        ((OrderServicesImpl) orderServices).sessionContext = sessionContext;
        ((OrderServicesImpl) orderServices).orderEvent = orderEvent;

        setUpLoggedEmail(LOGGED_EMAIL, Roles.ADMINISTRATOR);
    }

    @Test(expected = UserNotFoundException.class)
    public void addOrderWithInexistentCustomer() throws Exception {
        when(userServices.findByEmail(LOGGED_EMAIL)).thenThrow(new UserNotFoundException());

        orderServices.add(orderReserved());
    }

    @Test(expected = BookNotFoundException.class)
    public void addOrderWithInexistentBook() {
        when(userServices.findByEmail(LOGGED_EMAIL)).thenReturn(johnDoe());
        when(bookServices.findById(anyLong())).thenThrow(new BookNotFoundException());

        orderServices.add(orderReserved());
    }

    @Test
    public void addOrderWithNullQuantityInOneItem() {
        when(userServices.findByEmail(LOGGED_EMAIL)).thenReturn(johnDoe());
        when(bookServices.findById(1L)).thenReturn(bookWithId(designPatterns(), 1L));
        when(bookServices.findById(2L)).thenReturn(bookWithId(refactoring(), 2L));

        final Order order = orderReserved();
        order.getItems().iterator().next().setQuantity(null);

        addOrderWithInvalidField(order, "items[].quantity");
    }

    @Test
    public void addOrderWithoutItems() throws Exception {
        when(userServices.findByEmail(LOGGED_EMAIL)).thenReturn(johnDoe());

        final Order order = orderReserved();
        order.setItems(null);
        addOrderWithInvalidField(order, "items");
    }

    @Test
    public void addOrderWithNullBookInOneItem() throws Exception {
        when(userServices.findByEmail(LOGGED_EMAIL)).thenReturn(johnDoe());

        final Order order = orderReserved();
        order.getItems().iterator().next().setBook(null);

        addOrderWithInvalidField(order, "items[].book");
    }

    @Test
    public void addValidOrder() {
        when(userServices.findByEmail(LOGGED_EMAIL)).thenReturn(johnDoe());
        when(bookServices.findById(1L)).thenReturn(bookWithId(designPatterns(), 1L));
        when(bookServices.findById(2L)).thenReturn(bookWithId(refactoring(), 2L));
        when(orderRepository.add(orderEq(orderReserved()))).thenReturn(orderWithId(orderReserved(), 1L));

        final Order order = new Order();
        order.setItems(orderReserved().getItems());

        final Long id = orderServices.add(order).getId();
        assertThat(id, is(notNullValue()));
    }

    @Test(expected = OrderNotFoundException.class)
    public void findOrderByIdNotFound() {
        when(orderRepository.findById(1L)).thenReturn(null);

        orderServices.findById(1L);
    }

    @Test
    public void findOrderById() {
        when(orderRepository.findById(1L)).thenReturn(orderWithId(orderReserved(), 1L));

        final Order order = orderServices.findById(1L);
        assertThat(order, is(notNullValue()));
    }

    @Test(expected = OrderNotFoundException.class)
    public void updateStatusOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(null);

        orderServices.updateStatus(1L, OrderStatus.DELIVERED);
    }

    @Test(expected = OrderStatusCannotBeChangedException.class)
    public void updateStatusForSameStatus() {
        when(orderRepository.findById(1L)).thenReturn(orderWithId(orderReserved(), 1L));

        orderServices.updateStatus(1L, OrderStatus.RESERVED);
    }

    @Test(expected = OrderStatusCannotBeChangedException.class)
    public void updateStatusForInvalidStatus() throws Exception {
        when(orderRepository.findById(1L)).thenReturn(orderWithId(orderDelivered(), 1L));

        orderServices.updateStatus(1L, OrderStatus.RESERVED);
    }

    @Test(expected = UserNotAuthorizedException.class)
    public void updateStatusDeliveredAsNotEmployee() throws Exception {
        setUpLoggedEmail(LOGGED_EMAIL, Roles.CUSTOMER);
        when(orderRepository.findById(1L)).thenReturn(orderWithId(orderReserved(), 1L));

        orderServices.updateStatus(1L, OrderStatus.DELIVERED);
    }

    @Test
    public void updateStatusDeliveredAsEmployee() throws Exception {
        setUpLoggedEmail(LOGGED_EMAIL, Roles.EMPLOYEE);
        when(orderRepository.findById(1L)).thenReturn(orderWithId(orderReserved(), 1L));

        orderServices.updateStatus(1L, OrderStatus.DELIVERED);

        final Order expectedOrder = orderWithId(orderReserved(), 1L);
        expectedOrder.addHistoryEntry(OrderStatus.DELIVERED);
        verify(orderRepository).update(orderEq(expectedOrder));
    }

    @Test(expected = UserNotAuthorizedException.class)
    public void updateStatusCancelledAsCustomerNotTheOrderCustomer() throws Exception {
        setUpLoggedEmail(LOGGED_EMAIL, Roles.CUSTOMER);
        when(orderRepository.findById(1L)).thenReturn(orderWithId(orderReserved(), 1L));

        orderServices.updateStatus(1L, OrderStatus.CANCELLED);
    }

    @Test
    public void updateStatusCancelledAsCustomerTheOrderCustomer() throws Exception {
        setUpLoggedEmail(orderReserved().getCustomer().getEmail(), Roles.CUSTOMER);
        when(orderRepository.findById(1L)).thenReturn(orderWithId(orderReserved(), 1L));

        orderServices.updateStatus(1L, OrderStatus.CANCELLED);

        final Order expectedOrder = orderWithId(orderReserved(), 1L);
        expectedOrder.addHistoryEntry(OrderStatus.CANCELLED);
        verify(orderRepository).update(orderEq(expectedOrder));
    }

    @Test
    public void findByFilter() {
        final PaginatedData<Order> orders = new PaginatedData<Order>(2,
                Arrays.asList(orderReserved(), orderDelivered()));
        when(orderRepository.findByFilter((OrderFilter) anyObject())).thenReturn(orders);

        final PaginatedData<Order> ordersReturned = orderServices.findByFilter(new OrderFilter());
        assertThat(ordersReturned.getNumberOfRows(), is(equalTo(2)));
        assertThat(ordersReturned.getRows().size(), is(equalTo(2)));
    }

    private void addOrderWithInvalidField(final Order order, final String invalidField) {
        try {
            orderServices.add(order);
            fail("An error should have been thrown");
        } catch (final FieldNotValidException e) {
            assertThat(e.getFieldName(), is(equalTo(invalidField)));
        }
    }

    private void setUpLoggedEmail(final String email, final Roles userRole) {
        reset(sessionContext);

        final Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(email);

        when(sessionContext.getCallerPrincipal()).thenReturn(principal);
        when(sessionContext.isCallerInRole(Roles.EMPLOYEE.name())).thenReturn(userRole == Roles.EMPLOYEE);
        when(sessionContext.isCallerInRole(Roles.CUSTOMER.name())).thenReturn(userRole == Roles.CUSTOMER);
    }

}
