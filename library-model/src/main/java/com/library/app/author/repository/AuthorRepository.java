package com.library.app.author.repository;

import com.library.app.author.model.Author;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author gabriel.freitas
 */
@Stateless
public class AuthorRepository {

    @PersistenceContext
    protected EntityManager em;

    public Author add(final Author author) {
        em.persist(author);
        return author;
    }

    public Author findById(final Long id) {
        if (id == null) {
            return null;
        }
        return em.find(Author.class, id);
    }

    public void update(final Author author) {
        em.merge(author);
    }

    @SuppressWarnings("all")
    public boolean existsById(final long id) {
        return em.createQuery("Select 1 From Author e where e.id = :id")
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultList().size() > 0;
    }

}
