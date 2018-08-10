package com.library.app.common.appproperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.Properties;

@ApplicationScoped
public class ApplicationProperties {

    private Properties properties;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @PostConstruct
    public void init() {
        try {
            properties = new Properties();
            properties.load(ApplicationProperties.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (final IOException e) {
            logger.error("Error while reading properties file", e);
            throw new IllegalArgumentException(e);
        }
    }

    public int getDaysBeforeOrderExpiration() {
        return Integer.valueOf(properties.getProperty("days-before-order-expiration"));
    }

}
