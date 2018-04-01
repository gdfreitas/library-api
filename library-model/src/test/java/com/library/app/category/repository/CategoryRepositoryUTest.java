package com.library.app.category.repository;

import com.library.app.category.model.Category;
import com.library.app.commontests.utils.DBCommand;
import com.library.app.commontests.utils.DBCommandTransactionalExecutor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static com.library.app.commontests.category.CategoryForTestsRepository.java;
import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * @author gabriel.freitas
 */
public class CategoryRepositoryUTest {

    private EntityManagerFactory emFactory;
    private EntityManager em;
    private CategoryRepository categoryRepository;
    private DBCommandTransactionalExecutor dbCommandTransactionalExecutor;

    @Before
    public void createEntityManager() {
        emFactory = Persistence.createEntityManagerFactory("libraryPU");
        em = emFactory.createEntityManager();

        categoryRepository = new CategoryRepository();
        categoryRepository.em = em;

        dbCommandTransactionalExecutor = new DBCommandTransactionalExecutor(em);
    }

    @After
    public void closeEntityManager() {
        em.close();
        emFactory.close();
    }

    @Test
    public void addCategoryAndFindIt() {
        final Long categoryAddedId = dbCommandTransactionalExecutor.execute(() -> categoryRepository.add(java()).getId());

        assertThat(categoryAddedId, is(notNullValue()));

        final Category category = categoryRepository.findById(categoryAddedId);
        assertThat(category, is(notNullValue()));
        assertThat(category.getName(), is(equalTo(java().getName())));
    }

}
