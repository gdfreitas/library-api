package com.library.app.book.services.impl;

import com.library.app.author.model.Author;
import com.library.app.author.services.AuthorServices;
import com.library.app.book.exception.BookNotFoundException;
import com.library.app.book.model.Book;
import com.library.app.book.model.filter.BookFilter;
import com.library.app.book.repository.BookRepository;
import com.library.app.book.services.BookServices;
import com.library.app.category.model.Category;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.model.PaginatedData;
import com.library.app.common.utils.ValidationUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gabriel.freitas
 */
@Stateless
public class BookServicesImpl implements BookServices {

    @Inject
    BookRepository bookRepository;

    @Inject
    Validator validator;

    @Inject
    AuthorServices authorServices;

    @Inject
    CategoryServices categoryServices;

    @Override
    public Book add(final Book book) {
        ValidationUtils.validateEntityFields(validator, book);

        checkCategoryAndSetItOnBook(book);
        checkAuthorsAndSetThemOnBook(book);

        return bookRepository.add(book);
    }

    @Override
    public void update(final Book book) {
        ValidationUtils.validateEntityFields(validator, book);

        if (!bookRepository.existsById(book.getId())) {
            throw new BookNotFoundException();
        }

        checkCategoryAndSetItOnBook(book);
        checkAuthorsAndSetThemOnBook(book);

        bookRepository.update(book);
    }

    @Override
    public Book findById(final Long id) {
        final Book book = bookRepository.findById(id);
        if (book == null) {
            throw new BookNotFoundException();
        }
        return book;
    }

    @Override
    public PaginatedData<Book> findByFilter(final BookFilter bookFilter) {
        return bookRepository.findByFilter(bookFilter);
    }

    private void checkAuthorsAndSetThemOnBook(final Book book) {
        final List<Author> newAuthorList = new ArrayList<>();
        for (final Author author : book.getAuthors()) {
            final Author authorExistent = authorServices.findById(author.getId());
            newAuthorList.add(authorExistent);
        }
        book.setAuthors(newAuthorList);
    }

    private void checkCategoryAndSetItOnBook(final Book book) {
        final Category category = categoryServices.findById(book.getCategory().getId());
        book.setCategory(category);
    }

}
