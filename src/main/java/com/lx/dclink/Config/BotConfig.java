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
import java.util.List;

public class BotConfig {
    private static String token;
    private static boolean cacheMember;
    private static int statusRefreshInterval;
    private static boolean outboundEnabled = true;
    private static boolean inboundEnabled = true;
    private static final Collection<String> intents = new ArrayList<>();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("DCLink").resolve("config.json");
    public static final List<String> sendChannel = new ArrayList<>();
    public static final List<String> statuses = new ArrayList<>();

    public static boolean load() {
        sendChannel.clear();
        intents.clear();
        statuses.clear();

        if(!Files.exists(CONFIG_PATH)) {
            DCLink.LOGGER.warn("Cannot find the main bot config file!");
            return false;
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
                return false;
            }
        }
        return true;
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
}
