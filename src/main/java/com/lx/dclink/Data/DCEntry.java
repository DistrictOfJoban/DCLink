package com.lx.dclink.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCEntry {
    public List<ContentType> contentType = new ArrayList<>();
    public List<String> channelID = new ArrayList<>();
    public List<String> allowedDimension = new ArrayList<>();
    public Map<String, String> emojiMap = new HashMap<>();
    public boolean allowMention = false;
    public boolean enableEmoji = false;
    public DiscordMessages message = new DiscordMessages();
}
