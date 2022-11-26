package com.lx.dclink.Events;

import com.lx.dclink.Config.BotConfig;
import com.lx.dclink.Config.DiscordConfig;
import com.lx.dclink.DCLink;
import com.lx.dclink.Data.DCEntry;
import com.lx.dclink.Data.MinecraftPlaceholder;
import com.lx.dclink.Data.Placeholder;
import com.lx.dclink.DiscordBot;
import com.lx.dclink.Utils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.crash.CrashReport;

public class ServerEvent {
    public static long serverStartingTimestamp;
    public static long serverStartedTimestamp;

    public static void serverPrelaunch() {
        serverStartingTimestamp = System.currentTimeMillis();
    }

    public static void serverStarting(MinecraftServer server) {
        DiscordBot.load(BotConfig.getToken(), BotConfig.getIntents());

        for(DCEntry entry : DiscordConfig.entries) {
            DiscordBot.sendUniversalMessage(entry.message.serverStarting, null, entry.channelID, entry.allowMention, entry.enableEmoji);
        }
    }

    public static void serverStarted(MinecraftServer server) {
        serverStartedTimestamp = System.currentTimeMillis();
        DCLink.server = server;
        for(DCEntry entry : DiscordConfig.entries) {
            Placeholder placeholder = new MinecraftPlaceholder(null, server, null, null);
            placeholder.addTimePlaceholder("time", serverStartedTimestamp - serverStartingTimestamp);
            DiscordBot.sendUniversalMessage(entry.message.serverStarted, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
        }
    }

    public static void serverStopping(MinecraftServer server) {
        long serverStoppingTimestamp = System.currentTimeMillis();
        DCLink.server = null;

        for(DCEntry entry : DiscordConfig.entries) {
            Placeholder placeholder = new MinecraftPlaceholder(null, server, null, null);
            placeholder.addTimePlaceholder("time", serverStoppingTimestamp - serverStartedTimestamp);
            DiscordBot.sendUniversalMessage(entry.message.serverStopping, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
        }
    }

    public static void serverStopped(MinecraftServer server) {
        for(DCEntry entry : DiscordConfig.entries) {
            DiscordBot.sendUniversalMessage(entry.message.serverStopped, null, entry.channelID, entry.allowMention, entry.enableEmoji);
        }
        DiscordBot.disconnect();
    }

    public static void serverCrashed(CrashReport report) {
        for(DCEntry entry : DiscordConfig.entries) {
            Placeholder placeholder = new MinecraftPlaceholder();
            placeholder.addPlaceholder("reason", report.getMessage());
            DiscordBot.sendUniversalMessage(entry.message.serverCrashed, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
        }
    }
}
