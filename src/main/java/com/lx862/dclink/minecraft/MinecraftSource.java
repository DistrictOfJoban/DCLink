package com.lx862.dclink.minecraft;

import com.lx862.dclink.data.Source;
import com.lx862.dclink.minecraft.commands.dclink;
import com.lx862.dclink.minecraft.events.ServerManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class MinecraftSource implements Source {
    public static boolean crashServerOnTick = false;
    @Override
    public void initialize() {
        ServerLifecycleEvents.SERVER_STARTING.register((ServerManager::serverStarting));
        ServerLifecycleEvents.SERVER_STARTED.register((ServerManager::serverStarted));
        ServerLifecycleEvents.SERVER_STOPPING.register((ServerManager::serverStopping));
        ServerLifecycleEvents.SERVER_STOPPED.register((ServerManager::serverStopped));
        ServerTickEvents.START_SERVER_TICK.register(e -> {
            if(crashServerOnTick) {
                throw new RuntimeException("Manually triggered DCLink Debug crash.");
            }
        });

        ServerManager.registerCommand((dispatcher) -> {
            dclink.register(dispatcher);
        });
    }
}
