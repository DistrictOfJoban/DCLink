package com.lx.RevoltAPI.data.accounts;

import com.google.gson.JsonObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Member {
    private final String nickname;
    private final List<String> roles;
    private final User user;
    private Date joinedAt;

    public Member(String id, String username, String tag, String nickname, String joinedAt, List<String> roles)  {
        this.user = new User(id, username);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        this.nickname = nickname;
        this.roles = roles;
        try {
            this.joinedAt = df.parse(joinedAt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Member(JsonObject jsonObject) {
        this.user = new User(jsonObject);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        this.nickname = jsonObject.get("nickname").getAsString();
        try {
            this.joinedAt = df.parse(jsonObject.get("joined_at").getAsString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.roles = new ArrayList<>();
        jsonObject.get("roles").getAsJsonArray().forEach(e -> {
            this.roles.add(e.getAsString());
        });
    }

    public String getNickname() {
        return nickname;
    }

    public Date getJoinedAt() {
        return joinedAt;
    }

    public User getUser() {
        return user;
    }
}
