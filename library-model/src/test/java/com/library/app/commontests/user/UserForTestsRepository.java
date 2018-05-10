package com.library.app.commontests.user;

import com.library.app.common.utils.DateUtils;
import com.library.app.common.utils.PasswordUtils;
import com.library.app.user.model.Customer;
import com.library.app.user.model.Employee;
import com.library.app.user.model.User;
import com.library.app.user.model.User.Roles;
import org.junit.Ignore;

import java.util.Arrays;
import java.util.List;

/**
 * @author gabriel.freitas
 */
@Ignore
public class UserForTestsRepository {

    public static User johnDoe() {
        final User user = new Customer();
        user.setName("John doe");
        user.setEmail("john@domain.com");
        user.setPassword("123456");

        return user;
    }

    public static User mary() {
        final User user = new Customer();
        user.setName("Mary");
        user.setEmail("mary@domain.com");
        user.setPassword("987789");

        return user;
    }

    public static User admin() {
        final User user = new Employee();
        user.setName("Admin");
        user.setEmail("admin@domain.com");
        user.setPassword("654321");
        user.setRoles(Arrays.asList(Roles.EMPLOYEE, Roles.ADMINISTRATOR));

        return user;
    }

    public static List<User> allUsers() {
        return Arrays.asList(admin(), johnDoe(), mary());
    }

    public static User userWithIdAndCreatedAt(final User user, final Long id) {
        user.setId(id);
        user.setCreatedAt(DateUtils.getAsDateTime("2015-01-03T22:35:42Z"));

        return user;
    }

    public static User userWithEncryptedPassword(final User user) {
        user.setPassword(PasswordUtils.encryptPassword(user.getPassword()));
        return user;
    }
}
