package com.lx.dclink.Data;

public class DiscordMessages {
    public String serverStarting = ":clock10: Loading world...";
    public String serverStarted = ":white_check_mark: Server started (**{timer}** elapsed)";
    public String serverStopping = ":warning: Server is stopping (It was up for **{timer}**)";
    public String serverStopped = ":x: Server no longer linked with Discord";
    public String serverCrashed = ":warning: **Crash Exception Detected!\n```{reason}```**";
    public String relay = "**<{playerTeam}{playerName}>** {message}";
    public String command = "**{playerTeam}{playerName}**: {message}";
    public String playerJoin = "**{playerTeam}{playerName}** has joined the game.";
    public String playerLeft = "**{playerTeam}{playerName}** left the game.";
    public String playerDisconnectReason = "({reason})";
    public String changeDimension = "**{playerTeam}{playerName}** has warped to {worldName}";
    public String playerDeath = ":skull: **{playerTeam}{playerName}** {reason}";
    public String playerAdvancement = ":medal: **{playerTeam}{playerName}** has achieved **{advancement}**!\n*{advancementDetails}*";

    public String getPlayerDisconnectReason(String reason) {
        if(reason.equals("Disconnected")) return "";
        return playerDisconnectReason.replace("reason", reason);
    }
}
