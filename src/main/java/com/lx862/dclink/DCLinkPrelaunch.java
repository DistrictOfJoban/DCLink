package com.lx862.dclink;

import com.lx862.dclink.minecraft.events.ServerManager;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class DCLinkPrelaunch implements PreLaunchEntrypoint {

	@Override
	public void onPreLaunch() {
		ServerManager.serverPrelaunch();
	}
}