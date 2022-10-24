package com.lx.dclink.Config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lx.dclink.DCLink;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class BotConfig {
    private static String token;
    private static boolean cacheMember;
    private static int statusRefreshInterval;
    private static boolean outboundEnabled = true;
    private static boolean inboundEnabled = true;
    private static final Collection<String> intents = new ArrayList<>();
    private static final HashMap<String, JsonArray> customEmbedsList = new HashMap<>();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("DCLink").resolve("config.json");
    private static final Path CUSTOM_DC_EMBED_PATH = FabricLoader.getInstance().getConfigDir().resolve("DCLink").resolve("embeds");
    public static final List<String> sendChannel = new ArrayList<>();
    public static final List<String> statuses = new ArrayList<>();

    public static boolean load() {
        boolean loadSuccessful = false;
        sendChannel.clear();
        intents.clear();
        statuses.clear();
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
                loadSuccessful = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(!Files.exists(CONFIG_PATH)) {
            DCLink.LOGGER.warn("Cannot find the main bot config file!");
            loadSuccessful = false;
        } else {
            try {
                final JsonObject jsonConfig = new JsonParser().parse(String.join("", Files.readAllLines(CONFIG_PATH))).getAsJsonObject();
                if(jsonConfig.has("token")) {
                    token = jsonConfig.get("token").getAsString();
                }

                if(jsonConfig.has("sendChannel")) {
                    JsonArray channels = jsonConfig.get("sendChannel").getAsJsonArray();
                    channels.forEach(jsonElement -> {
                        String channelId = jsonElement.getAsString();
                        sendChannel.add(channelId);
                    });
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
                loadSuccessful = false;
            }
        }
        return loadSuccessful;
    }

    public static String getToken() {
        return token;
    }

    public static boolean getCacheMember() {
        return cacheMember;
    }

    public static boolean getOutboundEnabled() {
        return outboundEnabled;
    }

    public static boolean getInboundEnabled() {
        return inboundEnabled;
    }

    public static void setOutboundEnabled(boolean outbound) {
        outboundEnabled = outbound;
    }

    public static void setInboundEnabled(boolean inbound) {
        inboundEnabled = inbound;
    }

    public static int getStatusRefreshInterval() {
        return statusRefreshInterval;
    }

    public static Collection<GatewayIntent> getIntents() {
        Collection<GatewayIntent> intentCollection = new ArrayList<>();

        for(String intentString : intents) {
            try {
                intentCollection.add(GatewayIntent.valueOf(intentString));
            } catch (Exception ignored) {
            }
        }

        return intentCollection;
    }

    public static JsonArray getEmbedJson(String key) {
        if(customEmbedsList.containsKey(key)) {
            return customEmbedsList.get(key);
        }
        return null;
    }
}
