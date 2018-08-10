package com.library.app.logaudit.interceptor;

import com.library.app.logaudit.model.LogAudit;
import com.library.app.logaudit.repository.LogAuditRepository;
import com.library.app.user.exception.UserNotFoundException;
import com.library.app.user.services.UserServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.security.Principal;

import static java.util.Objects.nonNull;

public class LogAuditInterceptor {

    @Inject
    private LogAuditRepository repository;

    @Inject
    private UserServices userServices;

    @Inject
    private Principal principal;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        logger.debug("Interceptor being executed...");

        Object toReturn = context.proceed();

        try {
            processAuditableAnnotation(context);
        } catch (final UserNotFoundException ex) {
            logger.info("No user found for " + principal.getName());
        }

        return toReturn;
    }

    private void processAuditableAnnotation(InvocationContext context) throws UserNotFoundException {
        Auditable auditable = context.getMethod().getAnnotation(Auditable.class);

        if (nonNull(auditable)) {
            String elementName = context.getParameters()[0].getClass().getSimpleName();
            LogAudit logAudit = new LogAudit(userServices.findByEmail(principal.getName()), auditable.action(), elementName);
            logger.debug("Creating log audit {}", logAudit);
            repository.add(logAudit);
        }
    }

}
