package com.library.app.commontests.utils;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Ignore;

import java.io.File;

/**
 * @author gabriel.freitas
 */
@Ignore
public class ArquillianTestUtils {

    public static WebArchive createDeploymentArchive() {
        return ShrinkWrap
                .create(WebArchive.class)
                .addPackages(true, "com.library.app")
                .addAsResource("persistence-integration.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource("jboss-web.xml")
                .addAsResource("application.properties")
                .addAsResource("META-INF/services/javax.enterprise.inject.spi.Extension")
                .setWebXML(new File("src/test/resources/web.xml"))
                .addAsLibraries(
                        Maven.resolver()
                                .loadPomFromFile("pom.xml")
                                .resolve("com.google.code.gson:gson", "org.mockito:mockito-core")
                                .withTransitivity().asFile());
    }

}
