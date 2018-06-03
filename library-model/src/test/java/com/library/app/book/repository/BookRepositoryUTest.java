package com.library.app.book.repository;

import static com.library.app.commontests.author.AuthorForTestsRepository.*;
import static com.library.app.commontests.book.BookForTestsRepository.*;
import static com.library.app.commontests.category.CategoryForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.library.app.author.model.Author;
import com.library.app.book.model.Book;
import com.library.app.book.model.filter.BookFilter;
import com.library.app.common.model.PaginatedData;
import com.library.app.common.model.filter.PaginationData;
import com.library.app.common.model.filter.PaginationData.OrderMode;
import com.library.app.commontests.book.BookForTestsRepository;
import com.library.app.commontests.utils.TestBaseRepository;

/**
 * @author gabriel.freitas
 */
public class BookRepositoryUTest extends TestBaseRepository {

    private BookRepository bookRepository;

    @Before
    public void initTestCase() {
        initializeTestDB();

        bookRepository = new BookRepository();
        bookRepository.em = em;

        loadCategoriesAndAuthors();
    }

    @After
    public void setDownTestCase() {
        closeEntityManager();
    }

    @Test
    public void addBookAndFindIt() {
        final Book designPatterns = normalizeDependencies(designPatterns(), em);
        final Long bookAddedId = dbCommandExecutor.execute(() -> bookRepository.add(designPatterns).getId());
        assertThat(bookAddedId, is(notNullValue()));

        final Book book = bookRepository.findById(bookAddedId);
        assertThat(book.getTitle(), is(equalTo(designPatterns().getTitle())));
        assertThat(book.getDescription(), is(equalTo(designPatterns().getDescription())));
        assertThat(book.getCategory().getName(), is(equalTo(designPatterns().getCategory().getName())));
        assertAuthors(book, erichGamma(), johnVlissides(), ralphJohnson(), richardHelm());
        assertThat(book.getPrice(), is(equalTo(48.94D)));
    }

    @Test
    public void findBookByIdNotFound() {
        final Book book = bookRepository.findById(999L);
        assertThat(book, is(nullValue()));
    }

    @Test
    public void updateBook() {
        final Book designPatterns = normalizeDependencies(designPatterns(), em);
        final Long bookAddedId = dbCommandExecutor.execute(() -> bookRepository.add(designPatterns).getId());

        assertThat(bookAddedId, is(notNullValue()));
        final Book book = bookRepository.findById(bookAddedId);
        assertThat(book.getTitle(), is(equalTo(designPatterns().getTitle())));

        book.setTitle("Design Patterns");
        dbCommandExecutor.execute(() -> {
            bookRepository.update(book);
            return null;
        });

        final Book bookAfterUpdate = bookRepository.findById(bookAddedId);
        assertThat(bookAfterUpdate.getTitle(), is(equalTo("Design Patterns")));
    }

    @Test
    public void existsById() {
        final Book designPatterns = normalizeDependencies(designPatterns(), em);
        final Long bookAddedId = dbCommandExecutor.execute(() -> {
            return bookRepository.add(designPatterns).getId();
        });

        assertThat(bookRepository.existsById(bookAddedId), is(equalTo(true)));
        assertThat(bookRepository.existsById(999l), is(equalTo(false)));
    }

    @Test
    public void findByFilterNoFilter() {
        loadBooksForFindByFilter();

        final PaginatedData<Book> result = bookRepository.findByFilter(new BookFilter());
        assertThat(result.getNumberOfRows(), is(equalTo(5)));
        assertThat(result.getRows().size(), is(equalTo(5)));
        assertThat(result.getRow(0).getTitle(), is(equalTo(BookForTestsRepository.cleanCode().getTitle())));
        assertThat(result.getRow(1).getTitle(), is(equalTo(designPatterns().getTitle())));
        assertThat(result.getRow(2).getTitle(), is(equalTo(effectiveJava().getTitle())));
        assertThat(result.getRow(3).getTitle(), is(equalTo(peaa().getTitle())));
        assertThat(result.getRow(4).getTitle(), is(equalTo(refactoring().getTitle())));
    }

    @Test
    public void findByFilterWithPaging() {
        loadBooksForFindByFilter();

        final BookFilter bookFilter = new BookFilter();
        bookFilter.setPaginationData(new PaginationData(0, 3, "title", OrderMode.DESCENDING));
        PaginatedData<Book> result = bookRepository.findByFilter(bookFilter);

        assertThat(result.getNumberOfRows(), is(equalTo(5)));
        assertThat(result.getRows().size(), is(equalTo(3)));
        assertThat(result.getRow(0).getTitle(), is(equalTo(refactoring().getTitle())));
        assertThat(result.getRow(1).getTitle(), is(equalTo(peaa().getTitle())));
        assertThat(result.getRow(2).getTitle(), is(equalTo(effectiveJava().getTitle())));

        bookFilter.setPaginationData(new PaginationData(3, 3, "title", OrderMode.DESCENDING));
        result = bookRepository.findByFilter(bookFilter);

        assertThat(result.getNumberOfRows(), is(equalTo(5)));
        assertThat(result.getRows().size(), is(equalTo(2)));
        assertThat(result.getRow(0).getTitle(), is(equalTo(designPatterns().getTitle())));
        assertThat(result.getRow(1).getTitle(), is(equalTo(BookForTestsRepository.cleanCode().getTitle())));
    }

    @Test
    public void findByFilterFilteringByCategoryAndTitle() {
        loadBooksForFindByFilter();

        final Book book = new Book();
        book.setCategory(architecture());

        final BookFilter bookFilter = new BookFilter();
        bookFilter.setCategoryId(normalizeDependencies(book, em).getCategory().getId());
        bookFilter.setTitle("Software");
        bookFilter.setPaginationData(new PaginationData(0, 3, "title", OrderMode.ASCENDING));
        final PaginatedData<Book> result = bookRepository.findByFilter(bookFilter);

        assertThat(result.getNumberOfRows(), is(equalTo(1)));
        assertThat(result.getRows().size(), is(equalTo(1)));
        assertThat(result.getRow(0).getTitle(), is(equalTo(designPatterns().getTitle())));
    }

    private void loadBooksForFindByFilter() {
        dbCommandExecutor.execute(() -> {
            allBooks().forEach((book) -> bookRepository.add(normalizeDependencies(book, em)));
            return null;
        });
    }

    private void assertAuthors(final Book book, final Author... expectedAuthors) {
        final List<Author> authors = book.getAuthors();
        assertThat(authors.size(), is(equalTo(expectedAuthors.length)));

        for (int i = 0; i < expectedAuthors.length; i++) {
            final Author actualAuthor = authors.get(i);
            final Author expectedAuthor = expectedAuthors[i];
            assertThat(actualAuthor.getName(), is(equalTo(expectedAuthor.getName())));
        }
    }

    private void loadCategoriesAndAuthors() {
        dbCommandExecutor.execute(() -> {
            allCategories().forEach(em::persist);
            allAuthors().forEach(em::persist);
            return null;
        });
    }

}
