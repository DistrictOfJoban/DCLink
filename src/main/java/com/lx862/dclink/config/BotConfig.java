package com.lx862.dclink.config;

import com.google.gson.*;

import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class BotConfig extends Config {
    private static BotConfig instance;
    private boolean cacheMember;
    private int statusRefreshInterval = 20;
    public boolean outboundEnabled = true;
    public boolean inboundEnabled = true;

    public final List<String> statuses = new ArrayList<>();

    public BotConfig() {
        super(CONFIG_ROOT.resolve("config.json"));
    }

    public static BotConfig getInstance() {
        if(instance == null) {
            instance = new BotConfig();
        }
        return instance;
    }

    @Override
    public boolean load() {
        statuses.clear();

        if(!Files.exists(configFile)) {
            boolean saved = save();
            if(saved) {
                return load();
            } else {
                return false;
            }
        } else {
            try {
                final JsonObject jsonConfig = new JsonParser().parse(String.join("", Files.readAllLines(configFile))).getAsJsonObject();

                if(jsonConfig.has("statuses")) {
                    jsonConfig.get("statuses").getAsJsonArray().forEach(jsonElement -> {
                        statuses.add(jsonElement.getAsString());
                    });
                }

                if(jsonConfig.has("statusRefreshInterval")) {
                    statusRefreshInterval = jsonConfig.get("statusRefreshInterval").getAsInt();
                }

                if(jsonConfig.has("cacheMember")) {
                    cacheMember = jsonConfig.get("cacheMember").getAsBoolean();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean generate() {
        return save();
    }

    @Override
    public boolean save() {
        ensureRootFolderExist();
        JsonObject jsonObject = new JsonObject();
        JsonArray statusesArray = new JsonArray();
        for(String status : statuses) {
            statusesArray.add(status);
        }
        jsonObject.add("statuses", statusesArray);
        jsonObject.addProperty("statusRefreshInterval", statusRefreshInterval);
        jsonObject.addProperty("cacheMember", cacheMember);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new FileWriter(configFile.toString())) {
            gson.toJson(jsonObject, writer);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean getCacheMember() {
        return cacheMember;
    }

    public int getStatusRefreshInterval() {
        return statusRefreshInterval;
    }
}
