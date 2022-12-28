package com.lx.dclink.config;

import com.google.gson.*;
import com.lx.dclink.DCLink;
import com.lx.dclink.data.DiscordEntry;
import com.lx.dclink.util.EmbedParser;
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
    private static DiscordConfig instance;
//    private final Path CONFIG_PATH = CONFIG_ROOT.resolve("discord.json");
    public final Path CUSTOM_DC_EMBED_PATH = CONFIG_ROOT.resolve("embeds");
    public final HashMap<String, JsonArray> customEmbedsList = new HashMap<>();
    public List<DiscordEntry> entries = new ArrayList<>();

    public DiscordConfig() {
        super(CONFIG_ROOT.resolve("discord.json"));
    }

    public static DiscordConfig getInstance() {
        if(instance == null) {
            instance = new DiscordConfig();
        }
        return instance;
    }

    public boolean load() {
        entries.clear();
        loadCustomEmbeds();
        if(!Files.exists(configFile)) {
            DCLink.LOGGER.warn("Cannot find the Discord config file (MC -> DC)!");
            return false;
        }

        try {
            final JsonArray jsonConfig = new JsonParser().parse(String.join("", Files.readAllLines(configFile))).getAsJsonArray();
            jsonConfig.forEach(jsonElement -> {
                DiscordEntry discordEntry = DiscordEntry.fromJson(jsonElement);
                if(discordEntry != null) {
                    entries.add(discordEntry);
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean save() {
//        if(!Files.exists(CUSTOM_DC_EMBED_PATH)) {
//            DCLink.LOGGER.info("Message Embeds does not exist, generating...");
//            try {
//                CONFIG_ROOT.resolve("embeds").toFile().mkdirs();
//            } catch (Exception e) {
//                DCLink.LOGGER.error("Failed to create embeds folder! Please check the config folder permission.");
//                return false;
//            }
//
//            HashMap<String, MessageEmbed> defaultEmbeds = new HashMap<>();
//            defaultEmbeds.put("serverStarting",
//                    new EmbedBuilder()
//                            .setDescription(":clock10: Server starting...")
//                            .build()
//            );
//            defaultEmbeds.put("serverStarted",
//                    new EmbedBuilder()
//                            .setDescription(":white_check_mark: Server started (Took **{time}**)")
//                            .build()
//            );
//            defaultEmbeds.put("serverStopping",
//                    new EmbedBuilder()
//                            .setDescription(":warning: Server is stopping (Was up for **{time|HH:mm:ss}**)")
//                            .build()
//            );
//            defaultEmbeds.put("serverStopped",
//                    new EmbedBuilder()
//                            .setDescription(":x: Server no longer linked with Discord")
//                            .build()
//            );
//            defaultEmbeds.put("playerJoined",
//                    new EmbedBuilder()
//                            .setDescription("{player.name} has joined the game.")
//                            .setThumbnail("https://minotar.net/avatar/{player.name}/16")
//                            .setFooter("{server.totalPlayerCount}/{server.maxPlayerCount} online.")
//                            .build()
//            );
//            defaultEmbeds.put("playerLeft",
//                    new EmbedBuilder()
//                            .setDescription("{player.name} left the game. {reason}")
//                            .setThumbnail("https://minotar.net/avatar/{player.name}/16")
//                            .setFooter("{server.totalPlayerCount}/{server.maxPlayerCount} online.")
//                            .build()
//            );
//
//            for(Map.Entry<String, MessageEmbed> entry : defaultEmbeds.entrySet()) {
//                String fileName = entry.getKey() + ".json";
//                MessageEmbed embed = entry.getValue();
//                JsonArray fileContent = EmbedParser.toJson(embed);
//
//                try (Writer writer = new FileWriter(CUSTOM_DC_EMBED_PATH.resolve(fileName).toString())) {
//                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                    gson.toJson(fileContent, writer);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        return false;
    }

    private void loadCustomEmbeds() {
        customEmbedsList.clear();

        if (Files.exists(CUSTOM_DC_EMBED_PATH)) {
            try {
                File[] files = CUSTOM_DC_EMBED_PATH.toFile().listFiles();
                if (files != null) {
                    for (File file : files) {
                        String fileName = FilenameUtils.getBaseName(file.getName());
                        final JsonArray json = new JsonParser().parse(String.join("", Files.readAllLines(file.toPath()))).getAsJsonArray();
                        customEmbedsList.put(fileName, json);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            DCLink.LOGGER.info("Message Embeds does not exist, generating...");
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
                JsonArray fileContent = EmbedParser.toJson(embed);

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

    public JsonArray getEmbedJson(String key) {
        if(customEmbedsList.containsKey(key)) {
            return customEmbedsList.get(key);
        }
        return null;
    }
}
