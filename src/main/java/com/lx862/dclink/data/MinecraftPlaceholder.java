package com.lx862.dclink.data;

import com.lx862.dclink.util.StringHelper;
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
        addPlaceholder(objName, "difficulty", world.getDifficulty().getName());
        addPlaceholder(objName, "time", String.valueOf(world.getTimeOfDay()));
        addPlaceholder(objName, "playerCount", String.valueOf(world.getPlayers().size()));
        addPlaceholder(objName, "name", StringHelper.getWorldName(world));
    }

    private void setPlayerPlaceholder(String objName, ServerPlayerEntity player) {
        if(player.getWorld() != null) {
            Team team = player.getWorld().getScoreboard().getTeam(player.getGameProfile().getName());
            setTeamPlaceholder(objName + ".team", team);
        }

        addPlaceholder(objName, "displayName", player.getDisplayName().getString());
        addPlaceholder(objName, "name", player.getGameProfile().getName());
        addPlaceholder(objName, "ping", String.valueOf(player.networkHandler.getLatency()));
        addPlaceholder(objName, "gamemode", player.interactionManager.getGameMode() == GameMode.CREATIVE ? "Creative" : player.interactionManager.getGameMode() == GameMode.SURVIVAL ? "Survival" : player.interactionManager.getGameMode() == GameMode.ADVENTURE ? "Adventure" : "Spectator");
        addPlaceholder(objName, "x", String.valueOf(Math.round(player.getX())));
        addPlaceholder(objName, "y", String.valueOf(Math.round(player.getY())));
        addPlaceholder(objName, "z", String.valueOf(Math.round(player.getZ())));
        addPlaceholder(objName, "hp", String.valueOf(Math.round(player.getHealth())));
        addPlaceholder(objName, "heart", String.valueOf(new DecimalFormat("##.#").format(player.getHealth() / 2.0)));
    }

    private void setServerPlaceholder(String objName, MinecraftServer server) {
        if(server != null) {
            addPlaceholder(objName, "totalPlayerCount", server.getPlayerManager() == null ? null : String.valueOf(server.getCurrentPlayerCount()));
            addPlaceholder(objName, "version", server.getVersion());
            addPlaceholder(objName, "maxPlayerCount", server.getPlayerManager() == null ? null : String.valueOf(server.getMaxPlayerCount()));
        }
    }

    private void setTeamPlaceholder(String objName, Team team) {
        if(team == null) {
            addPlaceholder(objName, "name", null);
            addPlaceholder(objName, "prefix", null);
            addPlaceholder(objName, "suffix", null);
            addPlaceholder(objName, "displayName", null);
        } else {
            addPlaceholder(objName, "name", team.getName());
            addPlaceholder(objName, "prefix", team.getPrefix().getString());
            addPlaceholder(objName, "suffix", team.getSuffix().getString());
            addPlaceholder(objName, "displayName", team.getDisplayName().getString());
        }
    }
}
