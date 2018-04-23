package com.library.app.category.repository;

import com.library.app.category.model.Category;
import com.library.app.common.repository.GenericRepository;

import javax.ejb.Stateless;

/**
 * @author gabriel.freitas
 */
@Stateless
public class CategoryRepository extends GenericRepository<Category> {

    public boolean alreadyExists(final Category category) {
        return alreadyExists("name", category.getName(), category.getId());
    }

}
