package com.lx862.dclink.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class BridgeMessages {
    public String serverStarting;
    public String serverStarted;
    public String serverStopping;
    public String serverStopped;
    public String serverCrashed;
    public String relay;
    public String relayCommand;
    public String playerJoin;
    public String playerLeft;
    public String playerDisconnectReason;
    public String changeDimension;
    public String playerDeath;
    public String playerAdvancement;

    public BridgeMessages() {
        /* Initialize default message */
        serverStarting = "<<<serverStarting>>>";
        serverStarted = "<<<serverStarted>>>";
        serverStopping = "<<<serverStopping>>>";
        serverStopped = "<<<serverStopped>>>";
        serverCrashed = "<<<serverCrashed>>>";
        relay = "**<{player.displayName}{player.name}>** {message}";
        relayCommand = "**{player.displayName}{player.name}**: {message}";
        playerJoin = "<<<playerJoined>>>";
        playerLeft = "<<<playerLeft>>>";
        playerDisconnectReason = "({reason})";
        changeDimension = "**{player.displayName}{player.name}** has warped to {world.name}";
        playerDeath = ":skull: **{player.displayName}{player.name}** {cause}";
        playerAdvancement = ":medal: **{player.displayName}{player.name}** has achieved **{advancement}**! (*{advancementDetails}*)";
    }

    public static BridgeMessages fromJson(JsonElement jsonElement) {
        Gson g = new Gson();
        return g.fromJson(jsonElement, BridgeMessages.class);
    }

    public static JsonElement toJson(BridgeMessages bridgeMessages) {
        Gson g = new Gson();
        return g.toJsonTree(bridgeMessages, BridgeMessages.class);
    }

    public String getPlayerDisconnectReason(String reason) {
        if(reason == null || reason.equals("Disconnected")) {
            return "";
        }
        return playerDisconnectReason.replace("{reason}", reason);
    }
}