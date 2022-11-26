package com.lx.dclink.data;

import com.lx.dclink.Utils;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.text.DecimalFormat;
import java.util.Iterator;

public class MinecraftPlaceholder extends Placeholder {

    public MinecraftPlaceholder() {
    }

    public MinecraftPlaceholder(ServerPlayerEntity player, MinecraftServer server, World targetWorld, String message) {
        if(targetWorld != null) {
            setWorldPlaceholder("world", targetWorld);
        }

        if(player != null) {
            setPlayerPlaceholder("player", player);
        }

        if(server != null) {
            setServerPlaceholder("server", server);

            Iterator<ServerWorld> worldList = server.getWorlds().iterator();

            for (Iterator<ServerWorld> it = worldList; it.hasNext();) {
                ServerWorld world = it.next();
                String worldID = world.getRegistryKey().getValue().toString();
                setWorldPlaceholder(worldID, world);
            }
        }

        if(message != null) {
            addPlaceholder("message", message);
        }
    }

    private void setWorldPlaceholder(String objName, World world) {
        placeholders.put(objName + ".difficulty", world.getDifficulty().getName());
        placeholders.put(objName + ".time", String.valueOf(world.getTimeOfDay()));
        placeholders.put(objName + ".playerCount", String.valueOf(world.getPlayers().size()));
        placeholders.put(objName + ".name", Utils.getWorldName(world));
    }

    private void setPlayerPlaceholder(String objName, ServerPlayerEntity player) {
        String playerTeam = "";

        if(player.world != null) {
            Team team = player.world.getScoreboard().getPlayerTeam(player.getGameProfile().getName());
            if(team != null && team.getPrefix() != null) {
                playerTeam = team.getPrefix().getString();
            }
        }

        placeholders.put(objName + ".name", player.getGameProfile().getName());
        placeholders.put(objName + ".team", playerTeam);
        placeholders.put(objName + ".ping", String.valueOf(player.pingMilliseconds));
        placeholders.put(objName + ".gamemode", player.interactionManager.getGameMode() == GameMode.CREATIVE ? "Creative" : player.interactionManager.getGameMode() == GameMode.SURVIVAL ? "Survival" : player.interactionManager.getGameMode() == GameMode.ADVENTURE ? "Adventure" : "Spectator");
        placeholders.put(objName + ".x", String.valueOf(Math.round(player.getX())));
        placeholders.put(objName + ".y", String.valueOf(Math.round(player.getY())));
        placeholders.put(objName + ".z", String.valueOf(Math.round(player.getZ())));
        placeholders.put(objName + ".hp", String.valueOf(Math.round(player.getHealth())));
        placeholders.put(objName + ".heart", String.valueOf(new DecimalFormat("##.#").format(player.getHealth() / 2.0)));
    }

    private void setServerPlaceholder(String objName, MinecraftServer server) {
        placeholders.put(objName + ".totalPlayerCount", String.valueOf(server.getCurrentPlayerCount()));
        placeholders.put(objName + ".version", server.getVersion());
        placeholders.put(objName + ".maxPlayerCount", String.valueOf(server.getMaxPlayerCount()));
    }
}
