package com.lx862.dclink.data;

import com.lx862.dclink.util.StringHelper;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildChannel;

public class BridgePlaceholder extends Placeholder {

    public BridgePlaceholder(Message message, StandardGuildChannel channel, Member member, Message.Attachment attachment) {
        if(channel != null) {
            setData("channel", channel);
            setData("guild", channel.getGuild());
        }

        if(member != null) {
            setData("member", member);
        }

        if(attachment != null) {
            setData("attachment", attachment);
        }

        if(message != null) {
            setData("message", message);
        }
    }

    public void setData(String objName, Message.Attachment attachment) {
        addPlaceholder(objName, "name", sanitize(attachment.getFileName()));
        addPlaceholder(objName, "ext", attachment.getFileExtension() == null ? "" : attachment.getFileExtension());
        addPlaceholder(objName, "url", sanitize(attachment.getUrl()));
        addPlaceholder(objName, "size", StringHelper.formatFileSize(attachment.getSize()));
    }

    public void setData(String objName, Member member) {
        addPlaceholder(objName, "nickOrUsername", sanitize(member.getEffectiveName()));
        setData(objName + "." + "user", member.getUser());
    }

    public void setData(String objName, User user) {
        addPlaceholder(objName, "username", sanitize(user.getName()));
        addPlaceholder(objName, "displayName", sanitize(user.getEffectiveName()));
        addPlaceholder(objName, "tagOrUsername", sanitize(getUserTagOrName(user, false)));
        addPlaceholder(objName, "tagOrDisplayName", sanitize(getUserTagOrName(user, true)));
        addPlaceholder(objName, "id", user.getId());
        addPlaceholder(objName, "avatarURL", user.getAvatarUrl() == null ? "" : user.getAvatarUrl());
    }

    private static String getUserTagOrName(User user, boolean displayName) {
        if(user.getDiscriminator().equals("0000")) {
            return displayName ? user.getEffectiveName() : "@" + user.getName();
        } else {
            return user.getAsTag();
        }
    }

    public void setData(String objName, StandardGuildChannel channel) {
        addPlaceholder(objName, "name", channel.getName());
        addPlaceholder(objName, "id", channel.getId());

        if(channel instanceof TextChannel) {
           addPlaceholder(objName, "mention", channel.getAsMention());
           addPlaceholder(objName, "topic", sanitize(((TextChannel)channel).getTopic()));
        }
    }

    public void setData(String objName, Guild guild) {
        addPlaceholder(objName, "memberCount", String.valueOf(guild.getMemberCount()));
        addPlaceholder(objName, "iconURL", guild.getIconUrl());
        addPlaceholder(objName, "name", sanitize(guild.getName()));

        if(guild.getOwner() != null) {
            setData(objName + ".owner", guild.getOwner());
        }

        if(guild.getBannerUrl() != null) {
            addPlaceholder(objName, "bannerURL", guild.getBannerUrl());
        }
    }

    public void setData(String objName, Message message) {
        addPlaceholder(objName, "content", sanitize(message.getContentDisplay()));
        addPlaceholder(objName, "jumpURL", message.getJumpUrl());
        if(message.getMember() != null) {
            setData(objName + "." + "author", message.getMember());
        } else {
            setData(objName + "." + "author" + "." + "user", message.getAuthor());
        }
    }

    private static String sanitize(String str) {
        if(str == null) return null;
        return str.replace("\"", "\\\"").replace("{", "\\{");
    }
}
