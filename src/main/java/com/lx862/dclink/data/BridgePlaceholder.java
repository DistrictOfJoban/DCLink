package com.lx862.dclink.data;

import com.lx862.vendorneutral.usermember.Member;
import com.lx862.vendorneutral.usermember.User;
import com.lx862.dclink.util.JsonHelper;
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
        addPlaceholder(objName, "name", JsonHelper.sanitize(attachment.getFileName()));
        addPlaceholder(objName, "ext", attachment.getFileExtension() == null ? "" : attachment.getFileExtension());
        addPlaceholder(objName, "url", JsonHelper.sanitize(attachment.getUrl()));
        addPlaceholder(objName, "size", StringHelper.formatFileSize(attachment.getSize()));
    }

    public void setData(String objName, Member member) {
        addPlaceholder(objName, "nickOrUsername", JsonHelper.sanitize(member.getEffectiveName()));
        setData(objName + "." + "user", member.getUser());
    }

    public void setData(String objName, User user) {
        addPlaceholder(objName, "username", JsonHelper.sanitize(user.getName()));
        addPlaceholder(objName, "displayName", JsonHelper.sanitize(user.getEffectiveName()));
        addPlaceholder(objName, "tagOrUsername", JsonHelper.sanitize(getUserTagOrName(user, false)));
        addPlaceholder(objName, "tagOrDisplayName", JsonHelper.sanitize(getUserTagOrName(user, true)));
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
           addPlaceholder(objName, "topic", JsonHelper.sanitize(((TextChannel)channel).getTopic()));
        }
    }

    public void setData(String objName, Guild guild) {
        addPlaceholder(objName, "memberCount", String.valueOf(guild.getMemberCount()));
        addPlaceholder(objName, "iconURL", guild.getIconUrl());
        addPlaceholder(objName, "name", JsonHelper.sanitize(guild.getName()));

        if(guild.getOwner() != null) {
            setData(objName + ".owner", Member.fromDiscord(guild.getOwner()));
        }

        if(guild.getBannerUrl() != null) {
            addPlaceholder(objName, "bannerURL", guild.getBannerUrl());
        }
    }

    public void setData(String objName, Message message) {
        addPlaceholder(objName, "content", JsonHelper.sanitize(message.getContentDisplay()));
        addPlaceholder(objName, "jumpURL", message.getJumpUrl());
        if(message.getMember() != null) {
            setData(objName + "." + "author", Member.fromDiscord(message.getMember()));
        } else {
            setData(objName + "." + "author" + "." + "user", User.fromDiscord(message.getAuthor()));
        }
    }
}
