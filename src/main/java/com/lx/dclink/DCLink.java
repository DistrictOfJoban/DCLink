package com.lx.dclink;

import com.lx.dclink.Commands.reload;
import com.lx.dclink.Config.DiscordConfig;
import com.lx.dclink.Config.MinecraftConfig;
import com.lx.dclink.Config.BotConfig;
import com.lx.dclink.Data.MCEntry;
import com.lx.dclink.Events.PlayerEvent;
import com.lx.dclink.Events.ServerEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class DCLink implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("dclink");
	public static MinecraftServer server = null;

	@Override
	public void onInitialize() {
		LOGGER.info("[DCLink] Loading Config");
		BotConfig.load();
		DiscordConfig.load();
		MinecraftConfig.load();

		ServerLifecycleEvents.SERVER_STARTING.register((ServerEvent::serverStarting));
		ServerLifecycleEvents.SERVER_STARTED.register((ServerEvent::serverStarted));
		ServerLifecycleEvents.SERVER_STOPPING.register((ServerEvent::serverStopping));
		ServerLifecycleEvents.SERVER_STOPPED.register((ServerEvent::serverStopped));

		ServerPlayConnectionEvents.JOIN.register(PlayerEvent::playerJoin);
		ServerPlayConnectionEvents.DISCONNECT.register(PlayerEvent::playerLeft);

		CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> {
			reload.register(dispatcher);
		}));
	}

	public static boolean loadAllConfig() {
		return BotConfig.load() && MinecraftConfig.load() && DiscordConfig.load();
	}

	public static void sendInGameMessage(List<MutableText> textToBeSent, MCEntry entry) {
		if(server == null) return;

		List<String> dimensions = new ArrayList<>(entry.sendDimension);

		if(dimensions.isEmpty()) {
			sendMessage(server.getPlayerManager().getPlayerList(), textToBeSent);
		} else {
			for(String dimensionKey : dimensions) {
				for(ServerWorld world : server.getWorlds()) {
					String worldKey = world.getRegistryKey().getValue().toString();
					if(dimensionKey.equals(worldKey)) {
						sendMessage(world.getPlayers(), textToBeSent);
					}
				}
			}
		}
	}

	public static void sendMessage(List<ServerPlayerEntity> players, List<MutableText> textToBeSent) {
		for(ServerPlayerEntity player : players) {
			for(MutableText text : textToBeSent) {
				player.sendSystemMessage(text, null);
			}
		}
	}
}