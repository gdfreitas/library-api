package com.library.app.user.repository;

import com.library.app.common.repository.GenericRepository;
import com.library.app.user.model.User;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author gabriel.freitas
 */
@Stateless
public class UserRepository extends GenericRepository<User> {

    @PersistenceContext
    EntityManager em;

    @Override
    protected Class<User> getPersistentClass() {
        return User.class;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean alreadyExists(final User user) {
        return alreadyExists("email", user.getEmail(), user.getId());
    }

}
