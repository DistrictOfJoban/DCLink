package com.lx.dclink.config;

import com.google.gson.*;
import com.lx.dclink.DCLink;
import com.lx.dclink.data.DCEntry;
import com.lx.dclink.data.EmbedGenerator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscordConfig extends BaseConfig {
    private static final Path CONFIG_PATH = CONFIG_ROOT.resolve("discord.json");
    public static final Path CUSTOM_DC_EMBED_PATH = CONFIG_ROOT.resolve("embeds");
    public static final HashMap<String, JsonArray> customEmbedsList = new HashMap<>();
    public static List<DCEntry> entries = new ArrayList<>();

    public static boolean load() {
        entries.clear();
        loadCustomEmbeds();
        if(!Files.exists(CONFIG_PATH)) {
            DCLink.LOGGER.warn("Cannot find the Discord config file (MC -> DC)!");
            return false;
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

    private static void loadCustomEmbeds() {
        customEmbedsList.clear();

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
        } else {
            DCLink.LOGGER.info("Message Embeds does not exist, generating them...");
            try {
                CONFIG_ROOT.resolve("embeds").toFile().mkdirs();
            } catch (Exception e) {
                DCLink.LOGGER.error("Failed to create embeds folder! Please check the config folder permission.");
                return;
            }

            HashMap<String, MessageEmbed> defaultEmbeds = new HashMap<>();
            defaultEmbeds.put("serverStarting",
                    new EmbedBuilder()
                    .setDescription(":clock10: Server starting...")
                            .build()
            );
            defaultEmbeds.put("serverStarted",
                    new EmbedBuilder()
                            .setDescription(":white_check_mark: Server started (Took **{time}**)")
                            .build()
            );
            defaultEmbeds.put("serverStopping",
                    new EmbedBuilder()
                            .setDescription(":warning: Server is stopping (Was up for **{time|HH:mm:ss}**)")
                            .build()
            );
            defaultEmbeds.put("serverStopped",
                    new EmbedBuilder()
                            .setDescription(":x: Server no longer linked with Discord")
                            .build()
            );
            defaultEmbeds.put("playerJoined",
                    new EmbedBuilder()
                            .setDescription("{player.name} has joined the game.")
                            .setThumbnail("https://minotar.net/avatar/{player.name}/16")
                            .setFooter("{server.totalPlayerCount}/{server.maxPlayerCount} online.")
                            .build()
            );
            defaultEmbeds.put("playerLeft",
                    new EmbedBuilder()
                            .setDescription("{player.name} left the game. {reason}")
                            .setThumbnail("https://minotar.net/avatar/{player.name}/16")
                            .setFooter("{server.totalPlayerCount}/{server.maxPlayerCount} online.")
                            .build()
            );

            for(Map.Entry<String, MessageEmbed> entry : defaultEmbeds.entrySet()) {
                String fileName = entry.getKey() + ".json";
                MessageEmbed embed = entry.getValue();
                JsonArray fileContent = EmbedGenerator.toJson(embed);

                try (Writer writer = new FileWriter(CUSTOM_DC_EMBED_PATH.resolve(fileName).toString())) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    gson.toJson(fileContent, writer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            loadCustomEmbeds();
        }
    }

    public static JsonArray getEmbedJson(String key) {
        if(customEmbedsList.containsKey(key)) {
            return customEmbedsList.get(key);
        }
        return null;
    }
}
