package com.library.app.common.appproperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.Set;

import static java.util.Objects.nonNull;

public class ApplicationPropertiesLoaderExtension implements Extension {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private Properties properties;

    public <T> void initializePropertyValues(@Observes final ProcessInjectionTarget<T> pit) {
        final AnnotatedType<T> at = pit.getAnnotatedType();

        final InjectionTarget<T> it = pit.getInjectionTarget();
        final InjectionTarget<T> wrapper = new InjectionTarget<T>() {

            @Override
            public T produce(final CreationalContext<T> ctx) {
                return it.produce(ctx);
            }

            @Override
            public void dispose(final T instance) {
                it.dispose(instance);
            }

            @Override
            public Set<InjectionPoint> getInjectionPoints() {
                return it.getInjectionPoints();
            }

            @Override
            public void inject(final T instance, final CreationalContext<T> ctx) {
                it.inject(instance, ctx);
                for (final Field field : at.getJavaClass().getDeclaredFields()) {
                    final PropertyValue annotation = field.getAnnotation(PropertyValue.class);
                    if (nonNull(annotation)) {
                        final String propertyName = annotation.name();
                        logger.debug("Setting property {} into field {}", propertyName, field.getName());
                        field.setAccessible(true);
                        final Class<?> fieldType = field.getType();
                        try {
                            if (fieldType == Integer.class) {
                                final String value = getPropertyValue(propertyName);
                                field.set(instance, Integer.valueOf(value));
                                logger.debug("Value of the field {} set with value {}", field.getName(), value);
                            } else {
                                logger.warn("Type of field not supported: {}", fieldType);
                            }
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            logger.error("Error while setting property into field", e);
                        }
                    }
                }
            }

            @Override
            public void postConstruct(final T instance) {
                it.postConstruct(instance);
            }

            @Override
            public void preDestroy(final T instance) {
                it.preDestroy(instance);
            }
        };
        pit.setInjectionTarget(wrapper);
    }

    private String getPropertyValue(final String propertyName) {
        if (properties == null) {
            properties = new Properties();
            try {
                properties.load(this.getClass().getClassLoader().getResourceAsStream("application.properties"));
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
        return properties.getProperty(propertyName);
    }

}