package com.library.app.author.services.impl;

import static com.library.app.commontests.author.AuthorForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.library.app.author.exception.AuthorNotFoundException;
import com.library.app.author.model.Author;
import com.library.app.author.model.filter.AuthorFilter;
import com.library.app.author.repository.AuthorRepository;
import com.library.app.author.services.AuthorServices;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.model.PaginatedData;


/**
 * @author gabriel.freitas
 */
public class AuthorServicesUTest {

    private static Validator validator;
    private AuthorServices authorServices;

    @Mock
    private AuthorRepository authorRepository;

    @BeforeClass
    public static void initTestClass() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Before
    public void initTestCase() {
        MockitoAnnotations.initMocks(this);

        authorServices = new AuthorServicesImpl();

        ((AuthorServicesImpl) authorServices).authorRepository = authorRepository;
        ((AuthorServicesImpl) authorServices).validator = validator;
    }

    @Test
    public void addAuthorWithNullName() {
        addAuthorWithInvalidName(null);
    }

    @Test
    public void addAuthorWithShortName() {
        addAuthorWithInvalidName("A");
    }

    @Test
    public void addAuthorWithLongName() {
        addAuthorWithInvalidName("This is a very long name that will cause an exception to be thrown");
    }

    @Test
    public void addValidAuthor() {
        when(authorRepository.add(robertMartin())).thenReturn(authorWithId(robertMartin(), 1L));

        try {
            final Author authorAdded = authorServices.add(robertMartin());
            assertThat(authorAdded.getId(), equalTo(1L));
        } catch (final FieldNotValidException e) {
            fail("No error should have been thrown");
        }
    }

    @Test
    public void updateAuthorWithNullName() {
        updateAuthorWithInvalidName(null);
    }

    @Test
    public void updateAuthorWithShortName() {
        updateAuthorWithInvalidName("A");
    }

    @Test
    public void updateAuthorWithLongName() {
        updateAuthorWithInvalidName("This is a very long name that will cause an exception to be thrown");
    }

    @Test(expected = AuthorNotFoundException.class)
    public void updateAuthorNotFound() {
        when(authorRepository.existsById(1L)).thenReturn(false);

        authorServices.update(authorWithId(robertMartin(), 1L));
    }

    @Test
    public void updateValidAuthor() {
        final Author authorToUpdate = authorWithId(robertMartin(), 1L);
        when(authorRepository.existsById(1L)).thenReturn(true);

        authorServices.update(authorToUpdate);
        verify(authorRepository).update(authorToUpdate);
    }

    @Test(expected = AuthorNotFoundException.class)
    public void findAuthorByIdNotFound() throws AuthorNotFoundException {
        when(authorRepository.findById(1L)).thenReturn(null);

        authorServices.findById(1L);
    }

    @Test
    public void findAuthorById() throws AuthorNotFoundException {
        when(authorRepository.findById(1L)).thenReturn(authorWithId(robertMartin(), 1L));

        final Author author = authorServices.findById(1L);
        assertThat(author, is(notNullValue()));
        assertThat(author.getName(), is(equalTo(robertMartin().getName())));
    }

    @Test
    public void findAuthorByFilter() {
        final PaginatedData<Author> authors = new PaginatedData<>(1, Collections.singletonList(authorWithId(robertMartin(), 1L)));
        when(authorRepository.findByFilter(anyObject())).thenReturn(authors);

        final PaginatedData<Author> authorsReturned = authorServices.findByFilter(new AuthorFilter());
        assertThat(authorsReturned.getNumberOfRows(), is(equalTo(1)));
        assertThat(authorsReturned.getRow(0).getName(), is(equalTo(robertMartin().getName())));
    }

    private void updateAuthorWithInvalidName(final String name) {
        try {
            authorServices.update(new Author(name));
            fail("An error should have been thrown");
        } catch (final FieldNotValidException e) {
            assertThat(e.getFieldName(), is(equalTo("name")));
        }
    }

    private void addAuthorWithInvalidName(final String name) {
        try {
            authorServices.add(new Author(name));
            fail("An error should have been thrown");
        } catch (final FieldNotValidException e) {
            assertThat(e.getFieldName(), is(equalTo("name")));
        }
    }

}
