package com.lx.RevoltAPI.data.accounts;

import com.google.gson.JsonObject;

public class User {
    private final String username;
    private final String id;
    public User(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public User(JsonObject jsonObject) {
        this.id = jsonObject.get("_id").getAsString();
        this.username = jsonObject.get("username").getAsString();
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
