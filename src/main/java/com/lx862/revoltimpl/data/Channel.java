package com.lx862.revoltimpl.data;

import com.lx862.revoltimpl.API;
import com.lx862.revoltimpl.RevoltClient;
import com.lx862.revoltimpl.data.text.embed.TextEmbed;

import java.util.Collection;

public class Channel {
    private String channelType;
    private String id;
    private String name;
    private String serverId;
    private String description;
    private API api;

    public Channel(API api, String id, String serverId, String name) {
        this.api = api;
        this.id = id;
        this.serverId = serverId;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // TODO: This is temp, create a server class
    public String getServerId() {
        return serverId;
    }

    public void sendMessage(RevoltClient client, String message) {
        client.sendMessage(message, id, null);
    }

    public void sendMessage(RevoltClient client, String message, Collection<TextEmbed> textEmbeds) {
        client.sendMessage(message, id, textEmbeds);
    }
}
