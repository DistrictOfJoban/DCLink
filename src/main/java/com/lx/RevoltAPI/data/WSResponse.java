package com.lx.RevoltAPI.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

public class WSResponse {
    private final String type;
    private final String id;
    private final String nonce;
    private final JsonElement data;

    public WSResponse(String type, String id, @Nullable String nonce, JsonElement data) {
        this.type = type;
        this.id = id;
        this.nonce = nonce;
        this.data = data;
    }

    public WSResponse(JsonObject jsonObject) {
        this.type = jsonObject.get("type").getAsString();
        if(jsonObject.has("id")) {
            this.id = jsonObject.get("id").getAsString();
        } else {
            this.id = null;
        }

        if(jsonObject.has("nonce")) {
            this.nonce = jsonObject.get("nonce").getAsString();
        } else {
            this.nonce = null;
        }

        this.data = jsonObject;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getNonce() {
        return nonce;
    }

    public JsonElement getData() {
        return data;
    }
}
