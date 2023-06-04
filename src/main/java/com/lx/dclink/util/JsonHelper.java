package com.lx.dclink.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonHelper {
    public static JsonObject getAsJsonObject(JsonElement jsonElement) {
        try {
            return jsonElement.getAsJsonObject();
        } catch (IllegalStateException ignored) {
            return null;
        }
    }

    public static String getString(JsonElement element) {
        return (element == null || element.isJsonNull()) ? null : element.getAsString();
    }

    public static String sanitize(String str) {
        if(str == null) return null;
        return str.replace("\"", "\\\"").replace("{", "\\{");
    }
}
