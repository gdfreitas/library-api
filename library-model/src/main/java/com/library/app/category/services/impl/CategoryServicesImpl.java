package com.library.app.category.services.impl;

import com.library.app.category.exception.CategoryExistentException;
import com.library.app.category.exception.CategoryNotFoundException;
import com.library.app.category.model.Category;
import com.library.app.category.repository.CategoryRepository;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.exception.FieldNotValidException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;

/**
 * @author gabriel.freitas
 */
@Stateless
public class CategoryServicesImpl implements CategoryServices {

    @Inject
    protected CategoryRepository repository;

    @Inject
    protected Validator validator;

    @Override
    public Category add(final Category category) throws FieldNotValidException {
        validate(category);

        return repository.add(category);
    }

    @Override
    public void update(final Category category) {
        validate(category);

        if (!repository.existsById(category.getId())) {
            throw new CategoryNotFoundException();
        }

        repository.update(category);
    }

    @Override
    public Category findById(final Long id) throws CategoryNotFoundException {
        Category category = repository.findById(id);

        if (isNull(category)) {
            throw new CategoryNotFoundException();
        }

        return category;
    }

    @Override
    public List<Category> findAll() {
        return repository.findAll("name");
    }

    private void validate(final Category category) {
        validateFields(category);

        if (repository.alreadyExists(category)) {
            throw new CategoryExistentException();
        }
    }

    private void validateFields(final Category category) {
        Set<ConstraintViolation<Category>> errors = validator.validate(category);
        Iterator<ConstraintViolation<Category>> itErrors = errors.iterator();

        if (itErrors.hasNext()) {
            ConstraintViolation<Category> violation = itErrors.next();
            throw new FieldNotValidException(violation.getPropertyPath().toString(), violation.getMessage());
        }
    }
}
