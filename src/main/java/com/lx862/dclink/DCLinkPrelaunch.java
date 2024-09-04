package com.lx862.dclink;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class DCLinkPrelaunch implements PreLaunchEntrypoint {

	@Override
	public void onPreLaunch() {
		DCLink.getMcSource().sourceStarted();
	}
}