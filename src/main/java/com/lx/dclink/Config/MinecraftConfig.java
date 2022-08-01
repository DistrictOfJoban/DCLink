package com.lx.dclink.Config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lx.dclink.Data.MCEntry;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MinecraftConfig {
    public static List<MCEntry> entries = new ArrayList<>();
    private static final Path ConfigFile = FabricLoader.getInstance().getConfigDir().resolve("DCLink").resolve("minecraft.json");

    public static void load() {
        entries.clear();
        if(!Files.exists(ConfigFile)) return;

        try {
            final JsonArray jsonConfig = new JsonParser().parse(String.join("", Files.readAllLines(ConfigFile))).getAsJsonArray();
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

                    if(msg.has("relayDeleted")) {
                        entry.message.relayDeleted = msg.get("relayDeleted").getAsString();
                    }

                    if(msg.has("attachments")) {
                        entry.message.attachments = msg.get("attachments").getAsString();
                    }
                }

                entries.add(entry);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
