package com.lx.RevoltAPI.data;

import com.google.gson.JsonObject;
import com.lx.RevoltAPI.RevoltClient;
import com.lx.RevoltAPI.data.accounts.Member;
import com.lx.RevoltAPI.data.accounts.User;
import com.lx.RevoltAPI.data.attachment.File;

import java.util.ArrayList;
import java.util.List;

/**
 * This represents a chat message sent on Revolt.
 */
public class Message {
    private final List<File> attachments;
    private final String authorUserId;
    private final String channelId;
    private final String content;
    private final String nonce;
    private final String id;

    public Message(String content, String channelId, String authorUserId, String id, String nonce, List<File> attachment) {
        this.content = content;
        this.channelId = channelId;
        this.authorUserId = authorUserId;
        this.id = id;
        this.nonce = nonce;
        this.attachments = attachment;
    }

    public Message(JsonObject jsonObject) {
        this.content = jsonObject.get("content").getAsString();
        this.channelId = jsonObject.get("channel").getAsString();
        this.authorUserId = jsonObject.get("author").getAsString();
        this.nonce = jsonObject.get("nonce").getAsString();
        this.id = jsonObject.get("_id").getAsString();
        this.attachments = new ArrayList<>();

        if(jsonObject.has("attachments")) {
            jsonObject.get("attachments").getAsJsonArray().forEach(e -> {
                attachments.add(new File(e.getAsJsonObject()));
            });
        }
    }

    public Channel getChannel(RevoltClient client) {
        return client.getChannel(channelId);
    }

    public User getAuthor(RevoltClient client) {
        return client.getUser(authorUserId);
    }

    public Member getAuthorAsMember(RevoltClient client) {
        return client.getMember(client.getChannel(channelId).getId(), authorUserId);
    }

    public List<File> getAttachments() {
        return attachments;
    }

    public String getContent() {
        return content;
    }

    public String getId() {
        return id;
    }

    public String getNonce() {
        return nonce;
    }
}
