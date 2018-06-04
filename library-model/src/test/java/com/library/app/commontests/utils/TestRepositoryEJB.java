package com.library.app.commontests.utils;

import com.library.app.author.model.Author;
import com.library.app.book.model.Book;
import com.library.app.category.model.Category;
import com.library.app.order.model.Order;
import com.library.app.user.model.User;
import org.junit.Ignore;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;

/**
 * @author gabriel.freitas
 */
@Ignore
@Stateless
public class TestRepositoryEJB {

    private static final List<Class<?>> ENTITIES_TO_REMOVE = Arrays.asList(
            Order.class,
            Book.class,
            User.class,
            Category.class,
            Author.class
    );

    @PersistenceContext
    private EntityManager em;

    public void deleteAll() {
        for (final Class<?> entityClass : ENTITIES_TO_REMOVE) {
            deleteAllForEntity(entityClass);
        }
    }

    @SuppressWarnings("unchecked")
    private void deleteAllForEntity(final Class<?> entityClass) {
        final List<Object> rows = em.createQuery("Select e From " + entityClass.getSimpleName() + " e").getResultList();
        for (final Object row : rows) {
            em.remove(row);
        }
    }

}
