package com.lx862.vendorneutral.usermember;

import com.google.gson.JsonObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
* TODO: Rework
* */
public class Member {
    private final String nickname;
    private final List<String> roles;
    private final User user;
    private Date joinedAt;

    public Member(String id, String username, String displayName, String discriminator, String avatarUrl, String nickname, String joinedAt, List<String> roles)  {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        this.user = new User(id, username, displayName, discriminator, avatarUrl);
        this.nickname = nickname;
        this.roles = roles;
        try {
            this.joinedAt = df.parse(joinedAt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Member(User user, String nickname, String joinedAt, List<String> roles) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        this.user = user;
        this.nickname = nickname;
        if(joinedAt != null) {
            try {
                this.joinedAt = df.parse(joinedAt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.roles = roles;
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

    public String getEffectiveName() {
        if(nickname != null) {
            return nickname;
        }
        return user.getEffectiveName();
    }

    public static Member fromDiscord(net.dv8tion.jda.api.entities.Member member) {
        return new Member(User.fromDiscord(member.getUser()), member.getNickname(), null, null);
    }

    public static Member fromRevolt(JsonObject jsonObject) {
        User user = User.fromRevolt(jsonObject);
        List<String> roles = new ArrayList<>();
        jsonObject.get("roles").getAsJsonArray().forEach(e -> {
            roles.add(e.getAsString());
        });
        return new Member(user, jsonObject.get("nickname").getAsString(), jsonObject.get("joined_at").getAsString(), roles);
    }
}
