package com.lx862.dclink;

import com.lx862.dclink.minecraft.MinecraftSource;
import com.lx862.dclink.config.BotConfig;
import com.lx862.dclink.config.DiscordConfig;
import com.lx862.dclink.config.MinecraftConfig;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DCLink implements ModInitializer {
	private static final MinecraftSource mcSource = new MinecraftSource();
	public static final Logger LOGGER = LogManager.getLogger("DCLink");

	@Override
	public void onInitialize() {
		LOGGER.info("[DCLink] Loading config...");
		boolean allConfigLoaded = loadAllConfig();
		if(!allConfigLoaded) {
			LOGGER.warn("[DCLink] Not all config are loaded! Please check console for error.");
		}

		mcSource.initialize();
	}

	public static MinecraftSource getMaster() {
		return mcSource;
	}

	public static boolean loadAllConfig() {
		return BotConfig.getInstance().load() && MinecraftConfig.getInstance().load() && DiscordConfig.getInstance().load();
	}
}