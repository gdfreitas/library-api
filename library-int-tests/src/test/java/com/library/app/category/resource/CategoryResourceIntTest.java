package com.library.app.category.resource;

import com.google.gson.JsonObject;
import com.library.app.common.json.JsonReader;
import com.library.app.common.model.HttpStatusCode;
import com.library.app.commontests.utils.JsonTestUtils;
import com.library.app.commontests.utils.ResourceClient;
import com.library.app.commontests.utils.ResourceDefinitions;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import java.io.File;
import java.net.URL;

import static com.library.app.commontests.category.CategoryForTestsRepository.java;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileRequest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author gabriel.freitas
 */
@RunWith(Arquillian.class)
public class CategoryResourceIntTest {

    private static final String PATH_RESOURCE = ResourceDefinitions.CATEGORY.getResourceName();

    @ArquillianResource
    private URL url;

    private ResourceClient resourceClient;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap
                .create(WebArchive.class)
                .addPackages(true, "com.library.app")
                .addAsResource("persistence-integration.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setWebXML(new File("src/test/resources/web.xml"))
                .addAsLibraries(
                        Maven.resolver()
                                .resolve("com.google.code.gson:gson:2.3.1", "org.mockito:mockito-core:1.9.5")
                                .withTransitivity()
                                .asFile()
                );
    }

    @Before
    public void initTestCase() {
        this.resourceClient = new ResourceClient(url);
    }

    @Test
    @RunAsClient
    public void addValidCategoryAndFindIt() {
        final Response response = resourceClient
                .resourcePath(PATH_RESOURCE)
                .postWithFile(
                        getPathFileRequest(PATH_RESOURCE, "category.json")
                );

        assertThat(response.getStatus(), is(equalTo(HttpStatusCode.CREATED.getStatusCode())));
        final Long id = JsonTestUtils.getIdFromJson(response.readEntity(String.class));

        final Response responseGet = resourceClient.resourcePath(PATH_RESOURCE + "/" + id).get();
        assertThat(responseGet.getStatus(), is(equalTo(HttpStatusCode.OK.getStatusCode())));

        final JsonObject categoryAsJson = JsonReader.readAsJsonObject(responseGet.readEntity(String.class));
        assertThat(JsonReader.getStringOrNull(categoryAsJson, "name"), is(equalTo(java().getName())));
    }

}
