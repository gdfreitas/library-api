package com.library.app.commontests.utils;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;

/**
 * @author gabriel.freitas
 */
@Path("/DB")
public class DBResource {

    @Inject
    private TestRepositoryEJB tesRepositoryEJB;

    @DELETE
    public void deleteAll() {
        tesRepositoryEJB.deleteAll();
    }

}
