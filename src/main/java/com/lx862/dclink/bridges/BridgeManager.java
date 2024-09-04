package com.lx862.dclink.bridges;

import com.google.gson.JsonArray;
import com.lx862.dclink.DCLink;
import com.lx862.dclink.config.BotConfig;
import com.lx862.dclink.config.DiscordConfig;
import com.lx862.dclink.config.RevoltConfig;
import com.lx862.dclink.data.MinecraftPlaceholder;
import com.lx862.dclink.data.Placeholder;
import com.lx862.dclink.data.bridge.StatusManager;
import com.lx862.dclink.util.EmbedParser;
import com.lx862.dclink.util.StringHelper;
import com.lx862.vendorneutral.texts.embed.TextEmbed;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BridgeManager {
    private static final Pattern EMBED_PATTERN = Pattern.compile("(<<<.+>>>)");
    private static final List<Bridge> bridges = new ArrayList<>();

    public static void addDefaultBridges() {
        Bridge discordBridge = new DiscordBridge(DiscordConfig.getInstance(), DCLink.getMcSource());
        Bridge revoltBridge = new RevoltBridge(RevoltConfig.getInstance(), DCLink.getMcSource());
        if(discordBridge.isValid()) BridgeManager.addBridge(discordBridge);
        if(revoltBridge.isValid()) BridgeManager.addBridge(revoltBridge);
    }

    public static void clearBridges() {
        bridges.clear();
    }

    public static void addBridge(Bridge bridge) {
        bridges.add(bridge);
    }

    public static void forEach(Consumer<Bridge> callback) {
        for(Bridge bridge : bridges) {
            callback.accept(bridge);
        }
    }

    public static void login() {
        for(Bridge bridge : bridges) {
            bridge.login();
        }
    }

    public static void logout() {
        for(Bridge bridge : bridges) {
            bridge.stopStatus();
            bridge.disconnect();
        }
    }

    public static void startStatus() {
        if(BotConfig.getInstance().statuses.isEmpty()) return;

        BridgeManager.forEach(Bridge::stopStatus);
        StatusManager.statusTimer = new Timer();
        StatusManager.statusTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!DCLink.getMcSource().alive()) {
                    StatusManager.stopTimer();
                    return;
                }

                StatusManager.nextStatus();
                Placeholder placeholder = new MinecraftPlaceholder(null, DCLink.getMcSource().getServer(), null, null);
                String status = BotConfig.getInstance().statuses.get(StatusManager.getCurrentStatusIndex());
                String formattedStatus = placeholder.parse(status);

                BridgeManager.forEach(bridge -> {
                    bridge.updateStatus(formattedStatus);
                });
            }
        }, 0, BotConfig.getInstance().getStatusRefreshInterval() * 1000L);
    }

    public static void sendMessage(Bridge bridge, String template, Placeholder placeholder, List<String> channelList, boolean allowMention, boolean enableEmoji) {
        if(!bridge.isReady() || !BotConfig.getInstance().outboundEnabled || StringHelper.notValidString(template)) return;

        ArrayList<TextEmbed> embedToBeSent = new ArrayList<>();
        Matcher matcher = EMBED_PATTERN.matcher(template);

        if(matcher.find()) {
            template = template.replace(matcher.group(0), "");
            String embedName = matcher.group(0).replace("<<<", "").replace(">>>", "");
            JsonArray embedJson = DiscordConfig.getInstance().getEmbedJson(embedName);
            if(embedJson != null) {
                embedToBeSent.addAll(EmbedParser.fromJson(placeholder, embedJson));
            }
        }

        String finalMessage = placeholder == null ? template : placeholder.parse(template);

        for(String channelId : channelList) {
            bridge.handleMessage(finalMessage, channelId, embedToBeSent, enableEmoji, allowMention);
        }
    }
}
