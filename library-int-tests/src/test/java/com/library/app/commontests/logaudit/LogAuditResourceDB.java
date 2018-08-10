package com.library.app.commontests.logaudit;

import com.library.app.logaudit.repository.LogAuditRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import static com.library.app.commontests.logaudit.LogAuditForTestsRepository.allLogs;
import static com.library.app.commontests.logaudit.LogAuditForTestsRepository.normalizeDependencies;

@Path("/DB/logsaudit")
public class LogAuditResourceDB {

    @Inject
    private LogAuditRepository logAuditRepository;

    @PersistenceContext
    private EntityManager em;

    @POST
    public void addAll() {
        allLogs().forEach((logAudit) -> logAuditRepository.add(normalizeDependencies(logAudit, em)));
    }

}