package com.lx862.revoltapi.managers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lx862.revoltapi.API;
import com.lx862.revoltapi.data.APIResponse;
import com.lx862.revoltapi.data.Channel;
import com.lx862.dclink.data.bridge.Member;
import com.lx862.dclink.data.bridge.User;

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
        return User.fromRevolt(dataObject);
    }

    public static User getUser(API api, String id) {
        APIResponse data = api.executeGet("/users/" + id);
        if(data == null || !data.isSuccess()) return null;
        JsonElement dataElement = new JsonParser().parse(data.getData());
        JsonObject dataObject = dataElement.getAsJsonObject();
        Member member = Member.fromRevolt(dataObject);
        CacheManager.cachedMembers.put(id, member);
        return member.getUser();
    }

    public static Member getMember(API api, String serverId, String id) {
        APIResponse data = api.executeGet("/servers/" + serverId + "/members/" + id);
        if(data == null || !data.isSuccess()) return null;
        JsonElement dataElement = new JsonParser().parse(data.getData());
        JsonObject dataObject = dataElement.getAsJsonObject();
        Member member = Member.fromRevolt(dataObject);
        CacheManager.cachedMembers.put(id, member);
        return member;
    }
}
