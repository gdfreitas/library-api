package com.library.app.logaudit.resource;

import com.google.gson.JsonArray;
import com.library.app.common.model.HttpCode;
import com.library.app.common.utils.DateUtils;
import com.library.app.commontests.utils.ArquillianTestUtils;
import com.library.app.commontests.utils.IntTestUtils;
import com.library.app.commontests.utils.ResourceClient;
import com.library.app.commontests.utils.ResourceDefinitions;
import com.library.app.logaudit.model.LogAudit;
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
import java.util.List;

import static com.library.app.commontests.logaudit.LogAuditForTestsRepository.allLogs;
import static com.library.app.commontests.user.UserForTestsRepository.admin;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Arquillian.class)
public class LogAuditResouceIntTest {

    private static final String PATH_RESOURCE = ResourceDefinitions.LOGAUDIT.getResourceName();
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

        resourceClient.resourcePath("DB/" + ResourceDefinitions.USER.getResourceName()).postWithContent("");

        resourceClient.user(admin());
    }

    @Test
    @RunAsClient
    public void findByFilterPaginatingAndOrderingDescendingByName() {
        resourceClient.resourcePath("DB/" + PATH_RESOURCE).postWithContent("");

        final List<LogAudit> allLogs = allLogs();

        // first page
        Response response = resourceClient.resourcePath(PATH_RESOURCE + "?page=0&per_page=2&sort=-createdAt").get();
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertResponseContainsTheLogs(response, 3, allLogs.get(2), allLogs.get(1));

        // second page
        response = resourceClient.resourcePath(PATH_RESOURCE + "?page=1&per_page=2&sort=-createdAt").get();
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertResponseContainsTheLogs(response, 3, allLogs.get(0));
    }

    private void assertResponseContainsTheLogs(final Response response, final int expectedTotalRecords,
                                               final LogAudit... expectedLogsAudit) {

        final JsonArray logsList = IntTestUtils.assertJsonHasTheNumberOfElementsAndReturnTheEntries(response,
                expectedTotalRecords, expectedLogsAudit.length);

        for (int i = 0; i < expectedLogsAudit.length; i++) {
            final LogAudit expectedLogAudit = expectedLogsAudit[i];
            assertThat(logsList.get(i).getAsJsonObject().get("createdAt").getAsString(),
                    is(equalTo(DateUtils.formatDateTime(expectedLogAudit.getCreatedAt()))));
        }
    }

}
