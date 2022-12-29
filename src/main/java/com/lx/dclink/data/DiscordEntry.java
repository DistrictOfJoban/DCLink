package com.lx.dclink.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lx.dclink.util.JsonHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscordEntry {
    public List<String> channelID;
    public List<String> allowedDimension;
    public Map<String, String> emojiMap;
    public DiscordMessages message;
    public boolean allowMention;
    public boolean enableEmoji;

    public DiscordEntry() {
        channelID = new ArrayList<>();
        allowedDimension = new ArrayList<>();
        emojiMap = new HashMap<>();
        message = new DiscordMessages();
    }

    public static DiscordEntry fromJson(JsonElement jsonElement) {
        JsonObject jsonEntry = JsonHelper.getAsJsonObject(jsonElement);
        if(jsonEntry == null) return null;

        DiscordEntry discordEntry = new DiscordEntry();
        JsonElement channelID = jsonEntry.get("channelID");
        JsonElement dimensions = jsonEntry.get("dimensions");

        if(channelID != null && !channelID.isJsonNull()) {
            jsonEntry.get("channelID").getAsJsonArray().forEach(id -> {
                discordEntry.channelID.add(id.getAsString());
            });
        }

        if(dimensions != null && !dimensions.isJsonNull()) {
            dimensions.getAsJsonArray().forEach(worldId -> {
                discordEntry.allowedDimension.add(worldId.getAsString());
            });
        }

        if(jsonEntry.has("allowMention")) {
            discordEntry.allowMention = jsonEntry.get("allowMention").getAsBoolean();
        }

        if(jsonEntry.has("enableEmoji")) {
            discordEntry.enableEmoji = jsonEntry.get("enableEmoji").getAsBoolean();
        }

        if(jsonEntry.has("messages") && jsonEntry.get("messages").isJsonObject()) {
            discordEntry.message = DiscordMessages.fromJson(jsonEntry.get("messages"));
        }

        if(jsonEntry.has("emojiMap")) {
            JsonArray emojiArray = jsonEntry.get("emojiMap").getAsJsonArray();
            emojiArray.forEach(jsonObject -> {
                JsonObject emojiEntry = jsonObject.getAsJsonObject();
                if(emojiEntry.has("name") && emojiEntry.has("id")) {
                    discordEntry.emojiMap.put(emojiEntry.get("name").getAsString(), emojiEntry.get("id").getAsString());
                }
            });
        }
        return discordEntry;
    }

    public static JsonObject toJson(DiscordEntry entry) {
        JsonObject rootObject = new JsonObject();
        JsonArray channelIDs = new JsonArray();
        JsonArray allowedDimension = new JsonArray();
        JsonArray emojiMap = new JsonArray();
        for(String channelId : entry.channelID) {
            channelIDs.add(channelId);
        }
        for(String dimension : entry.allowedDimension) {
            allowedDimension.add(dimension);
        }
        for(Map.Entry<String, String> emoji : entry.emojiMap.entrySet()) {
            JsonObject emojiObj = new JsonObject();
            emojiObj.addProperty("name", emoji.getKey());
            emojiObj.addProperty("id", emoji.getValue());
            emojiMap.add(emoji.getValue());
        }
        rootObject.add("channelID", channelIDs);
        rootObject.add("dimensions", allowedDimension);
        rootObject.addProperty("allowMention", entry.allowMention);
        rootObject.addProperty("enableEmoji", entry.enableEmoji);
        rootObject.add("messages", DiscordMessages.toJson(entry.message));
        rootObject.add("emojiMap", emojiMap);

        return rootObject;
    }
}
