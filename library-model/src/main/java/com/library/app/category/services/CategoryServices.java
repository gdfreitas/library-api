package com.library.app.category.services;

import com.library.app.category.exception.CategoryExistentException;
import com.library.app.category.model.Category;
import com.library.app.common.exception.FieldNotValidException;

/**
 * @author gabriel.freitas
 */
public interface CategoryServices {

    Category add(Category category) throws FieldNotValidException, CategoryExistentException;

}
