package com.lx.dclink.Data;

import com.lx.dclink.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.concurrent.TimeUnit;

public class DiscordFormatter {
    public static String format(String content, ServerPlayerEntity player, MinecraftServer server, World world, Long time, String message) {
        String modifiedContent = content;
        String playerTeam;
        if(world == null || world.getScoreboard().getPlayerTeam(player.getGameProfile().getName()) == null) {
            playerTeam = "";
        } else {
            playerTeam = world.getScoreboard().getPlayerTeam(player.getGameProfile().getName()).getPrefix().getString();
        }

        if(player != null) {
            modifiedContent = modifiedContent
                .replace("{playerName}", player.getGameProfile().getName())
                .replace("{playerTeam}", playerTeam)
                .replace("{playerPing}", String.valueOf(player.pingMilliseconds))
                .replace("{playerGameMode}", player.interactionManager.getGameMode() == GameMode.CREATIVE ? "Creative" : player.interactionManager.getGameMode() == GameMode.SURVIVAL ? "Survival" : player.interactionManager.getGameMode() == GameMode.ADVENTURE ? "Adventure" : "Spectator")
                .replace("{playerX}", String.valueOf(Math.round(player.getX())))
                .replace("{playerY}", String.valueOf(Math.round(player.getY())))
                .replace("{playerZ}", String.valueOf(Math.round(player.getZ())));
            }

        if(world != null) {
            modifiedContent = modifiedContent
                .replace("{difficulty}", world.getDifficulty().getName())
                .replace("{time}", String.valueOf(world.getTimeOfDay()))
                .replace("{worldPlayerCount}", String.valueOf(world.getPlayers().size()))
                .replace("{worldName}", Util.getWorldName(world));
        }

        if(server != null) {
            modifiedContent = modifiedContent
                .replace("{totalPlayerCount}", String.valueOf(server.getCurrentPlayerCount()))
                .replace("{serverVersion}", server.getVersion())
                .replace("{maxPlayerCount}", String.valueOf(server.getMaxPlayerCount()));
        }

        if(time != null) {
            int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
            modifiedContent = modifiedContent
                .replace("{timer}", String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60));
        }

        if(message != null) {
            modifiedContent = modifiedContent
                .replace("{message}", message);
        }

        return modifiedContent;
    }

    public static String format(String content, ServerPlayerEntity player, MinecraftServer server, World world, Long time) {
        return format(content, player, server, world, time, null);
    }

    public static String format(String content, ServerPlayerEntity player, MinecraftServer server, World world) {
        return format(content, player, server, world, null, null);
    }
}
