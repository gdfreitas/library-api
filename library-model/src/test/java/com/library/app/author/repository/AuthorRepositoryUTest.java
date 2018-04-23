package com.library.app.author.repository;

import com.library.app.author.model.Author;
import com.library.app.author.model.filter.AuthorFilter;
import com.library.app.common.model.PaginatedData;
import com.library.app.common.model.filter.PaginationData;
import com.library.app.commontests.utils.TestBaseRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.library.app.commontests.author.AuthorForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;


/**
 * @author gabriel.freitas
 */
public class AuthorRepositoryUTest extends TestBaseRepository {

    private AuthorRepository authorRepository;

    @Before
    public void initTestCase() {
        initializeTestDB();
        authorRepository = new AuthorRepository();
        authorRepository.em = em;
    }

    @After
    public void setDownTestCase() {
        closeEntityManager();
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

    @Test
    public void findByFilterNoFilter() {
        loadDataForFindByFilter();

        final PaginatedData<Author> result = authorRepository.findByFilter(new AuthorFilter());
        assertThat(result.getNumberOfRows(), is(equalTo(4)));
        assertThat(result.getRows().size(), is(equalTo(4)));
        assertThat(result.getRow(0).getName(), is(equalTo(erichGamma().getName())));
        assertThat(result.getRow(1).getName(), is(equalTo(jamesGosling().getName())));
        assertThat(result.getRow(2).getName(), is(equalTo(martinFowler().getName())));
        assertThat(result.getRow(3).getName(), is(equalTo(robertMartin().getName())));
    }

    @Test
    public void findByFilterFilteringByNameAndPaginatingAndOrderingDescending() {
        loadDataForFindByFilter();

        final AuthorFilter authorFilter = new AuthorFilter();
        authorFilter.setName("o");
        authorFilter.setPaginationData(new PaginationData(0, 2, "name", PaginationData.OrderMode.DESCENDING));

        PaginatedData<Author> result = authorRepository.findByFilter(authorFilter);
        assertThat(result.getNumberOfRows(), is(equalTo(3)));
        assertThat(result.getRows().size(), is(equalTo(2)));
        assertThat(result.getRow(0).getName(), is(equalTo(robertMartin().getName())));
        assertThat(result.getRow(1).getName(), is(equalTo(martinFowler().getName())));

        authorFilter.setPaginationData(new PaginationData(2, 2, "name", PaginationData.OrderMode.DESCENDING));
        result = authorRepository.findByFilter(authorFilter);

        assertThat(result.getNumberOfRows(), is(equalTo(3)));
        assertThat(result.getRows().size(), is(equalTo(1)));
        assertThat(result.getRow(0).getName(), is(equalTo(jamesGosling().getName())));

    }

    private void loadDataForFindByFilter() {
        dbCommandExecutor.execute(() -> {
            authorRepository.add(robertMartin());
            authorRepository.add(jamesGosling());
            authorRepository.add(martinFowler());
            authorRepository.add(erichGamma());
            return null;
        });
    }

}
