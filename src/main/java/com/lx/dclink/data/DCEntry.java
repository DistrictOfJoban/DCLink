package com.lx.dclink.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCEntry {
    public List<String> channelID = new ArrayList<>();
    public List<String> allowedDimension = new ArrayList<>();
    public Map<String, String> emojiMap = new HashMap<>();
    public DiscordMessages message = new DiscordMessages();
    public boolean allowMention = false;
    public boolean enableEmoji = false;
}
