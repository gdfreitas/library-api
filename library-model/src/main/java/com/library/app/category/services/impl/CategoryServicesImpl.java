package com.library.app.category.services.impl;

import com.library.app.category.exception.CategoryExistentException;
import com.library.app.category.model.Category;
import com.library.app.category.repository.CategoryRepository;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.exception.FieldNotValidException;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Iterator;
import java.util.Set;

/**
 * @author gabriel.freitas
 */
public class CategoryServicesImpl implements CategoryServices {

    protected CategoryRepository repository;
    protected Validator validator;

    @Override
    public Category add(final Category category) throws FieldNotValidException {
        Set<ConstraintViolation<Category>> errors = validator.validate(category);
        Iterator<ConstraintViolation<Category>> itErrors = errors.iterator();

        if (itErrors.hasNext()) {
            ConstraintViolation<Category> violation = itErrors.next();
            throw new FieldNotValidException(violation.getPropertyPath().toString(), violation.getMessage());
        }

        if (repository.alreadyExists(category)) {
            throw new CategoryExistentException();
        }

        return repository.add(category);
    }

}
