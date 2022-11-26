package com.lx.dclink;

import com.lx.dclink.events.ServerEvent;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class DCLinkPrelaunch implements PreLaunchEntrypoint {

	@Override
	public void onPreLaunch() {
		ServerEvent.serverPrelaunch();
	}
}