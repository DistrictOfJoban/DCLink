package com.lx.dclink.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.lx.dclink.DCLink;
import com.lx.dclink.data.BridgeEntry;
import com.lx.dclink.util.EmbedParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BridgeConfig extends BaseConfig {
    public final Path CUSTOM_EMBED_PATH = CONFIG_ROOT.resolve("embeds");
    public List<BridgeEntry> entries;
    public BridgeConfig(Path configFile) {
        super(configFile);
        this.entries = new ArrayList<>();
    }

    @Override
    public abstract boolean load();

    @Override
    public abstract boolean generate();

    @Override
    public abstract boolean save();

    public void generateDefaultEmbed() {
        try {
            CONFIG_ROOT.resolve("embeds").toFile().mkdirs();
        } catch (Exception e) {
            DCLink.LOGGER.error("[DCLink] Failed to create embeds folder! Please check the config folder permission.");
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
        defaultEmbeds.put("serverCrashed",
                new EmbedBuilder()
                        .setTitle(":warning: Server crashed!")
                        .setDescription("`{reason}`")
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
        defaultEmbeds.put("changeDimension",
                new EmbedBuilder()
                        .setDescription(":cyclone: **{player.name}** has warped to **{world.name}**")
                        .build()
        );
        defaultEmbeds.put("playerAdvancement",
                new EmbedBuilder()
                        .setDescription(":medal: **{player.name}** has achieved **{advancement}**! (*{advancementDetails}*")
                        .build()
        );

        for(Map.Entry<String, MessageEmbed> entry : defaultEmbeds.entrySet()) {
            String fileName = entry.getKey() + ".json";
            MessageEmbed embed = entry.getValue();
            JsonArray fileContent = EmbedParser.toJson(embed);

            try (Writer writer = new FileWriter(CUSTOM_EMBED_PATH.resolve(fileName).toString())) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(fileContent, writer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
