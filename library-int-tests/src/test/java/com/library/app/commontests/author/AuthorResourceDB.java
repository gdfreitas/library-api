package com.library.app.commontests.author;

import com.library.app.author.services.AuthorServices;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static com.library.app.commontests.author.AuthorForTestsRepository.allAuthors;

/**
 * @author gabriel.freitas
 */
@Path("/DB/authors")
@Produces(MediaType.APPLICATION_JSON)
public class AuthorResourceDB {

    @Inject
    private AuthorServices authorServices;

    @POST
    public void addAll() {
        allAuthors().forEach(authorServices::add);
    }
}
