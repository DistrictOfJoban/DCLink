package com.lx.dclink.bridges;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BridgeManager {
    private static final List<Bridge> bridges = new ArrayList<>();

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
