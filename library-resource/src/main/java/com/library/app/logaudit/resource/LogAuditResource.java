package com.library.app.logaudit.resource;

import com.google.gson.JsonElement;
import com.library.app.common.json.JsonUtils;
import com.library.app.common.json.JsonWriter;
import com.library.app.common.model.HttpCode;
import com.library.app.common.model.PaginatedData;
import com.library.app.logaudit.model.LogAudit;
import com.library.app.logaudit.model.filter.LogAuditFilter;
import com.library.app.logaudit.repository.LogAuditRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;


@Path("/logsaudit")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMINISTRATOR"})
public class LogAuditResource {

    @Inject
    LogAuditRepository logAuditRepository;

    @Inject
    LogAuditJsonConverter logAuditJsonConverter;

    @Context
    UriInfo uriInfo;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @GET
    public Response findByFilter() {
        final LogAuditFilter logAuditFilter = new LogAuditFilterExtractorFromUrl(uriInfo).getFilter();
        logger.debug("Finding auditing logs using filter: {}", logAuditFilter);

        final PaginatedData<LogAudit> logs = logAuditRepository.findByFilter(logAuditFilter);

        logger.debug("Found {} logs", logs.getNumberOfRows());

        final JsonElement jsonWithPagingAndEntries = JsonUtils.getJsonElementWithPagingAndEntries(logs,
                logAuditJsonConverter);
        return Response.status(HttpCode.OK.getCode()).entity(JsonWriter.writeToString(jsonWithPagingAndEntries))
                .build();
    }

}