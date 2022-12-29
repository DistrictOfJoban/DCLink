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
        serverStarting = ":clock10: Loading world...";
        serverStarted = ":white_check_mark: Server started (**{time}** elapsed)";
        serverStopping = ":warning: Server is stopping (It was up for **{time}**)";
        serverStopped = ":x: Server no longer linked with Discord";
        serverCrashed = ":warning: **Crash Exception Detected!\n```{reason}```**";
        relay = "**<{player.team}{player.name}>** {message}";
        relayCommand = "**{player.team}{player.name}**: {message}";
        playerJoin = "**{player.team}{player.name}** has joined the game.";
        playerLeft = "**{player.team}{player.name}** left the game.";
        playerDisconnectReason = "({reason})";
        changeDimension = "**{player.team}{player.name}** has warped to {world.name}";
        playerDeath = ":skull: **{player.team}{player.name}** {reason}";
        playerAdvancement = ":medal: **{player.team}{player.name}** has achieved **{advancement}**! (*{advancementDetails}*)";
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