package com.library.app.common.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * @author gabriel.freitas
 */
public abstract class GenericRepository<T> {

    private final Class<T> type;

    // FIXME: public?
    @PersistenceContext
    public EntityManager em;

    public GenericRepository() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public Class<T> getType() {
        return this.type;
    }

    public T add(final T entity) {
        em.persist(entity);
        return entity;
    }

    public T findById(final Long id) {
        if (id == null) {
            return null;
        }

        return em.find(type, id);
    }

    public void update(final T entity) {
        em.merge(entity);
    }

    @SuppressWarnings("unchecked")
    public List<T> findAll(final String orderField) {
        return em.createQuery("Select e From " + type.getSimpleName() + " e Order by e." + orderField).getResultList();
    }

    public boolean alreadyExists(final String propertyName, final String propertyValue, final Long id) {
        final StringBuilder jpql = new StringBuilder();

        jpql.append("Select 1 From " + type.getSimpleName() + " e where e." + propertyName + " = :propertyValue");

        if (id != null) {
            jpql.append(" and e.id != :id");
        }

        final Query query = em.createQuery(jpql.toString());
        query.setParameter("propertyValue", propertyValue);
        if (id != null) {
            query.setParameter("id", id);
        }

        return query.setMaxResults(1).getResultList().size() > 0;
    }

    public boolean existsById(final Long id) {
        return em
                .createQuery("Select 1 From " + type.getSimpleName() + " e where e.id = :id")
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultList().size() > 0;
    }

}
