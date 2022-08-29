package com.lx.dclink;

import com.lx.dclink.Events.ServerEvent;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class DCLinkPrelaunch implements PreLaunchEntrypoint {

	@Override
	public void onPreLaunch() {
		ServerEvent.serverPrelaunch();
	}
}