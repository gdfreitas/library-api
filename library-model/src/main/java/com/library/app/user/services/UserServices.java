package com.library.app.user.services;

import javax.ejb.Local;

import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.model.PaginatedData;
import com.library.app.user.exception.UserExistentException;
import com.library.app.user.exception.UserNotFoundException;
import com.library.app.user.model.User;
import com.library.app.user.model.filter.UserFilter;


/**
 * @author gabriel.freitas
 */
@Local
public interface UserServices {

    User add(User user) throws FieldNotValidException, UserExistentException;

    User findById(Long id) throws UserNotFoundException;

    void update(User user) throws FieldNotValidException, UserExistentException, UserNotFoundException;

    void updatePassword(Long id, String password) throws UserNotFoundException;

    User findByEmail(String email) throws UserNotFoundException;

    User findByEmailAndPassword(String email, String password) throws UserNotFoundException;

    PaginatedData<User> findByFilter(UserFilter userFilter);

}
