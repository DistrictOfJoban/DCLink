package com.lx.dclink.Config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lx.dclink.Data.ContentType;
import com.lx.dclink.Data.DCEntry;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DiscordConfig {
    public static List<DCEntry> entries = new ArrayList<>();
    private static final Path ConfigFile = FabricLoader.getInstance().getConfigDir().resolve("DCLink").resolve("discord.json");

    public static void load() {
        entries.clear();
        if(!Files.exists(ConfigFile)) return;

        try {
            final JsonArray jsonConfig = new JsonParser().parse(String.join("", Files.readAllLines(ConfigFile))).getAsJsonArray();
            jsonConfig.forEach(jsonElement -> {
                DCEntry entry = new DCEntry();
                JsonObject jsonEntry = jsonElement.getAsJsonObject();
                if(jsonEntry.has("channelID")) {
                    jsonEntry.get("channelID").getAsJsonArray().forEach(id -> {
                        entry.channelID.add(id.getAsString());
                    });
                }

                if(jsonEntry.has("dimensions")) {
                    jsonEntry.get("dimensions").getAsJsonArray().forEach(worldId -> {
                        entry.allowedDimension.add(worldId.getAsString());
                    });
                }

                if(jsonEntry.has("contentType")) {
                    jsonEntry.get("contentType").getAsJsonArray().forEach(content -> {
                        try {
                            String typeString = content.getAsString();
                            ContentType type = ContentType.valueOf(typeString);
                            entry.contentType.add(type);
                        } catch (IllegalArgumentException ignored) {
                        }
                    });
                } else {
                    entry.contentType.add(ContentType.CHAT);
                }

                if(jsonEntry.has("playerHeadURL")) {
                    entry.thumbnailURL = jsonEntry.get("playerHeadURL").getAsString();
                }

                if(jsonEntry.has("allowMention")) {
                    entry.allowMention = jsonEntry.get("allowMention").getAsBoolean();
                }

                if(jsonEntry.has("enableEmoji")) {
                    entry.enableEmoji = jsonEntry.get("enableEmoji").getAsBoolean();
                }

                if(jsonEntry.has("messages")) {
                    JsonObject msg = jsonEntry.get("messages").getAsJsonObject();

                    if(msg.has("relay")) {
                        entry.message.relay = msg.get("relay").getAsString();
                    }

                    if(msg.has("serverStarting")) {
                        entry.message.serverStarting = msg.get("serverStarting").getAsString();
                    }

                    if(msg.has("serverStarted")) {
                        entry.message.serverStarted = msg.get("serverStarted").getAsString();
                    }

                    if(msg.has("serverStopping")) {
                        entry.message.serverStopping = msg.get("serverStopping").getAsString();
                    }

                    if(msg.has("serverStopped")) {
                        entry.message.serverStopped = msg.get("serverStopped").getAsString();
                    }

                    if(msg.has("playerJoin")) {
                        entry.message.playerJoin = msg.get("playerJoin").getAsString();
                    }

                    if(msg.has("playerLeft")) {
                        entry.message.playerLeft = msg.get("playerLeft").getAsString();
                    }

                    if(msg.has("changeDimension")) {
                        entry.message.changeDimension = msg.get("changeDimension").getAsString();
                    }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
