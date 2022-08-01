package com.lx.dclink.Events;

import com.lx.dclink.Config.DiscordConfig;
import com.lx.dclink.DCLink;
import com.lx.dclink.Data.ContentType;
import com.lx.dclink.Data.DCEntry;
import com.lx.dclink.DiscordBot;
import net.minecraft.server.MinecraftServer;

public class ServerEvent {
    public static long serverStartingTimestamp;
    public static long serverStartedTimestamp;

    public static void serverStarting(MinecraftServer server) {
        serverStartingTimestamp = System.currentTimeMillis();

        for(DCEntry entry : DiscordConfig.entries) {
            if(!entry.contentType.contains(ContentType.SERVER)) continue;
            DiscordBot.sendSimpleEmbed(entry.message.getServerStartingMessage(), entry.channelID);
        }
    }

    public static void serverStarted(MinecraftServer server) {
        serverStartedTimestamp = System.currentTimeMillis();
        DCLink.server = server;
        for(DCEntry entry : DiscordConfig.entries) {
            if(!entry.contentType.contains(ContentType.SERVER)) continue;
            DiscordBot.sendSimpleEmbed(entry.message.getServerStartedMessage(server, serverStartedTimestamp - serverStartingTimestamp), entry.channelID);
        }
    }

    public static void serverStopping(MinecraftServer server) {
        long serverStoppingTimestamp = System.currentTimeMillis();
        DCLink.server = null;

        for(DCEntry entry : DiscordConfig.entries) {
            if(!entry.contentType.contains(ContentType.SERVER)) continue;
            DiscordBot.sendSimpleEmbed(entry.message.getServerStoppingMessage(server, serverStoppingTimestamp - serverStartedTimestamp), entry.channelID);
        }
    }

    public static void serverStopped(MinecraftServer server) {
        for(DCEntry entry : DiscordConfig.entries) {
            if(!entry.contentType.contains(ContentType.SERVER)) continue;
            DiscordBot.sendSimpleEmbed(entry.message.getServerStoppedMessage(), entry.channelID);
        }
    }
}
