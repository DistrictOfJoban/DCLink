package com.lx.dclink.data.bridge;

import com.google.gson.JsonObject;
import com.lx.RevoltAPI.data.attachment.File;

public class User {
    private final String username;
    private final String displayName;
    private final String id;
    private final String discriminator;
    private final String avatarUrl;

    public User(String id, String username, String displayName, String discriminator, String avatarUrl) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.discriminator = discriminator;
        this.avatarUrl = avatarUrl;
    }

    public User(String id, String username, String displayName, String avatarUrl) {
        this(id, username, displayName, "0000", avatarUrl);
    }

    public User(String id, String username, String avatarUrl) {
        this(id, username, null, "0000", avatarUrl);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return username;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public String getAsTag() {
        return username + "#" + discriminator;
    }

    public String getEffectiveName() {
        if(displayName == null) {
            return username;
        } else {
            return displayName;
        }
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public static User fromDiscord(net.dv8tion.jda.api.entities.User user) {
        return new User(user.getId(), user.getName(), user.getEffectiveName(), user.getDiscriminator());
    }

    public static User fromRevolt(JsonObject jsonObject) {
        return new User(jsonObject.get("_id").getAsString(), jsonObject.get("username").getAsString(), null, "0000", new File(jsonObject.get("avatar").getAsJsonObject()).getDownloadUrl());
    }
}
