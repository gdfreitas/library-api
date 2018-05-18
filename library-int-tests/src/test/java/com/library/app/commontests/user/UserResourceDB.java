package com.library.app.commontests.user;

import static com.library.app.commontests.user.UserForTestsRepository.*;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.library.app.user.services.UserServices;

/**
 * @author gabriel.freitas
 */
@Path("/DB/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResourceDB {

    @Inject
    private UserServices userServices;

    @POST
    public void addAll() {
        allUsers().forEach(userServices::add);
    }

    @POST
    @Path("/admin")
    public void addAdmin() {
        userServices.add(admin());
    }

}
