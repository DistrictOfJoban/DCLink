package com.lx.RevoltAPI;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lx.RevoltAPI.data.*;
import com.lx.dclink.data.bridge.Member;
import com.lx.dclink.data.bridge.User;
import com.lx.RevoltAPI.data.StatusPresence;
import com.lx.RevoltAPI.events.EventEmitter;
import com.lx.RevoltAPI.managers.CacheManager;
import com.lx.RevoltAPI.managers.FetchManager;
import com.lx.dclink.util.StringHelper;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class RevoltClient {
    private final String token;
    private API api;
    private static final Logger LOGGER = LogManager.getLogger("RevoltAPI");
    private EventEmitter eventEmitter;

    public RevoltClient(String token) {
        this.api = new API("https://api.revolt.chat");
        this.api.setToken(token);
        this.eventEmitter = new EventEmitter(token);
        this.token = token;
    }

    public void addListener(RevoltListener listener) {
        eventEmitter.addListener(listener);
    }

    public User getSelf() {
        if(CacheManager.userInfo != null) {
            return CacheManager.userInfo;
        } else {
            return FetchManager.getSelf(api);
        }
    }

    public void setStatus(StatusPresence presence, String text) {
        JsonObject jsonObject = new JsonObject();
        JsonObject statusObject = new JsonObject();
        if(presence != null) {
            statusObject.addProperty("presence", presence.toString());
        }
        if(text != null) {
            statusObject.addProperty("text", text);
        }

        if(presence == null && text == null) {
            JsonArray removeObject = new JsonArray();
            removeObject.add("StatusText");
            jsonObject.add("remove", removeObject);
        } else {
            jsonObject.add("status", statusObject);
        }

        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        APIResponse response = api.executePatch("/users/@me", requestBody);
    }

    public void login() {
        if(StringHelper.notValidString(token)) {
            throw new InvalidTokenException("No token provided/Token is empty!");
        }

        User userInfo = getSelf();
        if(userInfo == null) {
            LOGGER.error("[RevoltAPI] Cannot get self user info, please ensure the token is valid.");
        } else {
            setStatus(StatusPresence.ONLINE, null);
            eventEmitter.emitReadyEvent(userInfo);
        }

        eventEmitter.startListeningWebSocket();
    }

    public void disconnect() {
        setStatus(StatusPresence.INVISIBLE, null);
    }

    public void sendMessage(String message, String channelId, @Nullable Collection<TextEmbed> textEmbeds) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("content", message);
        if(textEmbeds != null && !textEmbeds.isEmpty()) {
            JsonArray jsonArray = new JsonArray();
            for(TextEmbed embed : textEmbeds) {
                jsonArray.add(embed.toJson());
            }
            jsonObject.add("embeds", jsonArray);
        }
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        CompletableFuture.runAsync(() -> {
            api.executePost("/channels/" + channelId + "/messages", requestBody);
        });
    }

    public Channel getChannel(String id) {
        if(CacheManager.cachedChannel.containsKey(id)) {
            return CacheManager.cachedChannel.get(id);
        } else {
            return FetchManager.getChannel(api, id);
        }
    }

    public Member getMember(String serverId, String id) {
        if(CacheManager.cachedMembers.containsKey(id)) {
            return CacheManager.cachedMembers.get(id);
        } else {
            return FetchManager.getMember(api, serverId, id);
        }
    }

    public User getUser(String id) {
        if(CacheManager.cachedUsers.containsKey(id)) {
            return CacheManager.cachedUsers.get(id);
        } else {
            return FetchManager.getUser(api, id);
        }
    }
}
