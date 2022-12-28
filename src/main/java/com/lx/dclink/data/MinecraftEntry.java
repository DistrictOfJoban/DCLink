package com.lx.dclink.data;

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
            JsonObject msg = jsonEntry.get("messages").getAsJsonObject();

            if(msg.has("relay")) {
                minecraftEntry.message.relay = msg.get("relay").getAsString();
            }

            if(msg.has("relayEdited")) {
                minecraftEntry.message.relayEdited = msg.get("relayEdited").getAsString();
            }

            if(msg.has("relayReply")) {
                minecraftEntry.message.relayReplied = msg.get("relayReply").getAsString();
            }

            if(msg.has("relayDeleted")) {
                minecraftEntry.message.relayDeleted = msg.get("relayDeleted").getAsString();
            }

            if(msg.has("reactionAdd")) {
                minecraftEntry.message.reactionAdded = msg.get("reactionAdd").getAsString();
            }

            if(msg.has("reactionRemove")) {
                minecraftEntry.message.reactionRemoved = msg.get("reactionRemove").getAsString();
            }

            if(msg.has("attachments")) {
                minecraftEntry.message.attachments = msg.get("attachments").getAsString();
            }
        }
        return minecraftEntry;
    }
}
