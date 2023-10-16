package com.lx862.dclink.config;

import com.google.gson.*;
import com.lx862.dclink.DCLink;
import com.lx862.dclink.data.BridgeEntry;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class RevoltConfig extends BridgeConfig {
    private static RevoltConfig instance;
    private String token = null;

    public final HashMap<String, JsonArray> customEmbedsList = new HashMap<>();

    public RevoltConfig() {
        super(CONFIG_ROOT.resolve("revolt.json"));
    }

    public static RevoltConfig getInstance() {
        if(instance == null) {
            instance = new RevoltConfig();
        }
        return instance;
    }

    @Override
    public boolean load() {
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

            final JsonArray entryList = jsonConfig.get("entries").getAsJsonArray();
            entryList.forEach(jsonElement -> {
                BridgeEntry bridgeEntry = BridgeEntry.fromJson(jsonElement);
                if(bridgeEntry != null) {
                    entries.add(bridgeEntry);
                }
            });
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
        for(BridgeEntry entry : entries) {
            jsonArray.add(BridgeEntry.toJson(entry));
        }

        jsonObject.add("entries", jsonArray);

        if(!Files.exists(CUSTOM_EMBED_PATH)) {
            DCLink.LOGGER.info("[DCLink] Message Embeds does not exist, generating...");
            generateDefaultEmbed();
        } else {
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
}
