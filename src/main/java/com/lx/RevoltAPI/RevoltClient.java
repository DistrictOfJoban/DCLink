package com.lx.RevoltAPI;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lx.RevoltAPI.data.APIResponse;
import com.lx.RevoltAPI.data.Channel;
import com.lx.RevoltAPI.data.UserInfo;
import com.lx.RevoltAPI.data.type.StatusPresence;
import com.lx.dclink.util.StringHelper;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class RevoltClient {
    private final String token;
    private API api;
    private static final Logger LOGGER = LogManager.getLogger("RevoltAPI");
    private List<RevoltListener> listeners = new ArrayList<>();

    public RevoltClient(String token) {
        this.api = new API();
        this.api.setToken(token);
        this.token = token;
    }

    public void addListener(RevoltListener listener) {
        listeners.add(listener);
    }

    public UserInfo getSelf() {
        if(CacheManager.userInfo != null) return CacheManager.userInfo;

        APIResponse data = api.executeGet("/users/@me");
        if(data == null || !data.isSuccess()) return null;

        JsonElement dataElement = new JsonParser().parse(data.getData());
        JsonObject dataObject = dataElement.getAsJsonObject();
        UserInfo info = new UserInfo(dataObject.get("_id").getAsString(), dataObject.get("username").getAsString());
        CacheManager.userInfo = info;
        return info;
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

        UserInfo userInfo = getSelf();
        if(userInfo == null) {
            LOGGER.error("[RevoltAPI] Cannot get self user info, please ensure the token is valid.");
        } else {
            setStatus(StatusPresence.ONLINE, null);
            for(RevoltListener listener : listeners) {
                listener.onReady(userInfo);
            }
        }
    }

    public Channel getChannel(String id) {
        if(CacheManager.cachedChannel.containsKey(id)) {
            return CacheManager.cachedChannel.get(id);
        } else {
            APIResponse data = api.executeGet("/channels/" + id);
            if(data == null || !data.isSuccess()) return null;
            JsonElement dataElement = new JsonParser().parse(data.getData());
            JsonObject dataObject = dataElement.getAsJsonObject();
            Channel channel = new Channel(api, id, dataObject.get("name").getAsString());
            CacheManager.cachedChannel.put(id, channel);
            return channel;
        }
    }

    public void disconnect() {
        setStatus(StatusPresence.INVISIBLE, null);
    }
}
