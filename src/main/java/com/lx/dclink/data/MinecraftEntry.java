package com.lx.dclink.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lx.dclink.util.JsonHelper;

import java.util.ArrayList;
import java.util.List;

public class MinecraftEntry {
    public List<String> channelID;
    public List<String> sendDimension;
    public MinecraftMessages message;

    public MinecraftEntry() {
        channelID = new ArrayList<>();
        sendDimension = new ArrayList<>();
        message = new MinecraftMessages();
    }

    public static MinecraftEntry fromJson(JsonElement element) {
        JsonObject jsonEntry = JsonHelper.getAsJsonObject(element);
        if(jsonEntry == null) return null;

        MinecraftEntry minecraftEntry = new MinecraftEntry();
        if(jsonEntry.has("channelID")) {
            jsonEntry.get("channelID").getAsJsonArray().forEach(id -> {
                minecraftEntry.channelID.add(id.getAsString());
            });
        }

        if(jsonEntry.has("dimensions")) {
            jsonEntry.get("dimensions").getAsJsonArray().forEach(worldId -> {
                minecraftEntry.sendDimension.add(worldId.getAsString());
            });
        }

        if(jsonEntry.has("messages")) {
            minecraftEntry.message = MinecraftMessages.fromJson(jsonEntry.getAsJsonObject("messages"));
        }
        return minecraftEntry;
    }

    public static JsonElement toJson(MinecraftEntry entry) {
        JsonObject rootObject = new JsonObject();
        JsonArray channelID = new JsonArray();
        JsonArray allowedDimension = new JsonArray();

        for(String id : entry.channelID) {
            channelID.add(id);
        }
        for(String dimension : entry.sendDimension) {
            allowedDimension.add(dimension);
        }
        rootObject.add("channelID", channelID);
        rootObject.add("dimensions", allowedDimension);
        rootObject.add("messages", MinecraftMessages.toJson(entry.message));
        return rootObject;
    }
}
