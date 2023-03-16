package com.lx.dclink;

import com.lx.dclink.bridges.Discord;
import com.lx.dclink.commands.*;
import com.lx.dclink.config.DiscordConfig;
import com.lx.dclink.config.MinecraftConfig;
import com.lx.dclink.config.BotConfig;
import com.lx.dclink.data.MinecraftEntry;
import com.lx.dclink.events.ServerEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
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
	public static Discord bot;

	@Override
	public void onInitialize() {
		LOGGER.info("[DCLink] Loading Config");
		boolean allConfigLoaded = loadAllConfig();
		if(!allConfigLoaded) {
			LOGGER.warn("[DCLink] Not all config are loaded! Please check console for error.");
		}

		bot = new Discord(BotConfig.getInstance().getToken(), BotConfig.getInstance().getIntents());

		ServerLifecycleEvents.SERVER_STARTING.register((ServerEvent::serverStarting));
		ServerLifecycleEvents.SERVER_STARTED.register((ServerEvent::serverStarted));
		ServerLifecycleEvents.SERVER_STOPPING.register((ServerEvent::serverStopping));
		ServerLifecycleEvents.SERVER_STOPPED.register((ServerEvent::serverStopped));

		CommandManager.registerCommand((dispatcher) -> {
			dclink.register(dispatcher);
		});
	}

	public static boolean loadAllConfig() {
		return BotConfig.getInstance().load() && MinecraftConfig.getInstance().load() && DiscordConfig.getInstance().load();
	}

	public static void sendInGameMessage(List<MutableText> textToBeSent, MinecraftEntry entry) {
		if(server == null) return;

		List<String> dimensions = new ArrayList<>(entry.sendDimension);

		if(dimensions.isEmpty()) {
			//Send to all
			sendMessage(server.getPlayerManager().getPlayerList(), textToBeSent);
		} else {
			for(String worldKeyConfig : dimensions) {
				for(ServerWorld world : server.getWorlds()) {
					String worldKey = world.getRegistryKey().getValue().toString();
					if(worldKeyConfig.equals(worldKey)) {
						sendMessage(world.getPlayers(), textToBeSent);
					}
				}
			}
		}
	}

	public static void sendMessage(List<ServerPlayerEntity> players, List<MutableText> textToBeSent) {
		for(ServerPlayerEntity player : players) {
			for(MutableText text : textToBeSent) {
				player.sendMessage(text, false);
			}
		}
	}
}