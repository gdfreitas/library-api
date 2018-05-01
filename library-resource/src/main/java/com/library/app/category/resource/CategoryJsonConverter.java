package com.library.app.category.resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.library.app.category.model.Category;
import com.library.app.common.json.EntityJsonConverter;
import com.library.app.common.json.JsonReader;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author gabriel.freitas
 */
@ApplicationScoped
public class CategoryJsonConverter implements EntityJsonConverter<Category> {

    @Override
    public Category convertFrom(final String json) {
        final JsonObject jsonObject = JsonReader.readAsJsonObject(json);

        final Category category = new Category();
        category.setName(JsonReader.getStringOrNull(jsonObject, "name"));

        return category;
    }

    @Override
    public JsonElement convertToJsonElement(final Category category) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", category.getId());
        jsonObject.addProperty("name", category.getName());
        return jsonObject;
    }

}