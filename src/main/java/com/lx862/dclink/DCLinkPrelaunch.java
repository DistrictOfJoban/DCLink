package com.lx862.dclink;

import com.lx862.dclink.events.ServerEvent;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class DCLinkPrelaunch implements PreLaunchEntrypoint {

	@Override
	public void onPreLaunch() {
		ServerEvent.serverPrelaunch();
	}
}