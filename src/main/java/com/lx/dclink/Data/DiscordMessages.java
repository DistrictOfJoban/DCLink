package com.lx.dclink.Data;

public class DiscordMessages {
    public String serverStarting = ":clock10: Loading world...";
    public String serverStarted = ":white_check_mark: Server started (**{time}** elapsed)";
    public String serverStopping = ":warning: Server is stopping (It was up for **{time}**)";
    public String serverStopped = ":x: Server no longer linked with Discord";
    public String serverCrashed = ":warning: **Crash Exception Detected!\n```{reason}```**";
    public String relay = "**<{player.team}{player.name}>** {message}";
    public String relayCommand = "**{player.team}{player.name}**: {message}";
    public String playerJoin = "**{player.team}{player.name}** has joined the game.";
    public String playerLeft = "**{player.team}{player.name}** left the game.";
    public String playerDisconnectReason = "({reason})";
    public String changeDimension = "**{player.team}{player.name}** has warped to {world.name}";
    public String playerDeath = ":skull: **{player.team}{player.name}** {reason}";
    public String playerAdvancement = ":medal: **{player.team}{player.name}** has achieved **{advancement}**!\n*{advancementDetails}*";

    public String getPlayerDisconnectReason(String reason) {
        if(reason.equals("Disconnected")) return "";
        return playerDisconnectReason.replace("reason", reason);
    }
}