package com.library.app.logaudit.resource;

import static com.library.app.commontests.logaudit.LogAuditForTestsRepository.*;
import static com.library.app.commontests.utils.FileTestNameUtils.*;
import static com.library.app.commontests.utils.JsonTestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.library.app.common.model.HttpCode;
import com.library.app.common.model.PaginatedData;
import com.library.app.commontests.utils.ResourceDefinitions;
import com.library.app.logaudit.model.LogAudit;
import com.library.app.logaudit.model.filter.LogAuditFilter;
import com.library.app.logaudit.repository.LogAuditRepository;

public class LogAuditResourceUTest {

    private static final String PATH_RESOURCE = ResourceDefinitions.LOGAUDIT.getResourceName();

    private LogAuditResource resource;

    @Mock
    private LogAuditRepository repository;

    @Mock
    private UriInfo uriInfo;

    @Before
    public void initTestCase() {
        MockitoAnnotations.initMocks(this);

        resource = new LogAuditResource();

        resource.logAuditRepository = repository;
        resource.uriInfo = uriInfo;
        resource.logAuditJsonConverter = new LogAuditJsonConverter();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findByFilter() {
        final MultivaluedMap<String, String> multiMap = mock(MultivaluedMap.class);
        when(uriInfo.getQueryParameters()).thenReturn(multiMap);

        when(repository.findByFilter(anyObject())).thenReturn(new PaginatedData<>(3, getLogs()));

        final Response response = resource.findByFilter();
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertJsonResponseWithFile(response, "allLogs.json");
    }

    private List<LogAudit> getLogs() {
        final List<LogAudit> logs = allLogs();

        logAuditWithId(logs.get(0), 1L);
        logs.get(0).getUser().setId(1L);

        logAuditWithId(logs.get(1), 2L);
        logs.get(1).getUser().setId(1L);

        logAuditWithId(logs.get(2), 3L);
        logs.get(2).getUser().setId(2L);

        return logs;
    }

    private void assertJsonResponseWithFile(final Response response, final String fileName) {
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE, fileName));
    }

}
