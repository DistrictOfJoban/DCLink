package com.lx.dclink.events;

import com.lx.dclink.config.DiscordConfig;
import com.lx.dclink.DCLink;
import com.lx.dclink.data.DiscordEntry;
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
        DCLink.bot.login();
        for(DiscordEntry entry : DiscordConfig.getInstance().entries) {
            DCLink.bot.sendMessage(entry.message.serverStarting, null, entry.channelID, entry.allowMention, entry.enableEmoji);
        }
    }

    public static void serverStarted(MinecraftServer server) {
        serverStartedTimestamp = System.currentTimeMillis();
        DCLink.server = server;
        for(DiscordEntry entry : DiscordConfig.getInstance().entries) {
            Placeholder placeholder = new MinecraftPlaceholder(null, server, null, null);
            placeholder.addTimePlaceholder("time", serverStartedTimestamp - serverStartingTimestamp);
            DCLink.bot.sendMessage(entry.message.serverStarted, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
        }
    }

    public static void serverStopping(MinecraftServer server) {
        long serverStoppingTimestamp = System.currentTimeMillis();

        for(DiscordEntry entry : DiscordConfig.getInstance().entries) {
            Placeholder placeholder = new MinecraftPlaceholder(null, server, null, null);
            placeholder.addTimePlaceholder("time", serverStoppingTimestamp - serverStartedTimestamp);
            DCLink.bot.sendMessage(entry.message.serverStopping, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
        }
    }

    public static void serverStopped(MinecraftServer server) {
        for(DiscordEntry entry : DiscordConfig.getInstance().entries) {
            DCLink.bot.sendMessage(entry.message.serverStopped, null, entry.channelID, entry.allowMention, entry.enableEmoji);
        }
        DCLink.server = null;
        DCLink.bot.disconnect();
    }

    public static void serverCrashed(CrashReport crashReport) {
        for(DiscordEntry entry : DiscordConfig.getInstance().entries) {
            Placeholder placeholder = new MinecraftPlaceholder();
            placeholder.addPlaceholder("reason", crashReport.getMessage());
            DCLink.bot.sendMessage(entry.message.serverCrashed, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
        }
    }
}
