package com.library.app.common.json;

import com.google.gson.JsonObject;
import com.library.app.common.model.OperationResult;

import static com.library.app.common.json.JsonWriter.writeToString;

/**
 * @author gabriel.freitas
 */
public class OperationResultJsonWriter {

    private OperationResultJsonWriter() {
    }

    public static String toJson(final OperationResult operationResult) {
        return writeToString(getJsonObject(operationResult));
    }

    private static Object getJsonObject(OperationResult operationResult) {
        if (operationResult.isSuccess()) {
            return getJsonSuccess(operationResult);
        }
        return getJsonError(operationResult);
    }

    private static Object getJsonSuccess(OperationResult operationResult) {
        return operationResult.getEntity();
    }

    private static JsonObject getJsonError(OperationResult operationResult) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("errorIdentification", operationResult.getErrorIdentification());
        jsonObject.addProperty("errorDescription", operationResult.getErrorDescription());
        return jsonObject;
    }

}
