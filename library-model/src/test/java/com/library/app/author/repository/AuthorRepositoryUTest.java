package com.library.app.author.repository;

import com.library.app.author.model.Author;
import com.library.app.commontests.utils.DBCommandTransactionalExecutor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static com.library.app.commontests.author.AuthorForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;


/**
 * @author gabriel.freitas
 */
public class AuthorRepositoryUTest {

    private EntityManagerFactory emf;
    private EntityManager em;
    private DBCommandTransactionalExecutor dbCommandExecutor;
    private AuthorRepository authorRepository;

    @Before
    public void initTestCase() {
        emf = Persistence.createEntityManagerFactory("libraryPU");
        em = emf.createEntityManager();

        authorRepository = new AuthorRepository();
        authorRepository.em = em;

        dbCommandExecutor = new DBCommandTransactionalExecutor(em);
    }

    @After
    public void closeEntityManager() {
        em.close();
        emf.close();
    }

    @Test
    public void addAuthorAndFindIt() {
        final Long authorAddedId = dbCommandExecutor.execute(() -> authorRepository.add(robertMartin()).getId());
        assertThat(authorAddedId, is(notNullValue()));

        final Author author = authorRepository.findById(authorAddedId);
        assertThat(author, is(notNullValue()));
        assertThat(author.getName(), is(equalTo(robertMartin().getName())));
    }

    @Test
    public void findAuthorByIdNotFound() {
        final Author author = authorRepository.findById(999L);
        assertThat(author, is(nullValue()));
    }

    @Test
    public void updateAuthor() {
        final Long authorAddedId = dbCommandExecutor.execute(() -> authorRepository.add(robertMartin()).getId());
        assertThat(authorAddedId, is(notNullValue()));

        final Author author = authorRepository.findById(authorAddedId);
        assertThat(author.getName(), is(equalTo(robertMartin().getName())));

        author.setName("Uncle Bob");
        dbCommandExecutor.execute(() -> {
            authorRepository.update(author);
            return null;
        });

        final Author authorAfterUpdate = authorRepository.findById(authorAddedId);
        assertThat(authorAfterUpdate.getName(), is(equalTo("Uncle Bob")));
    }

    @Test
    public void existsById() {
        final Long authorAddedId = dbCommandExecutor.execute(() -> authorRepository.add(robertMartin()).getId());

        assertThat(authorRepository.existsById(authorAddedId), is(equalTo(true)));
        assertThat(authorRepository.existsById(999l), is(equalTo(false)));
    }

}
