package com.library.app.author.resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.library.app.author.model.Author;
import com.library.app.common.json.EntityJsonConverter;
import com.library.app.common.json.JsonReader;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author gabriel.freitas
 */
@ApplicationScoped
public class AuthorJsonConverter implements EntityJsonConverter<Author> {

    @Override
    public Author convertFrom(final String json) {
        final JsonObject jsonObject = JsonReader.readAsJsonObject(json);

        final Author author = new Author();
        author.setName(JsonReader.getStringOrNull(jsonObject, "name"));

        return author;
    }

    @Override
    public JsonElement convertToJsonElement(final Author author) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", author.getId());
        jsonObject.addProperty("name", author.getName());
        return jsonObject;
    }

}