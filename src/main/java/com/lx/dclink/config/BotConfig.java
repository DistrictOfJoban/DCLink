package com.lx.dclink.config;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lx.dclink.DCLink;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BotConfig extends BaseConfig {
    private static BotConfig instance;
    private final Collection<String> intents = new ArrayList<>();
    private String token;
    private boolean cacheMember;
    private int statusRefreshInterval;
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
        intents.clear();
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

                if(jsonConfig.has("status")) {
                    jsonConfig.get("status").getAsJsonArray().forEach(jsonElement -> {
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
    public boolean save() {
        JsonObject jsonObject = new JsonObject();
        JsonArray statusesArray = new JsonArray();
        JsonArray intentsArray = new JsonArray();
        for(String status : statuses) {
            statusesArray.add(status);
        }
        for(String intent : intents) {
            intentsArray.add(intent);
        }
        jsonObject.addProperty("token", token);
        jsonObject.add("status", statusesArray);
        jsonObject.addProperty("statusRefreshInterval", statusRefreshInterval);
        jsonObject.add("intents", intentsArray);
        jsonObject.addProperty("cacheMember", cacheMember);
        Gson gson = new Gson();
        try (Writer writer = new FileWriter(configFile.toString())) {
            gson.toJson(jsonObject, writer);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public String getToken() {
        return token;
    }

    public boolean getCacheMember() {
        return cacheMember;
    }

    public int getStatusRefreshInterval() {
        return statusRefreshInterval;
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
