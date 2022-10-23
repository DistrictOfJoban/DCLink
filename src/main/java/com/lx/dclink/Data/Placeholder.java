package com.lx.dclink.Data;

import com.lx.dclink.Util;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.HashMap;
import java.util.Map;

public class Placeholder {
    private final HashMap<String, String> placeholders;

    public Placeholder() {
        placeholders = new HashMap<>();
    }

    public static Placeholder getDefaultPlaceholder(ServerPlayerEntity player, MinecraftServer server, World world, Long time, String message) {
        String playerTeamString = "";

        if(world != null) {
            Team team = world.getScoreboard().getPlayerTeam(player.getGameProfile().getName());
            if(team != null && team.getPrefix() != null) {
                playerTeamString = team.getPrefix().getString();
            }
        }

        Placeholder placeholder = new Placeholder();

        if(player != null) {
            placeholder.addPlaceholder("playerName", player.getGameProfile().getName());
            placeholder.addPlaceholder("playerTeam", playerTeamString);
            placeholder.addPlaceholder("playerPing", String.valueOf(player.pingMilliseconds));
            placeholder.addPlaceholder("playerGameMode", player.interactionManager.getGameMode() == GameMode.CREATIVE ? "Creative" : player.interactionManager.getGameMode() == GameMode.SURVIVAL ? "Survival" : player.interactionManager.getGameMode() == GameMode.ADVENTURE ? "Adventure" : "Spectator");
            placeholder.addPlaceholder("playerX", String.valueOf(Math.round(player.getX())));
            placeholder.addPlaceholder("playerY", String.valueOf(Math.round(player.getY())));
            placeholder.addPlaceholder("playerZ", String.valueOf(Math.round(player.getZ())));
        }

        if(world != null) {
            placeholder.addPlaceholder("difficulty", world.getDifficulty().getName());
            placeholder.addPlaceholder("time", String.valueOf(world.getTimeOfDay()));
            placeholder.addPlaceholder("worldPlayerCount", String.valueOf(world.getPlayers().size()));
            placeholder.addPlaceholder("worldName", Util.getWorldName(world));
        }

        if(server != null) {
            placeholder.addPlaceholder("totalPlayerCount", String.valueOf(server.getCurrentPlayerCount()));
            placeholder.addPlaceholder("serverVersion", server.getVersion());
            placeholder.addPlaceholder("maxPlayerCount", String.valueOf(server.getMaxPlayerCount()));
        }

        if(time != null) {
            placeholder.addPlaceholder("timer", DurationFormatUtils.formatDuration(time, "HH:mm:ss"));
        }

        if(message != null) {
            placeholder.addPlaceholder("message", message);
        }
        return placeholder;
    }

    public void addPlaceholder(String key, String value) {
        placeholders.put(key, value);
    }

    public String parse(String original) {
        for(Map.Entry<String, String> entry : placeholders.entrySet()) {
            original = original.replaceAll("\\{" + entry.getKey() + "}", entry.getValue());
        }
        return original;
    }
}