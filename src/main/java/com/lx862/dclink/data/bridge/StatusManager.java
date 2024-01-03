package com.lx862.dclink.data.bridge;

import com.lx862.dclink.config.BotConfig;

import java.util.Timer;

public class StatusManager {
    private static int currentStatus;
    public static Timer statusTimer = new Timer();

    public static void nextStatus() {
        currentStatus++;
        if(currentStatus >= BotConfig.getInstance().statuses.size()) {
            currentStatus = 0;
        }
    }

    public static int getCurrentStatusIndex() {
        return currentStatus;
    }

    public static void stopTimer() {
        statusTimer.cancel();
        statusTimer.purge();
    }
}
