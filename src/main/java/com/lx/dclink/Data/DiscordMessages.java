package com.lx.dclink.Data;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import static com.lx.dclink.Data.DiscordFormatter.format;

public class DiscordMessages {
    public String serverStarting = ":clock10: Loading world...";
    public String serverStarted = ":white_check_mark: Server started (**{timer}** elapsed)";
    public String serverStopping = ":warning: Server is stopping (It was up for **{timer}**)";
    public String serverStopped = ":x: Server no longer linked with Discord";
    public String relay = "**<{playerTeam}{playerName}>** {message}";
    public String command = "**{playerTeam}{playerName}**: {message}";
    public String playerJoin = "**{playerTeam}{playerName}** has joined the game.";
    public String playerLeft = "**{playerTeam}{playerName}** left the game.";
    public String changeDimension = "**{playerTeam}{playerName}** has warped to {worldName}";

    public String getServerStartingMessage() {
        return serverStarting;
    }

    public String getServerStartedMessage(MinecraftServer server, long duration) {
        return format(serverStarted, null, server, null, duration);
    }

    public String getServerStoppingMessage(MinecraftServer server, long duration) {
        return format(serverStopping, null, server, null, duration);
    }

    public String getServerStoppedMessage() {
        return serverStopped;
    }

    public String getPlayerJoinMessage(ServerPlayerEntity player, MinecraftServer server, World world) {
        return format(playerJoin, player, server, world);
    }

    public String getPlayerLeftMessage(ServerPlayerEntity player, MinecraftServer server, World world, String disconReason) {
        return format(playerLeft, player, server, world, disconReason);
    }

    public String getPlayerMessage(String content, ServerPlayerEntity player, MinecraftServer server, World world) {
        return format(relay, player, server, world, null, content, null);
    }

    public String getCommandMessage(String content, ServerPlayerEntity player, MinecraftServer server, World world) {
        return format(command, player, server, world, null, content, null);
    }

    public String getDimensionChangeMessage(ServerPlayerEntity player, MinecraftServer server, World world) {
        return format(changeDimension, player, server, world);
    }
}
