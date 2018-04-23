package com.library.app.commontests.utils;

import org.junit.Ignore;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @author gabriel.freitas
 */
@Ignore
public class TestBaseRepository {

    protected EntityManager em;
    protected DBCommandTransactionalExecutor dbCommandExecutor;
    private EntityManagerFactory emf;

    protected void initializeTestDB() {
        emf = Persistence.createEntityManagerFactory("libraryPU");
        em = emf.createEntityManager();

        dbCommandExecutor = new DBCommandTransactionalExecutor(em);
    }

    protected void closeEntityManager() {
        em.close();
        emf.close();
    }

}