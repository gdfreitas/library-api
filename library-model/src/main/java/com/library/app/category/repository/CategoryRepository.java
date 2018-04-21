package com.library.app.category.repository;

import com.library.app.category.model.Category;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author gabriel.freitas
 */
@Stateless
public class CategoryRepository {

    @PersistenceContext
    protected EntityManager em;

    public Category add(final Category category) {
        em.persist(category);
        return category;
    }

    public Category findById(final Long id) {
        if (id == null) {
            return null;
        }

        return em.find(Category.class, id);
    }

    public void update(final Category category) {
        em.merge(category);
    }

    @SuppressWarnings("all")
    public List<Category> findAll(final String orderField) {
        return em.createQuery("Select e From Category e Order by e." + orderField).getResultList();
    }

    public boolean alreadyExists(final Category category) {
        final StringBuilder jpql = new StringBuilder();
        jpql.append("Select 1 From Category e where e.name = :name");

        if (category.getId() != null) {
            jpql.append(" And e.id != :id");
        }

        final Query query = em.createQuery(jpql.toString());
        query.setParameter("name", category.getName());

        if (category.getId() != null) {
            query.setParameter("id", category.getId());
        }

        return !query.setMaxResults(1).getResultList().isEmpty();
    }

    @SuppressWarnings("all")
    public boolean existsById(final Long id) {
        return !em.createQuery("Select 1 From Category e where e.id = :id")
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultList()
                .isEmpty();
    }
}
