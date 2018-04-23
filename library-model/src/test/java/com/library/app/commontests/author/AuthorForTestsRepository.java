package com.library.app.commontests.author;

import com.library.app.author.model.Author;
import org.junit.Ignore;

import java.util.Arrays;
import java.util.List;

/**
 * @author gabriel.freitas
 */
@Ignore
public class AuthorForTestsRepository {

    public static Author robertMartin() {
        return new Author("Robert Martin");
    }

    public static Author jamesGosling() {
        return new Author("James Gosling");
    }

    public static Author martinFowler() {
        return new Author("Martin Fowler");
    }

    public static Author erichGamma() {
        return new Author("Erich Gamma");
    }

    public static Author richardHelm() {
        return new Author("Richard Helm");
    }

    public static Author ralphJohnson() {
        return new Author("Ralph Johnson");
    }

    public static Author johnVlissides() {
        return new Author("John Vlissides");
    }

    public static Author kentBeck() {
        return new Author("Kent Beck");
    }

    public static Author johnBrant() {
        return new Author("John Brant");
    }

    public static Author williamOpdyke() {
        return new Author("William Opdyke");
    }

    public static Author donRoberts() {
        return new Author("Don Roberts");
    }

    public static Author joshuaBloch() {
        return new Author("Joshua Bloch");
    }

    public static List<Author> allAuthors() {
        return Arrays.asList(robertMartin(), jamesGosling(), martinFowler(), erichGamma(), richardHelm(),
                ralphJohnson(), johnVlissides(), kentBeck(), johnBrant(), williamOpdyke(), donRoberts(), joshuaBloch());
    }

    public static Author authorWithId(final Author author, final Long id) {
        author.setId(id);
        return author;
    }

}
