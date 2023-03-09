package com.lx.dclink.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lx.dclink.util.JsonHelper;

public class DiscordMessages {
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

    public DiscordMessages() {
        /* Initialize default message */
        serverStarting = "<<<serverStarting>>>";
        serverStarted = "<<<serverStarted>>>";
        serverStopping = "<<<serverStopping>>>";
        serverStopped = "<<<serverStopped>>>";
        serverCrashed = ":warning: **Crash Exception Detected!\n```{reason}```**";
        relay = "**<{player.team.prefix}{player.name}>** {message}";
        relayCommand = "**{player.team.prefix}{player.name}**: {message}";
        playerJoin = "<<<playerJoined>>>";
        playerLeft = "<<<playerLeft>>>";
        playerDisconnectReason = "({reason})";
        changeDimension = "**{player.team.prefix}{player.name}** has warped to {world.name}";
        playerDeath = ":skull: **{player.team.prefix}{player.name}** {reason}";
        playerAdvancement = ":medal: **{player.team.prefix}{player.name}** has achieved **{advancement}**! (*{advancementDetails}*)";
    }

    public static DiscordMessages fromJson(JsonElement jsonElement) {
        Gson g = new Gson();
        return g.fromJson(jsonElement, DiscordMessages.class);
    }

    public static JsonElement toJson(DiscordMessages discordMessages) {
        Gson g = new Gson();
        return g.toJsonTree(discordMessages, DiscordMessages.class);
    }

    public String getPlayerDisconnectReason(String reason) {
        if(reason.equals("Disconnected")) return "";
        return playerDisconnectReason.replace("reason", reason);
    }
}