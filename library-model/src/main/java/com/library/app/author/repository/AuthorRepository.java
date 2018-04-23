package com.library.app.author.repository;

import com.library.app.author.model.Author;
import com.library.app.author.model.filter.AuthorFilter;
import com.library.app.common.model.PaginatedData;
import com.library.app.common.repository.GenericRepository;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author gabriel.freitas
 */
@Stateless
public class AuthorRepository extends GenericRepository<Author> {

    @PersistenceContext
    protected EntityManager em;

    @Override
    protected Class<Author> getPersistentClass() {
        return Author.class;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @SuppressWarnings("all")
    public PaginatedData<Author> findByFilter(final AuthorFilter filter) {
        final StringBuilder clause = new StringBuilder("WHERE e.id is not null");
        final Map<String, Object> queryParameters = new HashMap<>();

        if (Objects.nonNull(filter.getName())) {
            clause.append(" And UPPER(e.name) Like UPPER(:name)");
            queryParameters.put("name", "%" + filter.getName() + "%");
        }

        final StringBuilder clauseSort = new StringBuilder();
        if (filter.hasOrderField()) {
            clauseSort.append("Order by e." + filter.getPaginationData().getOrderField());
            clauseSort.append(filter.getPaginationData().isAscending() ? " ASC" : " DESC");
        } else {
            clauseSort.append("Order by e.name ASC");
        }

        final Query queryAuthors = em.createQuery("Select e From Author e " + clause.toString() + " " + clauseSort.toString());

        applyQueryParametersOnQuery(queryParameters, queryAuthors);
        if (filter.hasPaginationData()) {
            queryAuthors.setFirstResult(filter.getPaginationData().getFirstResult());
            queryAuthors.setMaxResults(filter.getPaginationData().getMaxResults());
        }

        final List<Author> authors = queryAuthors.getResultList();

        final Query queryCount = em.createQuery("Select Count(e) From Author e " + clause.toString());
        applyQueryParametersOnQuery(queryParameters, queryCount);
        final Integer count = ((Long) queryCount.getSingleResult()).intValue();

        return new PaginatedData<Author>(count, authors);
    }

    private void applyQueryParametersOnQuery(final Map<String, Object> queryParameters, final Query query) {
        for (final Map.Entry<String, Object> entryMap : queryParameters.entrySet()) {
            query.setParameter(entryMap.getKey(), entryMap.getValue());
        }
    }

}
