package com.library.app.commontests.book;

import com.library.app.book.model.Book;
import org.mockito.ArgumentMatcher;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;

/**
 * @author gabriel.freitas
 */
public class BookArgumentMatcher extends ArgumentMatcher<Book> {

    private Book expected;

    public BookArgumentMatcher(final Book expected) {
        this.expected = expected;
    }

    public static Book bookEq(final Book expected) {
        return argThat(new BookArgumentMatcher(expected));
    }

    @Override
    public boolean matches(final Object actualObj) {
        final Book actual = (Book) actualObj;

        assertThat(actual.getId(), is(equalTo(expected.getId())));
        assertThat(actual.getTitle(), is(equalTo(expected.getTitle())));
        assertThat(actual.getDescription(), is(equalTo(expected.getDescription())));
        assertThat(actual.getCategory(), is(equalTo(expected.getCategory())));
        assertThat(actual.getAuthors(), is(equalTo(expected.getAuthors())));
        assertThat(actual.getPrice(), is(equalTo(expected.getPrice())));

        return true;
    }
}
