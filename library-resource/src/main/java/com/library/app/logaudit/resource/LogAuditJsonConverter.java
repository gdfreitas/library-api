package com.library.app.logaudit.resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.library.app.common.json.EntityJsonConverter;
import com.library.app.common.utils.DateUtils;
import com.library.app.logaudit.model.LogAudit;
import com.library.app.user.model.User;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LogAuditJsonConverter implements EntityJsonConverter<LogAudit> {

    @Override
    public LogAudit convertFrom(final String json) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public JsonElement convertToJsonElement(final LogAudit logAudit) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", logAudit.getId());
        jsonObject.addProperty("createdAt", DateUtils.formatDateTime(logAudit.getCreatedAt()));
        jsonObject.add("user", getUserAsJsonElement(logAudit.getUser()));
        jsonObject.addProperty("action", logAudit.getAction().toString());
        jsonObject.addProperty("element", logAudit.getElement());

        return jsonObject;
    }

    private JsonElement getUserAsJsonElement(final User user) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", user.getId());
        jsonObject.addProperty("name", user.getName());
        return jsonObject;
    }

}
