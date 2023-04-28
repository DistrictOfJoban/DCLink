package com.lx.dclink.bridges;

import com.lx.RevoltAPI.data.UserInfo;
import com.lx.dclink.data.BridgeEntry;
import com.lx.dclink.data.Placeholder;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public interface Bridge {
    void sendMessage(String template, Placeholder placeholder, List<String> channelList, boolean allowMention, boolean enableEmoji);
    void login();
    void disconnect();
    Collection<BridgeEntry> getEntries();
    boolean isValid();
    boolean isReady();
    BridgeType getType();
    // TEMP
    UserInfo getUserInfo();
    void executeOnReady(Runnable callback);
    void startStatus();
    void stopStatus();
}
