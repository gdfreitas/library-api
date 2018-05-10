package com.library.app.commontests.user;

import com.library.app.user.model.User;
import org.mockito.ArgumentMatcher;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;

/**
 * @author gabriel.freitas
 */
public class UserArgumentMatcher extends ArgumentMatcher<User> {
    private User expectedUser;

    public UserArgumentMatcher(final User expectedUser) {
        this.expectedUser = expectedUser;
    }

    public static User userEq(final User expectedUser) {
        return argThat(new UserArgumentMatcher(expectedUser));
    }

    @Override
    public boolean matches(final Object argument) {
        final User actualUser = (User) argument;

        assertThat(actualUser.getId(), is(equalTo(expectedUser.getId())));
        assertThat(actualUser.getName(), is(equalTo(expectedUser.getName())));
        assertThat(actualUser.getEmail(), is(equalTo(expectedUser.getEmail())));
        assertThat(actualUser.getPassword(), is(equalTo(expectedUser.getPassword())));

        return true;
    }

}
