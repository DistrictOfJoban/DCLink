package com.lx862.dclink.bridges;

import com.lx862.dclink.config.DiscordConfig;
import com.lx862.dclink.config.RevoltConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BridgeManager {
    private static final List<Bridge> bridges = new ArrayList<>();

    public static void addDefaultBridges() {
        Bridge discordBridge = new DiscordBridge(DiscordConfig.getInstance());
        Bridge revoltBridge = new RevoltBridge(RevoltConfig.getInstance());
        if(discordBridge.isValid()) BridgeManager.addBridge(discordBridge);
        if(revoltBridge.isValid()) BridgeManager.addBridge(revoltBridge);
    }

    public static void clearBridges() {
        bridges.clear();
    }

    public static void addBridge(Bridge bridge) {
        bridges.add(bridge);
    }

    public static void forEach(Consumer<Bridge> callback) {
        for(Bridge bridge : bridges) {
            callback.accept(bridge);
        }
    }

    public static void login() {
        for(Bridge bridge : bridges) {
            bridge.login();
        }
    }

    public static void logout() {
        for(Bridge bridge : bridges) {
            bridge.stopStatus();
            bridge.disconnect();
        }
    }
}
