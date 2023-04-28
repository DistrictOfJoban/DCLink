package com.lx.dclink.events;

import com.lx.dclink.bridges.BridgeManager;
import com.lx.dclink.DCLink;
import com.lx.dclink.data.BridgeEntry;
import com.lx.dclink.data.MinecraftPlaceholder;
import com.lx.dclink.data.Placeholder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.crash.CrashReport;

public class ServerEvent {
    public static long serverStartingTimestamp;
    public static long serverStartedTimestamp;

    public static void serverPrelaunch() {
        serverStartingTimestamp = System.currentTimeMillis();
    }

    public static void serverStarting(MinecraftServer server) {
        DCLink.server = server;
        BridgeManager.login();

        BridgeManager.forEach(bridge -> {
            for(BridgeEntry entry : bridge.getEntries()) {
                bridge.sendMessage(entry.message.serverStarting, null, entry.channelID, entry.allowMention, entry.enableEmoji);
            }
        });
    }

    public static void serverStarted(MinecraftServer server) {
        serverStartedTimestamp = System.currentTimeMillis();
        Placeholder placeholder = new MinecraftPlaceholder(null, server, null, null);
        placeholder.addTimePlaceholder("time", serverStartedTimestamp - serverStartingTimestamp);

        BridgeManager.forEach(bridge -> {
            for(BridgeEntry entry : bridge.getEntries()) {
                bridge.sendMessage(entry.message.serverStarted, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
            }
        });
    }

    public static void serverStopping(MinecraftServer server) {
        long serverStoppingTimestamp = System.currentTimeMillis();
        Placeholder placeholder = new MinecraftPlaceholder(null, server, null, null);
        placeholder.addTimePlaceholder("time", serverStoppingTimestamp - serverStartedTimestamp);

        BridgeManager.forEach(bridge -> {
            for(BridgeEntry entry : bridge.getEntries()) {
                bridge.sendMessage(entry.message.serverStopping, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
            }
        });
    }

    public static void serverStopped(MinecraftServer server) {
        BridgeManager.forEach(bridge -> {
            for(BridgeEntry entry : bridge.getEntries()) {
                bridge.sendMessage(entry.message.serverStopped, null, entry.channelID, entry.allowMention, entry.enableEmoji);
            }
        });
        DCLink.server = null;
        BridgeManager.logout();
    }

    public static void serverCrashed(CrashReport crashReport) {
        Placeholder placeholder = new MinecraftPlaceholder();
        placeholder.addPlaceholder("reason", crashReport.getMessage());

        BridgeManager.forEach(bridge -> {
            for(BridgeEntry entry : bridge.getEntries()) {
                bridge.sendMessage(entry.message.serverCrashed, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
            }
        });
    }
}
