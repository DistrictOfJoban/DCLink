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
            JsonObject msg = jsonEntry.get("messages").getAsJsonObject();
            discordEntry.message.relay = JsonHelper.getString(msg.get("relay"));
            discordEntry.message.relayCommand = JsonHelper.getString(msg.get("relayCommand"));
            discordEntry.message.serverStarting = JsonHelper.getString(msg.get("serverStarting"));
            discordEntry.message.serverStarted = JsonHelper.getString(msg.get("serverStarted"));
            discordEntry.message.serverStopping = JsonHelper.getString(msg.get("serverStopping"));
            discordEntry.message.serverStopped = JsonHelper.getString(msg.get("serverStopped"));
            discordEntry.message.serverCrashed = JsonHelper.getString(msg.get("serverCrashed"));
            discordEntry.message.playerJoin = JsonHelper.getString(msg.get("playerJoin"));
            discordEntry.message.playerLeft = JsonHelper.getString(msg.get("playerLeft"));
            discordEntry.message.playerDeath = JsonHelper.getString(msg.get("playerDeath"));
            discordEntry.message.changeDimension = JsonHelper.getString(msg.get("changeDimension"));
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
}
