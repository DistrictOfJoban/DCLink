package com.lx.dclink.data;

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
        playerAdvancement = ":medal: **{player.team}{player.name}** has achieved **{advancement}**!\n*{advancementDetails}*";
    }

    public String getPlayerDisconnectReason(String reason) {
        if(reason.equals("Disconnected")) return "";
        return playerDisconnectReason.replace("reason", reason);
    }
}