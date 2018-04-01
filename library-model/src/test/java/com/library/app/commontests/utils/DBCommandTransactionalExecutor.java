package com.library.app.commontests.utils;

import org.junit.Ignore;

import javax.persistence.EntityManager;

/**
 * @author gabriel.freitas
 */
@Ignore
public class DBCommandTransactionalExecutor {

    private EntityManager em;

    public DBCommandTransactionalExecutor(EntityManager em) {
        this.em = em;
    }

    public <T> T execute(DBCommand<T> command) {
        try {
            em.getTransaction().begin();
            T result = command.execute();
            em.getTransaction().commit();
            em.clear();
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
            throw new IllegalStateException(e);
        }
    }
}
