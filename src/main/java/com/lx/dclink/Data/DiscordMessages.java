package com.lx.dclink.Data;

import net.minecraft.advancement.Advancement;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.world.World;

import static com.lx.dclink.Data.DiscordFormatter.format;

public class DiscordMessages {
    public String serverStarting = ":clock10: Loading world...";
    public String serverStarted = ":white_check_mark: Server started (**{timer}** elapsed)";
    public String serverStopping = ":warning: Server is stopping (It was up for **{timer}**)";
    public String serverStopped = ":x: Server no longer linked with Discord";
    public String serverCrashed = ":warning: **Crash Exception Detected!\n```{reason}```**";
    public String relay = "**<{playerTeam}{playerName}>** {message}";
    public String command = "**{playerTeam}{playerName}**: {message}";
    public String playerJoin = "**{playerTeam}{playerName}** has joined the game.";
    public String playerLeft = "**{playerTeam}{playerName}** left the game.";
    public String playerDisconnectReason = "({reason})";
    public String changeDimension = "**{playerTeam}{playerName}** has warped to {worldName}";
    public String playerDeath = ":skull: **{playerTeam}{playerName}** {reason}";
    public String playerAdvancement = ":medal: **{playerTeam}{playerName}** has achieved **{advancement}**!\n*{advancementDetails}*";

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

    public String getPlayerLeftMessage(ServerPlayerEntity player, MinecraftServer server, World world) {
        return format(playerLeft, player, server, world);
    }

    public String getPlayerDisconnectReason(String reason) {
        if(reason.equals("Disconnected")) return "";
        return playerDisconnectReason.replace("{reason}", reason);
    }

    public String getPlayerMessage(String content, ServerPlayerEntity player, MinecraftServer server, World world) {
        return format(relay, player, server, world, null, content);
    }

    public String getCommandMessage(String content, ServerPlayerEntity player, MinecraftServer server, World world) {
        return format(command, player, server, world, null, content);
    }

    public String getDimensionChangeMessage(ServerPlayerEntity player, MinecraftServer server, World world) {
        return format(changeDimension, player, server, world);
    }

    public String getPlayerDeathMessage(ServerPlayerEntity player, DamageSource source, World world) {
        String cause = source.getDeathMessage(player).getString().replace(player.getGameProfile().getName(), "");
        String formatted = playerDeath.replace("{reason}", cause);
        return format(formatted, player, null, world);
    }

    public String getPlayerAdvancementMessage(ServerPlayerEntity player, World world, Advancement advancement) {
        String advancementName = playerAdvancement
                .replace("{advancement}", advancement.getDisplay().getTitle().getString())
                .replace("{advancementDetails}", advancement.getDisplay().getDescription().getString());
        return format(advancementName, player, null, world);
    }

    public String getServerCrashedMessage(CrashReport report) {
        return serverCrashed.replace("{reason}", report.getMessage());
    }
}
