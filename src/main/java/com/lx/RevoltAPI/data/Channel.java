package com.lx.RevoltAPI.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lx.RevoltAPI.API;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class Channel {
    private String channelType;
    private String id;
    private String name;
    private String description;
    private API api;

    public Channel(API api, String id, String name) {
        this.api = api;
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void sendMessage(String message) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("content", message);
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        CompletableFuture.runAsync(() -> {
            api.executePost("channels/" + id + "/messages", requestBody);
        });
    }

    public void sendMessage(String message, Collection<TextEmbed> textEmbeds) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("content", message);
        if(!textEmbeds.isEmpty()) {
            JsonArray jsonArray = new JsonArray();
            for(TextEmbed embed : textEmbeds) {
                jsonArray.add(embed.toJson());
            }
            jsonObject.add("embeds", jsonArray);
        }
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        CompletableFuture.runAsync(() -> {
            api.executePost("/channels/" + id + "/messages", requestBody);
        });
    }
}
