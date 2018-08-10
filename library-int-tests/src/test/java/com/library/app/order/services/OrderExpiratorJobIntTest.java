package com.library.app.order.services;

import static com.library.app.commontests.author.AuthorForTestsRepository.*;
import static com.library.app.commontests.book.BookForTestsRepository.*;
import static com.library.app.commontests.category.CategoryForTestsRepository.*;
import static com.library.app.commontests.order.OrderForTestsRepository.*;
import static com.library.app.commontests.user.UserForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.library.app.commontests.order.OrderForTestsRepository;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.library.app.common.model.PaginatedData;
import com.library.app.common.utils.DateUtils;
import com.library.app.commontests.utils.ArquillianTestUtils;
import com.library.app.commontests.utils.TestRepositoryEJB;
import com.library.app.order.model.Order;
import com.library.app.order.model.filter.OrderFilter;
import com.library.app.order.services.impl.OrderExpiratorJob;

@RunWith(Arquillian.class)
public class OrderExpiratorJobIntTest {

    @Inject
    private OrderExpiratorJob orderExpiratorJob;

    @Inject
    private OrderServices orderServices;

    @Inject
    private TestRepositoryEJB testRepositoryEJB;

    @PersistenceContext
    private EntityManager em;

    @Deployment
    public static WebArchive createDeployment() {
        return ArquillianTestUtils.createDeploymentArchive();
    }

    @Test
    @InSequence(1)
    public void prepareOrderForTest() {
        testRepositoryEJB.deleteAll();

        allCategories().forEach(testRepositoryEJB::add);
        allAuthors().forEach(testRepositoryEJB::add);
        allBooks().forEach((book) -> testRepositoryEJB.add(normalizeDependencies(book, em)));
        allUsers().forEach(testRepositoryEJB::add);

        final Order orderReservedToBeExpired = orderReserved();
        orderReservedToBeExpired.setCreatedAt(DateUtils.currentDatePlusDays(-8));

        final Order orderReserved = orderReserved();

        testRepositoryEJB.add(OrderForTestsRepository.normalizeDependencies(orderReservedToBeExpired, em));
        testRepositoryEJB.add(OrderForTestsRepository.normalizeDependencies(orderReserved, em));
    }

    @Test
    @InSequence(2)
    public void runJob() {
        assertNumberOfOrdersWithStatus(2, Order.OrderStatus.RESERVED);
        assertNumberOfOrdersWithStatus(0, Order.OrderStatus.RESERVATION_EXPIRED);

        orderExpiratorJob.run();

        assertNumberOfOrdersWithStatus(1, Order.OrderStatus.RESERVED);
        assertNumberOfOrdersWithStatus(1, Order.OrderStatus.RESERVATION_EXPIRED);
    }

    private void assertNumberOfOrdersWithStatus(final int expectedTotalRecords, final Order.OrderStatus status) {
        final OrderFilter orderFilter = new OrderFilter();
        orderFilter.setStatus(status);

        final PaginatedData<Order> orders = orderServices.findByFilter(orderFilter);

        assertThat(orders.getNumberOfRows(), is(equalTo(expectedTotalRecords)));
    }

}