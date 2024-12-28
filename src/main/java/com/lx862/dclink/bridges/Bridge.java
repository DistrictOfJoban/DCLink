package com.lx862.dclink.bridges;

import com.lx862.dclink.data.BridgeContext;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.util.Collection;
import java.util.List;

public interface Bridge {
    void handleMessage(String finalMessage, String channelId, List<MessageEmbed> embeds, boolean allowEmoji, boolean allowMention);
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
