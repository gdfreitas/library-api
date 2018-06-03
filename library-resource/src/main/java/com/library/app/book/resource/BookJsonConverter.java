package com.library.app.book.resource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.library.app.author.model.Author;
import com.library.app.author.resource.AuthorJsonConverter;
import com.library.app.book.model.Book;
import com.library.app.category.model.Category;
import com.library.app.category.resource.CategoryJsonConverter;
import com.library.app.common.json.EntityJsonConverter;
import com.library.app.common.json.JsonReader;

@ApplicationScoped
public class BookJsonConverter implements EntityJsonConverter<Book> {

	@Inject
	CategoryJsonConverter categoryJsonConverter;

	@Inject
	AuthorJsonConverter authorJsonConverter;

	@Override
	public Book convertFrom(final String json) {
		final JsonObject jsonObject = JsonReader.readAsJsonObject(json);

		final Book book = new Book();
		book.setTitle(JsonReader.getStringOrNull(jsonObject, "title"));
		book.setDescription(JsonReader.getStringOrNull(jsonObject, "description"));

		final Category category = new Category();
		category.setId(JsonReader.getLongOrNull(jsonObject, "categoryId"));
		book.setCategory(category);

		final JsonArray authorsIdsJsonArray = jsonObject.getAsJsonArray("authorsIds");
		if (authorsIdsJsonArray != null) {
			for (final JsonElement authorIdJsonElement : authorsIdsJsonArray) {
				final Author author = new Author();
				author.setId(authorIdJsonElement.getAsLong());
				book.addAuthor(author);
			}
		}

		book.setPrice(JsonReader.getDoubeOrNull(jsonObject, "price"));

		return book;
	}

	@Override
	public JsonElement convertToJsonElement(final Book book) {
		final JsonObject jsonObject = new JsonObject();

		jsonObject.addProperty("id", book.getId());
		jsonObject.addProperty("title", book.getTitle());
		jsonObject.addProperty("description", book.getDescription());
		jsonObject.add("category", categoryJsonConverter.convertToJsonElement(book.getCategory()));
		jsonObject.add("authors", authorJsonConverter.convertToJsonElement(book.getAuthors()));
		jsonObject.addProperty("price", book.getPrice());

		return jsonObject;
	}
}