package com.library.app.commontests.book;

import static com.library.app.commontests.book.BookForTestsRepository.*;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.library.app.book.model.Book;
import com.library.app.book.model.filter.BookFilter;
import com.library.app.book.resource.BookJsonConverter;
import com.library.app.book.services.BookServices;
import com.library.app.common.json.JsonWriter;
import com.library.app.common.model.HttpCode;
import com.library.app.common.model.PaginatedData;

/**
 * @author gabriel.freitas
 */
@Path("/DB/books")
@Produces(MediaType.APPLICATION_JSON)
public class BookResourceDB {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private BookServices bookServices;

    @Inject
    private BookJsonConverter bookJsonConverter;

    @POST
    public void addAll() {
        allBooks().forEach((book) -> bookServices.add(normalizeDependencies(book, em)));
    }

    @GET
    @Path("/{title}")
    public Response findByTitle(@PathParam("title") final String title) {
        final BookFilter bookFilter = new BookFilter();
        bookFilter.setTitle(title);

        final PaginatedData<Book> books = bookServices.findByFilter(bookFilter);
        if (books.getNumberOfRows() > 0) {
            final Book book = books.getRow(0);
            final String bookAsJson = JsonWriter.writeToString(bookJsonConverter.convertToJsonElement(book));
            return Response.status(HttpCode.OK.getCode()).entity(bookAsJson).build();
        }

        return Response.status(HttpCode.NOT_FOUND.getCode()).build();
    }
}
