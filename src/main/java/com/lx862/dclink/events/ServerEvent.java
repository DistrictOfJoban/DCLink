package com.lx862.dclink.events;

import com.lx862.dclink.bridges.BridgeManager;
import com.lx862.dclink.DCLink;
import com.lx862.dclink.data.BridgeEntry;
import com.lx862.dclink.data.MinecraftPlaceholder;
import com.lx862.dclink.data.Placeholder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.crash.CrashReport;

public class ServerEvent {
    public static long serverStartingTimestamp;
    public static long serverStartedTimestamp;

    public static void serverPrelaunch() {
        serverStartingTimestamp = System.currentTimeMillis();
    }

    public static void serverStarting(MinecraftServer server) {
        try {
            DCLink.server = server;

            BridgeManager.clearBridges();
            BridgeManager.addDefaultBridges();
            BridgeManager.login();

            BridgeManager.forEach(bridge -> {
                for(BridgeEntry entry : bridge.getEntries()) {
                    bridge.sendMessage(entry.message.serverStarting, null, entry.channelID, entry.allowMention, entry.enableEmoji);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void serverStarted(MinecraftServer server) {
        try {
            serverStartedTimestamp = System.currentTimeMillis();
            Placeholder placeholder = new MinecraftPlaceholder(null, server, null, null);
            placeholder.addTimePlaceholder("time", serverStartedTimestamp - serverStartingTimestamp);

            BridgeManager.forEach(bridge -> {
                for(BridgeEntry entry : bridge.getEntries()) {
                    bridge.sendMessage(entry.message.serverStarted, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
                }
                bridge.startStatus();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void serverStopping(MinecraftServer server) {
        try {
            long serverStoppingTimestamp = System.currentTimeMillis();
            Placeholder placeholder = new MinecraftPlaceholder(null, server, null, null);
            placeholder.addTimePlaceholder("time", serverStoppingTimestamp - serverStartedTimestamp);

            BridgeManager.forEach(bridge -> {
                for(BridgeEntry entry : bridge.getEntries()) {
                    bridge.sendMessage(entry.message.serverStopping, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void serverStopped(MinecraftServer server) {
        try {
            BridgeManager.forEach(bridge -> {
                for(BridgeEntry entry : bridge.getEntries()) {
                    bridge.sendMessage(entry.message.serverStopped, null, entry.channelID, entry.allowMention, entry.enableEmoji);
                }
            });
            DCLink.server = null;
            BridgeManager.logout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void serverCrashed(CrashReport crashReport) {
        try {
            Placeholder placeholder = new MinecraftPlaceholder();
            placeholder.addPlaceholder("reason", crashReport.getMessage());
            placeholder.addPlaceholder("stacktrace", crashReport.getCauseAsString());

            BridgeManager.forEach(bridge -> {
                for(BridgeEntry entry : bridge.getEntries()) {
                    bridge.sendMessage(entry.message.serverCrashed, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
