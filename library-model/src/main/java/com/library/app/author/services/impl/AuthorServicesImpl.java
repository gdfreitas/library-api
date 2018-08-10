package com.library.app.author.services.impl;

import com.library.app.author.exception.AuthorNotFoundException;
import com.library.app.author.model.Author;
import com.library.app.author.model.filter.AuthorFilter;
import com.library.app.author.repository.AuthorRepository;
import com.library.app.author.services.AuthorServices;
import com.library.app.common.model.PaginatedData;
import com.library.app.common.utils.ValidationUtils;
import com.library.app.logaudit.interceptor.Auditable;
import com.library.app.logaudit.interceptor.LogAuditInterceptor;
import com.library.app.logaudit.model.LogAudit;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.validation.Validator;

/**
 * @author gabriel.freitas
 */
@Stateless
@Interceptors(LogAuditInterceptor.class)
public class AuthorServicesImpl implements AuthorServices {

    @Inject
    AuthorRepository authorRepository;

    @Inject
    Validator validator;

    @Override
    @Auditable(action = LogAudit.Action.ADD)
    public Author add(final Author author) {
        ValidationUtils.validateEntityFields(validator, author);

        return authorRepository.add(author);
    }

    @Override
    @Auditable(action = LogAudit.Action.UPDATE)
    public void update(final Author author) {
        ValidationUtils.validateEntityFields(validator, author);

        if (!authorRepository.existsById(author.getId())) {
            throw new AuthorNotFoundException();
        }

        authorRepository.update(author);
    }

    @Override
    public Author findById(final Long id) throws AuthorNotFoundException {
        final Author author = authorRepository.findById(id);
        if (author == null) {
            throw new AuthorNotFoundException();
        }
        return author;
    }

    @Override
    public PaginatedData<Author> findByFilter(final AuthorFilter authorFilter) {
        return authorRepository.findByFilter(authorFilter);
    }

}
