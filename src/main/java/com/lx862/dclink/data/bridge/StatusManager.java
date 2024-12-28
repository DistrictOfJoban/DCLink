package com.lx862.dclink.data.bridge;

import com.lx862.dclink.DCLink;
import com.lx862.dclink.bridges.BridgeManager;
import com.lx862.dclink.config.BotConfig;
import com.lx862.dclink.data.MinecraftPlaceholder;
import com.lx862.dclink.data.Placeholder;

import java.util.Timer;
import java.util.TimerTask;

public class StatusManager {
    private Timer statusTimer;
    private int currentStatus;

    public void start() {
        statusTimer = new Timer();
        statusTimer.schedule(new TimerTask() {
            @Override
            public void run() {
            if(!DCLink.getMaster().alive()) {
                stop();
                return;
            }

            nextStatus();
            String status = BotConfig.getInstance().statuses.get(currentStatus);
            Placeholder placeholder = new MinecraftPlaceholder(null, DCLink.getMaster().getServer(), null, null);
            String formattedStatus = placeholder.parse(status);

            BridgeManager.forEach(bridge -> {
                bridge.updateStatus(formattedStatus);
            });
            }
        }, 0, BotConfig.getInstance().getStatusRefreshInterval() * 1000L);
    }

    private void nextStatus() {
        currentStatus = (currentStatus + 1) % BotConfig.getInstance().statuses.size();
    }

    public void stop() {
        statusTimer.cancel();
        statusTimer.purge();
    }
}