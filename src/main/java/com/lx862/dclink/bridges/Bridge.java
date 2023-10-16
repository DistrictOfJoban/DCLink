package com.lx862.dclink.bridges;

import com.lx862.dclink.data.bridge.User;
import com.lx862.dclink.data.BridgeEntry;
import com.lx862.dclink.data.Placeholder;

import java.util.Collection;
import java.util.List;

public interface Bridge {
    void sendMessage(String template, Placeholder placeholder, List<String> channelList, boolean allowMention, boolean enableEmoji);
    void login();
    void disconnect();
    Collection<BridgeEntry> getEntries();
    boolean isValid();
    boolean isReady();
    BridgeType getType();
    // TEMP
    User getUserInfo();
    void executeWhenReady(Runnable callback);
    void startStatus();
    void stopStatus();
}
