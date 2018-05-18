package com.library.app.user.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.library.app.common.json.JsonReader;
import com.library.app.common.model.HttpCode;
import com.library.app.commontests.utils.ArquillianTestUtils;
import com.library.app.commontests.utils.IntTestUtils;
import com.library.app.commontests.utils.ResourceClient;
import com.library.app.commontests.utils.ResourceDefinitions;
import com.library.app.user.model.User;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import java.net.URL;

import static com.library.app.commontests.user.UserForTestsRepository.*;
import static com.library.app.commontests.user.UserTestUtils.getJsonWithEmailAndPassword;
import static com.library.app.commontests.user.UserTestUtils.getJsonWithPassword;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileRequest;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileResponse;
import static com.library.app.commontests.utils.JsonTestUtils.assertJsonMatchesFileContent;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author gabriel.freitas
 */
@RunWith(Arquillian.class)
public class UserResourceIntTest {

    private static final String PATH_RESOURCE = ResourceDefinitions.USER.getResourceName();
    @ArquillianResource
    private URL deploymentUrl;
    private ResourceClient resourceClient;

    @Deployment
    public static WebArchive createDeployment() {
        return ArquillianTestUtils.createDeploymentArchive();
    }

    @Before
    public void initTestCase() {
        resourceClient = new ResourceClient(deploymentUrl);

        resourceClient.resourcePath("DB/").delete();
        resourceClient.resourcePath("DB/" + PATH_RESOURCE + "/admin").postWithContent("");
    }

    @Test
    @RunAsClient
    public void addValidCustomerAndFindIt() {
        final Long userId = addUserAndGetId("customerJohnDoe.json");

        findUserAndAssertResponseWithUser(userId, johnDoe());
    }

    @Test
    @RunAsClient
    public void addUserWithNullName() {
        addUserWithValidationError("customerWithNullName.json", "userErrorNullName.json");
    }

    @Test
    @RunAsClient
    public void addExistentUser() {
        addUserAndGetId("customerJohnDoe.json");
        addUserWithValidationError("customerJohnDoe.json", "userAlreadyExists.json");
    }

    @Test
    @RunAsClient
    public void updateValidCustomerAsAdmin() {
        final Long userId = addUserAndGetId("customerJohnDoe.json");
        findUserAndAssertResponseWithUser(userId, johnDoe());

        final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/" + userId).putWithFile(
                getPathFileRequest(PATH_RESOURCE, "updateCustomerJohnDoeWithNewName.json"));
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));

        final User expectedUser = johnDoe();
        expectedUser.setName("New name");
        findUserAndAssertResponseWithUser(userId, expectedUser);
    }

    @Test
    @RunAsClient
    public void updateValidLoggedCustomerAsCustomer() {
        final Long userId = addUserAndGetId("customerJohnDoe.json");
        findUserAndAssertResponseWithUser(userId, johnDoe());

        final Response response = resourceClient.user(johnDoe()).resourcePath(PATH_RESOURCE + "/" + userId)
                .putWithFile(
                        getPathFileRequest(PATH_RESOURCE, "updateCustomerJohnDoeWithNewName.json"));
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));

        resourceClient.user(admin());
        final User expectedUser = johnDoe();
        expectedUser.setName("New name");
        findUserAndAssertResponseWithUser(userId, expectedUser);
    }

    @Test
    @RunAsClient
    public void updateCustomerButNotTheLoggedCustomer() {
        final Long userId = addUserAndGetId("customerJohnDoe.json");
        findUserAndAssertResponseWithUser(userId, johnDoe());
        addUserAndGetId("customerMary.json");

        final Response response = resourceClient.user(mary()).resourcePath(PATH_RESOURCE + "/" + userId).putWithFile(
                getPathFileRequest(PATH_RESOURCE, "updateCustomerJohnDoeWithNewName.json"));
        assertThat(response.getStatus(), is(equalTo(HttpCode.FORBIDDEN.getCode())));
    }

    @Test
    @RunAsClient
    public void updateValidCustomerTryingToChangeType() {
        final Long userId = addUserAndGetId("customerJohnDoe.json");
        findUserAndAssertResponseWithUser(userId, johnDoe());

        final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/" + userId).putWithFile(
                getPathFileRequest(PATH_RESOURCE, "updateCustomerJohnDoeWithNewType.json"));
        assertThat(response.getStatus(), is(equalTo(HttpCode.INTERNAL_ERROR.getCode())));
    }

    @Test
    @RunAsClient
    public void updateValidCustomerTryingToChangePassword() {
        final Long userId = addUserAndGetId("customerJohnDoe.json");
        findUserAndAssertResponseWithUser(userId, johnDoe());

        assertThat(authenticate(johnDoe().getEmail(), johnDoe().getPassword()), is(equalTo(true)));
        assertThat(authenticate(johnDoe().getEmail(), "111111"), is(equalTo(false)));

        final Response response = resourceClient.user(johnDoe()).resourcePath(PATH_RESOURCE + "/" + userId)
                .putWithFile(
                        getPathFileRequest(PATH_RESOURCE, "updateCustomerJohnDoeWithNewPassword.json"));
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));

        assertThat(authenticate(johnDoe().getEmail(), johnDoe().getPassword()), is(equalTo(true)));
        assertThat(authenticate(johnDoe().getEmail(), "111111"), is(equalTo(false)));
    }

    @Test
    @RunAsClient
    public void updateUserWithEmailBelongingToOtherUser() {
        final Long userId = addUserAndGetId("customerJohnDoe.json");
        addUserAndGetId("customerMary.json");

        final Response responseUpdate = resourceClient.user(admin()).resourcePath(PATH_RESOURCE + "/" + userId)
                .putWithFile(
                        getPathFileRequest(PATH_RESOURCE, "customerMary.json"));
        assertThat(responseUpdate.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(responseUpdate, "userAlreadyExists.json");
    }

    @Test
    @RunAsClient
    public void updateUserNotFound() {
        final Response response = resourceClient.user(admin()).resourcePath(PATH_RESOURCE + "/" + 999).putWithFile(
                getPathFileRequest(PATH_RESOURCE, "customerJohnDoe.json"));
        assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
    }

    @Test
    @RunAsClient
    public void updatePasswordAsAdmin() {
        final Long userId = addUserAndGetId("customerJohnDoe.json");

        assertThat(authenticate(johnDoe().getEmail(), johnDoe().getPassword()), is(equalTo(true)));
        assertThat(authenticate(johnDoe().getEmail(), "111111"), is(equalTo(false)));

        final Response response = resourceClient.user(admin()).resourcePath(PATH_RESOURCE + "/" + userId + "/password")
                .putWithContent(getJsonWithPassword("111111"));
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));

        assertThat(authenticate(johnDoe().getEmail(), johnDoe().getPassword()), is(equalTo(false)));
        assertThat(authenticate(johnDoe().getEmail(), "111111"), is(equalTo(true)));
    }

    @Test
    @RunAsClient
    public void updatePasswordLoggedCustomerAsCustomer() {
        final Long userId = addUserAndGetId("customerJohnDoe.json");

        assertThat(authenticate(johnDoe().getEmail(), johnDoe().getPassword()), is(equalTo(true)));
        assertThat(authenticate(johnDoe().getEmail(), "111111"), is(equalTo(false)));

        final Response response = resourceClient.user(johnDoe())
                .resourcePath(PATH_RESOURCE + "/" + userId + "/password")
                .putWithContent(getJsonWithPassword("111111"));
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));

        assertThat(authenticate(johnDoe().getEmail(), johnDoe().getPassword()), is(equalTo(false)));
        assertThat(authenticate(johnDoe().getEmail(), "111111"), is(equalTo(true)));
    }

    @Test
    @RunAsClient
    public void updatePasswordButNotTheLoggedCustomer() {
        final Long userId = addUserAndGetId("customerJohnDoe.json");
        addUserAndGetId("customerMary.json");

        final Response response = resourceClient.user(mary()).resourcePath(PATH_RESOURCE + "/" + userId + "/password")
                .putWithContent(getJsonWithPassword("111111"));
        assertThat(response.getStatus(), is(equalTo(HttpCode.FORBIDDEN.getCode())));
    }

    @Test
    @RunAsClient
    public void findUserByIdNotFound() {
        final Response response = resourceClient.user(admin()).resourcePath(PATH_RESOURCE + "/" + 999).get();
        assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
    }

    @Test
    @RunAsClient
    public void findByFilterPaginatingAndOrderingDescendingByName() {
        resourceClient.resourcePath("DB/").delete();
        resourceClient.resourcePath("DB/" + PATH_RESOURCE).postWithContent("");
        resourceClient.user(admin());

        // first page
        Response response = resourceClient.resourcePath(PATH_RESOURCE + "?page=0&per_page=2&sort=-name").get();
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertResponseContainsTheUsers(response, 3, mary(), johnDoe());

        // second page
        response = resourceClient.resourcePath(PATH_RESOURCE + "?page=1&per_page=2&sort=-name").get();
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertResponseContainsTheUsers(response, 3, admin());
    }

    private void addUserWithValidationError(final String requestFileName, final String responseFileName) {
        final Response response = resourceClient.user(null).resourcePath(PATH_RESOURCE)
                .postWithFile(getPathFileRequest(PATH_RESOURCE, requestFileName));
        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, responseFileName);
    }

    private void assertResponseContainsTheUsers(final Response response, final int expectedTotalRecords,
                                                final User... expectedUsers) {

        final JsonArray usersList = IntTestUtils.assertJsonHasTheNumberOfElementsAndReturnTheEntries(response,
                expectedTotalRecords, expectedUsers.length);

        for (int i = 0; i < expectedUsers.length; i++) {
            final User expectedUser = expectedUsers[i];
            assertThat(usersList.get(i).getAsJsonObject().get("name").getAsString(),
                    is(equalTo(expectedUser.getName())));
        }
    }

    private boolean authenticate(final String email, final String password) {
        final Response response = resourceClient.user(null).resourcePath(PATH_RESOURCE + "/authenticate")
                .postWithContent(getJsonWithEmailAndPassword(email, password));
        return response.getStatus() == HttpCode.OK.getCode();
    }

    private Long addUserAndGetId(final String fileName) {
        resourceClient.user(null);
        return IntTestUtils.addElementWithFileAndGetId(resourceClient, PATH_RESOURCE, PATH_RESOURCE, fileName);
    }

    private void findUserAndAssertResponseWithUser(final Long userIdToBeFound, final User expectedUser) {
        resourceClient.user(admin());
        final String bodyResponse = IntTestUtils.findById(resourceClient, PATH_RESOURCE, userIdToBeFound);
        assertResponseWithUser(bodyResponse, expectedUser);
    }

    private void assertResponseWithUser(final String bodyResponse, final User expectedUser) {
        final JsonObject userJson = JsonReader.readAsJsonObject(bodyResponse);
        assertThat(userJson.get("id").getAsLong(), is(notNullValue()));
        assertThat(userJson.get("name").getAsString(), is(equalTo(expectedUser.getName())));
        assertThat(userJson.get("email").getAsString(), is(equalTo(expectedUser.getEmail())));
        assertThat(userJson.get("type").getAsString(), is(equalTo(expectedUser.getUserType().toString())));
        assertThat(userJson.get("createdAt").getAsString(), is(notNullValue()));

        final JsonArray roles = userJson.getAsJsonArray("roles");
        assertThat(roles.size(), is(equalTo(expectedUser.getRoles().size())));
        for (int i = 0; i < roles.size(); i++) {
            final String actualRole = roles.get(i).getAsJsonPrimitive().getAsString();
            final String expectedRole = expectedUser.getRoles().get(i).toString();
            assertThat(actualRole, is(equalTo(expectedRole)));
        }
    }

    private void assertJsonResponseWithFile(final Response response, final String fileName) {
        assertJsonMatchesFileContent(response.readEntity(String.class), getPathFileResponse(PATH_RESOURCE, fileName));
    }

}