package com.lx862.dclink.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BridgeContext {
    public List<String> channelID;
    public List<String> allowedDimension;
    public Map<String, String> emojiMap;
    public BridgeMessages message;
    public boolean allowMention;
    public boolean enableEmoji;

    public BridgeContext() {
        channelID = new ArrayList<>();
        allowedDimension = new ArrayList<>();
        emojiMap = new HashMap<>();
        message = new BridgeMessages();
    }

    public static BridgeContext fromJson(JsonElement jsonElement) {
        JsonObject jsonEntry = jsonElement.getAsJsonObject();
        if(jsonEntry == null) return null;

        BridgeContext bridgeContext = new BridgeContext();
        JsonElement channelID = jsonEntry.get("channelID");
        JsonElement dimensions = jsonEntry.get("dimensions");

        if(channelID != null && !channelID.isJsonNull()) {
            jsonEntry.get("channelID").getAsJsonArray().forEach(id -> {
                bridgeContext.channelID.add(id.getAsString());
            });
        }

        if(dimensions != null && !dimensions.isJsonNull()) {
            dimensions.getAsJsonArray().forEach(worldId -> {
                bridgeContext.allowedDimension.add(worldId.getAsString());
            });
        }

        if(jsonEntry.has("allowMention")) {
            bridgeContext.allowMention = jsonEntry.get("allowMention").getAsBoolean();
        }

        if(jsonEntry.has("enableEmoji")) {
            bridgeContext.enableEmoji = jsonEntry.get("enableEmoji").getAsBoolean();
        }

        if(jsonEntry.has("messages") && jsonEntry.get("messages").isJsonObject()) {
            bridgeContext.message = BridgeMessages.fromJson(jsonEntry.get("messages"));
        }

        if(jsonEntry.has("emojiMap")) {
            JsonArray emojiArray = jsonEntry.get("emojiMap").getAsJsonArray();
            emojiArray.forEach(jsonObject -> {
                JsonObject emojiEntry = jsonObject.getAsJsonObject();
                if(emojiEntry.has("name") && emojiEntry.has("id")) {
                    bridgeContext.emojiMap.put(emojiEntry.get("name").getAsString(), emojiEntry.get("id").getAsString());
                }
            });
        }
        return bridgeContext;
    }

    public static JsonObject toJson(BridgeContext entry) {
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
        rootObject.add("messages", BridgeMessages.toJson(entry.message));
        rootObject.add("emojiMap", emojiMap);

        return rootObject;
    }
}
