package com.lx.dclink.Data;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lx.dclink.Data.DiscordFormatter.format;

public class DCEntry {
    public List<ContentType> contentType = new ArrayList<>();
    public List<String> channelID = new ArrayList<>();
    public List<String> allowedDimension = new ArrayList<>();
    public Map<String, String> emojiMap = new HashMap<>();
    public String thumbnailURL = "https://minotar.net/avatar/{player}/16";
    public boolean allowMention = false;
    public boolean enableEmoji = false;
    public DiscordMessages message = new DiscordMessages();

    public String getThumbnailURL(ServerPlayerEntity player, MinecraftServer server, World world) {
        if(thumbnailURL == null) return null;
        return format(thumbnailURL, player, server, world);
    }
}
