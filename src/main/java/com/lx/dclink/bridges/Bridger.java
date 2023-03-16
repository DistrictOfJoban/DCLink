package com.lx.dclink.bridges;

import com.lx.dclink.data.Placeholder;

import java.util.List;

public interface Bridger {
    void sendMessage(String template, Placeholder placeholder, List<String> channelList, boolean allowMention, boolean enableEmoji);
    void login();
    void disconnect();
    boolean isReady();
}
