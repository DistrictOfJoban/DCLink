package com.lx.dclink.config;

import com.google.gson.*;
import com.lx.dclink.DCLink;
import com.lx.dclink.data.BridgeEntry;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.*;

public class DiscordConfig extends BridgeConfig {
    private static DiscordConfig instance;
    private String token = null;
    private final Collection<String> intents = new ArrayList<>();
    public final HashMap<String, JsonArray> customEmbedsList = new HashMap<>();

    public DiscordConfig() {
        super(CONFIG_ROOT.resolve("discord.json"));
    }

    public static DiscordConfig getInstance() {
        if(instance == null) {
            instance = new DiscordConfig();
        }
        return instance;
    }

    @Override
    public boolean load() {
        intents.clear();
        entries.clear();
        loadCustomEmbeds();
        if(!Files.exists(configFile)) {
            boolean saved = generate();
            if(saved) {
                return load();
            } else {
                return false;
            }
        }

        try {
            final JsonObject jsonConfig = new JsonParser().parse(String.join("", Files.readAllLines(configFile))).getAsJsonObject();
            if(jsonConfig.has("token")) {
                token = jsonConfig.get("token").getAsString();
            }

            if(jsonConfig.has("intents")) {
                JsonArray channels = jsonConfig.get("intents").getAsJsonArray();
                channels.forEach(jsonElement -> {
                    String intent = jsonElement.getAsString();
                    intents.add(intent);
                });
            }

            if(jsonConfig.has("entries")) {
                jsonConfig.get("entries").getAsJsonArray().forEach(jsonElement -> {
                    BridgeEntry bridgeEntry = BridgeEntry.fromJson(jsonElement);
                    if(bridgeEntry != null) {
                        entries.add(bridgeEntry);
                    }
                });
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean generate() {
        BridgeEntry defaultEntry = new BridgeEntry();
        defaultEntry.allowedDimension.add("minecraft:overworld");
        entries.add(defaultEntry);
        return save();
    }

    @Override
    public boolean save() {
        ensureRootFolderExist();
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        JsonArray intentsArray = new JsonArray();
        for(String intent : intents) {
            intentsArray.add(intent);
        }
        for(BridgeEntry entry : entries) {
            jsonArray.add(BridgeEntry.toJson(entry));
        }

        jsonObject.add("intents", intentsArray);
        jsonObject.add("entries", jsonArray);

        if(!Files.exists(CUSTOM_EMBED_PATH)) {
            DCLink.LOGGER.info("[DCLink] Message Embeds does not exist, generating...");
            generateDefaultEmbed();
        } else {
            // Write embed
            for(Map.Entry<String, JsonArray> entry : customEmbedsList.entrySet()) {
                String filename = entry.getKey();
                try (Writer writer = new FileWriter(CUSTOM_EMBED_PATH.resolve(filename + ".json").toString())) {
                    gson.toJson(entry.getValue(), writer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        try (Writer writer = new FileWriter(configFile.toString())) {
            gson.toJson(jsonObject, writer);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void loadCustomEmbeds() {
        customEmbedsList.clear();

        if (Files.exists(CUSTOM_EMBED_PATH)) {
            try {
                File[] files = CUSTOM_EMBED_PATH.toFile().listFiles();
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
            generateDefaultEmbed();
            loadCustomEmbeds();
        }
    }

    public String getToken() {
        return token;
    }

    public JsonArray getEmbedJson(String key) {
        if(customEmbedsList.containsKey(key)) {
            return customEmbedsList.get(key);
        }
        return null;
    }

    public Collection<GatewayIntent> getIntents() {
        Collection<GatewayIntent> intentCollection = new ArrayList<>();

        for(String intentString : intents) {
            try {
                intentCollection.add(GatewayIntent.valueOf(intentString));
            } catch (Exception ignored) {
            }
        }

        return intentCollection;
    }
}
