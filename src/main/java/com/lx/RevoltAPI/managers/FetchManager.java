package com.lx.RevoltAPI.managers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lx.RevoltAPI.API;
import com.lx.RevoltAPI.data.APIResponse;
import com.lx.RevoltAPI.data.Channel;
import com.lx.RevoltAPI.data.accounts.Member;
import com.lx.RevoltAPI.data.accounts.User;

public class FetchManager {
    public static Channel getChannel(API api, String id) {
        APIResponse data = api.executeGet("/channels/" + id);
        if(data == null || !data.isSuccess()) return null;
        JsonElement dataElement = new JsonParser().parse(data.getData());
        JsonObject dataObject = dataElement.getAsJsonObject();
        Channel channel = new Channel(api, id, dataObject.get("server").getAsString(), dataObject.get("name").getAsString());
        CacheManager.cachedChannel.put(id, channel);
        return channel;
    }

    public static User getSelf(API api) {
        APIResponse data = api.executeGet("/users/@me");
        if(data == null || !data.isSuccess()) return null;

        JsonElement dataElement = new JsonParser().parse(data.getData());
        JsonObject dataObject = dataElement.getAsJsonObject();
        return new User(dataObject);
    }

    public static User getUser(API api, String id) {
        APIResponse data = api.executeGet("/users/" + id);
        if(data == null || !data.isSuccess()) return null;
        JsonElement dataElement = new JsonParser().parse(data.getData());
        JsonObject dataObject = dataElement.getAsJsonObject();
        Member member = new Member(dataObject);
        CacheManager.cachedMembers.put(id, member);
        return member.getUser();
    }

    public static Member getMember(API api, String serverId, String id) {
        APIResponse data = api.executeGet("/servers/" + serverId + "/members/" + id);
        if(data == null || !data.isSuccess()) return null;
        JsonElement dataElement = new JsonParser().parse(data.getData());
        JsonObject dataObject = dataElement.getAsJsonObject();
        Member member = new Member(dataObject);
        CacheManager.cachedMembers.put(id, member);
        return member;
    }
}
