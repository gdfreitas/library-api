package com.library.app.common.json;

import com.google.gson.JsonObject;
import com.library.app.common.model.OperationResult;

/**
 * @author gabriel.freitas
 */
public final class OperationResultJsonWriter {

    private OperationResultJsonWriter() {
    }

    public static String toJson(final OperationResult operationResult) {
        return JsonWriter.writeToString(getJsonObject(operationResult));
    }

    private static Object getJsonObject(final OperationResult operationResult) {
        if (operationResult.isSuccess()) {
            return getJsonSucess(operationResult);
        }
        return getJsonError(operationResult);
    }

    private static Object getJsonSucess(final OperationResult operationResult) {
        return operationResult.getEntity();
    }

    private static JsonObject getJsonError(final OperationResult operationResult) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("errorIdentification", operationResult.getErrorIdentification());
        jsonObject.addProperty("errorDescription", operationResult.getErrorDescription());

        return jsonObject;
    }

}