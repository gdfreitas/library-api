package com.library.app.category.repository;

import com.library.app.category.model.Category;

import javax.persistence.EntityManager;

/**
 * @author gabriel.freitas
 */
public class CategoryRepository {

    protected EntityManager em;

    public Category add(Category category) {
        em.persist(category);
        return category;
    }

    public Category findById(Long id) {
        return em.find(Category.class, id);
    }
}
