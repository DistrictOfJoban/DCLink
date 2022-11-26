package com.lx.dclink.Config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lx.dclink.DCLink;
import com.lx.dclink.Data.MCEntry;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MinecraftConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("dclink").resolve("minecraft.json");
    public static List<MCEntry> entries = new ArrayList<>();

    public static boolean load() {
        entries.clear();
        if(!Files.exists(CONFIG_PATH)) {
            DCLink.LOGGER.warn("Cannot find the Minecraft config file (DC -> MC)!");
            return false;
        }

        try {
            final JsonArray jsonConfig = new JsonParser().parse(String.join("", Files.readAllLines(CONFIG_PATH))).getAsJsonArray();
            jsonConfig.forEach(jsonElement -> {
                MCEntry entry = new MCEntry();
                JsonObject jsonEntry = jsonElement.getAsJsonObject();
                if(jsonEntry.has("channelID")) {
                    jsonEntry.get("channelID").getAsJsonArray().forEach(id -> {
                        entry.channelID.add(id.getAsString());
                    });
                }

                if(jsonEntry.has("dimensions")) {
                    jsonEntry.get("dimensions").getAsJsonArray().forEach(worldId -> {
                        entry.sendDimension.add(worldId.getAsString());
                    });
                }

                if(jsonEntry.has("messages")) {
                    JsonObject msg = jsonEntry.get("messages").getAsJsonObject();

                    if(msg.has("relay")) {
                        entry.message.relay = msg.get("relay").getAsString();
                    }

                    if(msg.has("relayEdited")) {
                        entry.message.relayEdited = msg.get("relayEdited").getAsString();
                    }

                    if(msg.has("relayReply")) {
                        entry.message.relayReplied = msg.get("relayReply").getAsString();
                    }

                    if(msg.has("relayDeleted")) {
                        entry.message.relayDeleted = msg.get("relayDeleted").getAsString();
                    }

                    if(msg.has("reactionAdd")) {
                        entry.message.reactionAdded = msg.get("reactionAdd").getAsString();
                    }

                    if(msg.has("reactionRemove")) {
                        entry.message.reactionRemoved = msg.get("reactionRemove").getAsString();
                    }

                    if(msg.has("attachments")) {
                        entry.message.attachments = msg.get("attachments").getAsString();
                    }
                }

                entries.add(entry);
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
