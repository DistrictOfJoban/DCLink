package com.lx.dclink.Config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lx.dclink.DCLink;
import com.lx.dclink.Data.DCEntry;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DiscordConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("dclink").resolve("discord.json");
    static final HashMap<String, JsonArray> customEmbedsList = new HashMap<>();
    static final Path CUSTOM_DC_EMBED_PATH = FabricLoader.getInstance().getConfigDir().resolve("dclink").resolve("embeds");
    public static List<DCEntry> entries = new ArrayList<>();

    public static boolean load() {
        entries.clear();
        customEmbedsList.clear();
        if(!Files.exists(CONFIG_PATH)) {
            DCLink.LOGGER.warn("Cannot find the Discord config file (MC -> DC)!");
            return false;
        }

        if (Files.exists(DiscordConfig.CUSTOM_DC_EMBED_PATH)) {
            try {
                File[] files = DiscordConfig.CUSTOM_DC_EMBED_PATH.toFile().listFiles();
                if (files != null) {
                    for (File file : files) {
                        String fileName = FilenameUtils.getBaseName(file.getName());
                        final JsonArray json = new JsonParser().parse(String.join("", Files.readAllLines(file.toPath()))).getAsJsonArray();
                        DiscordConfig.customEmbedsList.put(fileName, json);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            final JsonArray jsonConfig = new JsonParser().parse(String.join("", Files.readAllLines(CONFIG_PATH))).getAsJsonArray();
            jsonConfig.forEach(jsonElement -> {
                DCEntry entry = new DCEntry();
                JsonObject jsonEntry = jsonElement.getAsJsonObject();
                JsonElement channelID = jsonEntry.get("channelID");
                JsonElement dimensions = jsonEntry.get("dimensions");
                if(channelID != null && !channelID.isJsonNull()) {
                    jsonEntry.get("channelID").getAsJsonArray().forEach(id -> {
                        entry.channelID.add(id.getAsString());
                    });
                }

                if(dimensions != null && !dimensions.isJsonNull()) {
                    dimensions.getAsJsonArray().forEach(worldId -> {
                        entry.allowedDimension.add(worldId.getAsString());
                    });
                }

                if(jsonEntry.has("allowMention")) {
                    entry.allowMention = jsonEntry.get("allowMention").getAsBoolean();
                }

                if(jsonEntry.has("enableEmoji")) {
                    entry.enableEmoji = jsonEntry.get("enableEmoji").getAsBoolean();
                }

                if(jsonEntry.has("messages") && jsonEntry.get("messages").isJsonObject()) {
                    JsonObject msg = jsonEntry.get("messages").getAsJsonObject();
                    entry.message.relay = getString(msg.get("relay"));
                    entry.message.relayCommand = getString(msg.get("relayCommand"));
                    entry.message.serverStarting = getString(msg.get("serverStarting"));
                    entry.message.serverStarted = getString(msg.get("serverStarted"));
                    entry.message.serverStopping = getString(msg.get("serverStopping"));
                    entry.message.serverStopped = getString(msg.get("serverStopped"));
                    entry.message.serverCrashed = getString(msg.get("serverCrashed"));
                    entry.message.playerJoin = getString(msg.get("playerJoin"));
                    entry.message.playerLeft = getString(msg.get("playerLeft"));
                    entry.message.playerDeath = getString(msg.get("playerDeath"));
                    entry.message.changeDimension = getString(msg.get("changeDimension"));
                }

                if(jsonEntry.has("emojiMap")) {
                    JsonArray emojiArray = jsonEntry.get("emojiMap").getAsJsonArray();
                    emojiArray.forEach(jsonObject -> {
                        JsonObject emojiEntry = jsonObject.getAsJsonObject();
                        if(emojiEntry.has("name") && emojiEntry.has("id")) {
                            entry.emojiMap.put(emojiEntry.get("name").getAsString(), emojiEntry.get("id").getAsString());
                        }
                    });
                }

                entries.add(entry);
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String getString(JsonElement element) {
        return (element == null || element.isJsonNull()) ? null : element.getAsString();
    }

    public static JsonArray getEmbedJson(String key) {
        if(customEmbedsList.containsKey(key)) {
            return customEmbedsList.get(key);
        }
        return null;
    }
}
