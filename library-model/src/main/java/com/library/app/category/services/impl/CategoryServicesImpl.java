package com.library.app.category.services.impl;

import com.library.app.category.exception.CategoryExistentException;
import com.library.app.category.exception.CategoryNotFoundException;
import com.library.app.category.model.Category;
import com.library.app.category.repository.CategoryRepository;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.utils.ValidationUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Validator;
import java.util.List;

/**
 * @author gabriel.freitas
 */
@Stateless
public class CategoryServicesImpl implements CategoryServices {

    @Inject
    Validator validator;

    @Inject
    CategoryRepository categoryRepository;

    @Override
    public Category add(final Category category) {
        validateCategory(category);

        return categoryRepository.add(category);
    }

    @Override
    public void update(final Category category) {
        validateCategory(category);

        if (!categoryRepository.existsById(category.getId())) {
            throw new CategoryNotFoundException();
        }

        categoryRepository.update(category);
    }

    @Override
    public Category findById(final Long id) throws CategoryNotFoundException {
        final Category category = categoryRepository.findById(id);
        if (category == null) {
            throw new CategoryNotFoundException();
        }
        return category;
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll("name");
    }

    private void validateCategory(final Category category) {
        ValidationUtils.validateEntityFields(validator, category);

        if (categoryRepository.alreadyExists(category)) {
            throw new CategoryExistentException();
        }
    }

}
