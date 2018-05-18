package com.library.app.user.repository;

import com.library.app.common.model.PaginatedData;
import com.library.app.common.model.filter.PaginationData;
import com.library.app.common.model.filter.PaginationData.OrderMode;
import com.library.app.commontests.utils.TestBaseRepository;
import com.library.app.user.model.User;
import com.library.app.user.model.User.UserType;
import com.library.app.user.model.filter.UserFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.library.app.commontests.user.UserForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

/**
 * @author gabriel.freitas
 */
public class UserRepositoryUTest extends TestBaseRepository {

    private UserRepository userRepository;

    @Before
    public void initTestCase() {
        initializeTestDB();

        userRepository = new UserRepository();
        userRepository.em = em;
    }

    @After
    public void setDownTestCase() {
        closeEntityManager();
    }

    @Test
    public void addCustomerAndFindIt() {
        final Long userAddedId = dbCommandExecutor.execute(() -> {
            return userRepository.add(johnDoe()).getId();
        });
        assertThat(userAddedId, is(notNullValue()));

        final User user = userRepository.findById(userAddedId);
        assertUser(user, johnDoe(), UserType.CUSTOMER);
    }

    @Test
    public void findUserByIdNotFound() {
        final User user = userRepository.findById(999L);
        assertThat(user, is(nullValue()));
    }

    @Test
    public void updateCustomer() {
        final Long userAddedId = dbCommandExecutor.execute(() -> {
            return userRepository.add(johnDoe()).getId();
        });
        assertThat(userAddedId, is(notNullValue()));

        final User user = userRepository.findById(userAddedId);
        assertThat(user.getName(), is(equalTo(johnDoe().getName())));

        user.setName("New name");
        dbCommandExecutor.execute(() -> {
            userRepository.update(user);
            return null;
        });

        final User userAfterUpdate = userRepository.findById(userAddedId);
        assertThat(userAfterUpdate.getName(), is(equalTo("New name")));
    }

    @Test
    public void alreadyExistsUserWithoutId() {
        dbCommandExecutor.execute(() -> {
            return userRepository.add(johnDoe()).getId();
        });

        assertThat(userRepository.alreadyExists(johnDoe()), is(equalTo(true)));
        assertThat(userRepository.alreadyExists(admin()), is(equalTo(false)));
    }

    @Test
    public void alreadyExistsCategoryWithId() {
        final User customer = dbCommandExecutor.execute(() -> {
            userRepository.add(admin());
            return userRepository.add(johnDoe());
        });

        assertFalse(userRepository.alreadyExists(customer));

        customer.setEmail(admin().getEmail());
        assertThat(userRepository.alreadyExists(customer), is(equalTo(true)));

        customer.setEmail("newemail@domain.com");
        assertThat(userRepository.alreadyExists(customer), is(equalTo(false)));
    }

    @Test
    public void findUserByEmail() {
        dbCommandExecutor.execute(() -> {
            return userRepository.add(johnDoe());
        });

        final User user = userRepository.findByEmail(johnDoe().getEmail());
        assertUser(user, johnDoe(), UserType.CUSTOMER);
    }

    @Test
    public void findUserByEmailNotFound() {
        final User user = userRepository.findByEmail(johnDoe().getEmail());
        assertThat(user, is(nullValue()));
    }

    @Test
    public void findByFilterWithPagingOrderingByNameDescending() {
        loadDataForFindByFilter();

        UserFilter userFilter = new UserFilter();
        userFilter.setPaginationData(new PaginationData(0, 2, "name", OrderMode.DESCENDING));

        PaginatedData<User> result = userRepository.findByFilter(userFilter);
        assertThat(result.getNumberOfRows(), is(equalTo(3)));
        assertThat(result.getRows().size(), is(equalTo(2)));
        assertThat(result.getRow(0).getName(), is(equalTo(mary().getName())));
        assertThat(result.getRow(1).getName(), is(equalTo(johnDoe().getName())));

        userFilter = new UserFilter();
        userFilter.setPaginationData(new PaginationData(2, 2, "name", OrderMode.DESCENDING));

        result = userRepository.findByFilter(userFilter);
        assertThat(result.getNumberOfRows(), is(equalTo(3)));
        assertThat(result.getRows().size(), is(equalTo(1)));
        assertThat(result.getRow(0).getName(), is(equalTo(admin().getName())));
    }

    @Test
    public void findByFilterFilteringByName() {
        loadDataForFindByFilter();

        final UserFilter userFilter = new UserFilter();
        userFilter.setName("m");
        userFilter.setPaginationData(new PaginationData(0, 2, "name", OrderMode.ASCENDING));

        final PaginatedData<User> result = userRepository.findByFilter(userFilter);
        assertThat(result.getNumberOfRows(), is(equalTo(2)));
        assertThat(result.getRows().size(), is(equalTo(2)));
        assertThat(result.getRow(0).getName(), is(equalTo(admin().getName())));
        assertThat(result.getRow(1).getName(), is(equalTo(mary().getName())));
    }

    @Test
    public void findByFilterFilteringByNameAndType() {
        loadDataForFindByFilter();

        final UserFilter userFilter = new UserFilter();
        userFilter.setName("m");
        userFilter.setUserType(UserType.EMPLOYEE);
        userFilter.setPaginationData(new PaginationData(0, 2, "name", OrderMode.ASCENDING));

        final PaginatedData<User> result = userRepository.findByFilter(userFilter);
        assertThat(result.getNumberOfRows(), is(equalTo(1)));
        assertThat(result.getRows().size(), is(equalTo(1)));
        assertThat(result.getRow(0).getName(), is(equalTo(admin().getName())));
    }

    private void loadDataForFindByFilter() {
        dbCommandExecutor.execute(() -> {
            allUsers().forEach(userRepository::add);
            return null;
        });
    }

    private void assertUser(final User actualUser, final User expectedUser, final UserType expectedUserType) {
        assertThat(actualUser.getName(), is(equalTo(expectedUser.getName())));
        assertThat(actualUser.getEmail(), is(equalTo(expectedUser.getEmail())));
        assertThat(actualUser.getRoles().toArray(), is(equalTo(expectedUser.getRoles().toArray())));
        assertThat(actualUser.getCreatedAt(), is(notNullValue()));
        assertThat(actualUser.getPassword(), is(expectedUser.getPassword()));
        assertThat(actualUser.getUserType(), is(equalTo(expectedUserType)));
    }

}
