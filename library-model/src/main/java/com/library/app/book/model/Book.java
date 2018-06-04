package com.library.app.book.model;

import com.library.app.author.model.Author;
import com.library.app.category.model.Category;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gabriel.freitas
 */
@Entity
@Table(name = "lib_book")
public class Book implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "may not be null")
    @Size(min = 10, max = 150)
    private String title;

    @NotNull(message = "may not be null")
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "lib_book_author",
            joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "author_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "author_id"}))
    @JoinColumn(name = "author_id")
    @OrderBy(value = "name")
    @NotNull(message = "may not be null")
    @Size(min = 1)
    private List<Author> authors;

    @Lob
    @NotNull(message = "may not be null")
    @Size(min = 10)
    private String description;

    @NotNull(message = "may not be null")
    private Double price;

    public Book() {
    }

    public Book(final Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(final Category category) {
        this.category = category;
    }

    public List<Author> getAuthors() {
        if (authors == null) {
            authors = new ArrayList<>();
        }
        return authors;
    }

    public void setAuthors(final List<Author> authors) {
        this.authors = authors;
    }

    public boolean addAuthor(final Author author) {
        if (!getAuthors().contains(author)) {
            getAuthors().add(author);
            return true;
        }
        return false;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(final Double price) {
        this.price = price;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Book other = (Book) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Book [id=" + id + ", title=" + title + ", price=" + price + "]";
    }
}
