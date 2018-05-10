package com.library.app.user.resource;

import static com.library.app.commontests.user.UserArgumentMatcher.*;
import static com.library.app.commontests.user.UserForTestsRepository.*;
import static com.library.app.commontests.user.UserTestUtils.*;
import static com.library.app.commontests.utils.FileTestNameUtils.*;
import static com.library.app.commontests.utils.JsonTestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.model.HttpCode;
import com.library.app.common.model.PaginatedData;
import com.library.app.commontests.utils.ResourceDefinitions;
import com.library.app.user.exception.UserExistentException;
import com.library.app.user.exception.UserNotFoundException;
import com.library.app.user.model.User;
import com.library.app.user.model.User.Roles;
import com.library.app.user.model.filter.UserFilter;
import com.library.app.user.services.UserServices;

/**
 * @author gabriel.freitas
 */
public class UserResourceUTest {

    private UserResource userResource;

    @Mock
    private UserServices userServices;

    @Mock
    private UriInfo uriInfo;

    @Mock
    private SecurityContext securityContext;

    private static final String PATH_RESOURCE = ResourceDefinitions.USER.getResourceName();

    @Before
    public void initTestCase() {
        MockitoAnnotations.initMocks(this);

        userResource = new UserResource();

        userResource.userJsonConverter = new UserJsonConverter();
        userResource.userServices = userServices;
        userResource.uriInfo = uriInfo;
        userResource.securityContext = securityContext;
    }

    @Test
    public void addValidCustomer() {
        when(userServices.add(userEq(johnDoe()))).thenReturn(userWithIdAndCreatedAt(johnDoe(), 1L));

        final Response response = userResource.add(readJsonFile(getPathFileRequest(PATH_RESOURCE,
                "customerJohnDoe.json")));
        assertThat(response.getStatus(), is(equalTo(HttpCode.CREATED.getCode())));
        assertJsonMatchesExpectedJson(response.getEntity().toString(), "{\"id\": 1}");
    }

    @Test
    public void addValidEmployee() {
        final Response response = userResource
                .add(readJsonFile(getPathFileRequest(PATH_RESOURCE, "employeeAdmin.json")));
        assertThat(response.getStatus(), is(equalTo(HttpCode.FORBIDDEN.getCode())));
    }

    @Test
    public void addExistentUser() {
        when(userServices.add(userEq(johnDoe()))).thenThrow(new UserExistentException());

        final Response response = userResource.add(readJsonFile(getPathFileRequest(PATH_RESOURCE,
                "customerJohnDoe.json")));
        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, "userAlreadyExists.json");
    }

    @Test
    public void addUserWithNullName() {
        when(userServices.add((User) anyObject())).thenThrow(new FieldNotValidException("name", "may not be null"));

        final Response response = userResource
                .add(readJsonFile(getPathFileRequest(PATH_RESOURCE, "customerWithNullName.json")));
        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, "userErrorNullName.json");
    }

    @Test
    public void updateValidCustomer() {
        when(securityContext.isUserInRole(Roles.ADMINISTRATOR.name())).thenReturn(true);

        final Response response = userResource.update(1L,
                readJsonFile(getPathFileRequest(PATH_RESOURCE, "updateCustomerJohnDoe.json")));
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertThat(response.getEntity().toString(), is(equalTo("")));

        final User expectedUser = userWithIdAndCreatedAt(johnDoe(), 1L);
        expectedUser.setPassword(null);
        verify(userServices).update(userEq(expectedUser));
    }

    @Test
    public void updateValidCustomerLoggedAsCustomerToBeUpdated() {
        setUpPrincipalUser(userWithIdAndCreatedAt(johnDoe(), 1L));
        when(securityContext.isUserInRole(Roles.ADMINISTRATOR.name())).thenReturn(false);

        final Response response = userResource.update(1L,
                readJsonFile(getPathFileRequest(PATH_RESOURCE, "updateCustomerJohnDoe.json")));
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertThat(response.getEntity().toString(), is(equalTo("")));

        final User expectedUser = userWithIdAndCreatedAt(johnDoe(), 1L);
        expectedUser.setPassword(null);
        verify(userServices).update(userEq(expectedUser));
    }

    @Test
    public void updateValidCustomerLoggedAsOtherCustomer() {
        setUpPrincipalUser(userWithIdAndCreatedAt(mary(), 2L));
        when(securityContext.isUserInRole(Roles.ADMINISTRATOR.name())).thenReturn(false);

        final Response response = userResource.update(1L,
                readJsonFile(getPathFileRequest(PATH_RESOURCE, "updateCustomerJohnDoe.json")));
        assertThat(response.getStatus(), is(equalTo(HttpCode.FORBIDDEN.getCode())));
    }

    @Test
    public void updateValidEmployee() {
        when(securityContext.isUserInRole(Roles.ADMINISTRATOR.name())).thenReturn(true);

        final Response response = userResource.update(1L,
                readJsonFile(getPathFileRequest(PATH_RESOURCE, "updateEmployeeAdmin.json")));
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertThat(response.getEntity().toString(), is(equalTo("")));

        final User expectedUser = userWithIdAndCreatedAt(admin(), 1L);
        expectedUser.setPassword(null);
        verify(userServices).update(userEq(expectedUser));
    }

    @Test
    public void updateUserWithEmailBelongingToOtherUser() {
        when(securityContext.isUserInRole(Roles.ADMINISTRATOR.name())).thenReturn(true);

        doThrow(new UserExistentException()).when(userServices).update(userWithIdAndCreatedAt(johnDoe(), 1L));

        final Response response = userResource.update(1L,
                readJsonFile(getPathFileRequest(PATH_RESOURCE, "updateCustomerJohnDoe.json")));
        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, "userAlreadyExists.json");
    }

    @Test
    public void updateUserWithNullName() {
        when(securityContext.isUserInRole(Roles.ADMINISTRATOR.name())).thenReturn(true);

        doThrow(new FieldNotValidException("name", "may not be null")).when(userServices).update(
                userWithIdAndCreatedAt(johnDoe(), 1L));

        final Response response = userResource.update(1L,
                readJsonFile(getPathFileRequest(PATH_RESOURCE, "customerWithNullName.json")));
        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, "userErrorNullName.json");
    }

    @Test
    public void updateUserNotFound() {
        when(securityContext.isUserInRole(Roles.ADMINISTRATOR.name())).thenReturn(true);
        doThrow(new UserNotFoundException()).when(userServices).update(userWithIdAndCreatedAt(johnDoe(), 2L));

        final Response response = userResource.update(2L,
                readJsonFile(getPathFileRequest(PATH_RESOURCE, "updateCustomerJohnDoe.json")));
        assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
    }

    @Test
    public void updateUserPassword() {
        when(securityContext.isUserInRole(Roles.ADMINISTRATOR.name())).thenReturn(true);

        final Response response = userResource.updatePassword(1L, getJsonWithPassword("123456"));
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertThat(response.getEntity().toString(), is(equalTo("")));

        verify(userServices).updatePassword(1L, "123456");
    }

    @Test
    public void updateCustomerPasswordLoggedAsCustomerToBeUpdated() {
        setUpPrincipalUser(userWithIdAndCreatedAt(johnDoe(), 1L));
        when(securityContext.isUserInRole(Roles.ADMINISTRATOR.name())).thenReturn(false);

        final Response response = userResource.updatePassword(1L, getJsonWithPassword("123456"));
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertThat(response.getEntity().toString(), is(equalTo("")));

        verify(userServices).updatePassword(1L, "123456");
    }

    @Test
    public void updateCustomerPasswordLoggedAsOtherCustomer() {
        setUpPrincipalUser(userWithIdAndCreatedAt(mary(), 2L));
        when(securityContext.isUserInRole(Roles.ADMINISTRATOR.name())).thenReturn(false);

        final Response response = userResource.updatePassword(1L, getJsonWithPassword("123456"));
        assertThat(response.getStatus(), is(equalTo(HttpCode.FORBIDDEN.getCode())));
    }

    @Test
    public void findCustomerById() {
        when(userServices.findById(1L)).thenReturn(userWithIdAndCreatedAt(johnDoe(), 1L));

        final Response response = userResource.findById(1L);
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertJsonResponseWithFile(response, "customerJohnDoeFound.json");
    }

    @Test
    public void findUserByIdNotFound() {
        when(userServices.findById(1L)).thenThrow(new UserNotFoundException());

        final Response response = userResource.findById(1L);
        assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
    }

    @Test
    public void findEmployeeByEmailAndPassword() {
        when(userServices.findByEmailAndPassword(admin().getEmail(), admin().getPassword())).thenReturn(
                userWithIdAndCreatedAt(admin(), 1L));

        final Response response = userResource.findByEmailAndPassword(getJsonWithEmailAndPassword(admin().getEmail(),
                admin()
                        .getPassword()));
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertJsonResponseWithFile(response, "employeeAdminFound.json");
    }

    @Test
    public void findUserByEmailAndPasswordNotFound() {
        when(userServices.findByEmailAndPassword(admin().getEmail(), admin().getPassword())).thenThrow(
                new UserNotFoundException());

        final Response response = userResource.findByEmailAndPassword(getJsonWithEmailAndPassword(admin().getEmail(),
                admin()
                        .getPassword()));
        assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findByFilterNoFilter() {
        final List<User> users = new ArrayList<>();
        final List<User> allUsers = allUsers();
        for (int i = 1; i <= allUsers.size(); i++) {
            users.add(userWithIdAndCreatedAt(allUsers.get(i - 1), new Long(i)));
        }

        final MultivaluedMap<String, String> multiMap = mock(MultivaluedMap.class);
        when(uriInfo.getQueryParameters()).thenReturn(multiMap);

        when(userServices.findByFilter((UserFilter) anyObject())).thenReturn(
                new PaginatedData<User>(users.size(), users));

        final Response response = userResource.findByFilter();
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertJsonResponseWithFile(response, "usersAllInOnePage.json");
    }

    private void setUpPrincipalUser(final User user) {
        final Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(user.getEmail());

        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(userServices.findByEmail(user.getEmail())).thenReturn(user);
    }

    private void assertJsonResponseWithFile(final Response response, final String fileName) {
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE, fileName));
    }

}
