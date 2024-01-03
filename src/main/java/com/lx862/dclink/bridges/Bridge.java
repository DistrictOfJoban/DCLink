package com.lx862.dclink.bridges;

import com.lx862.vendorneutral.usermember.User;
import com.lx862.dclink.data.BridgeContext;
import com.lx862.vendorneutral.texts.embed.TextEmbed;

import java.util.Collection;
import java.util.List;

public interface Bridge {
    void handleMessage(String finalMessage, String channelId, List<TextEmbed> embeds, boolean allowEmoji, boolean allowMention);
    void login();
    void disconnect();
    Collection<BridgeContext> getContext();
    boolean isValid();
    boolean isReady();
    BridgeType getType();
    // TEMP
    User getUserInfo();
    void updateStatus(String status);
    void stopStatus();
}
