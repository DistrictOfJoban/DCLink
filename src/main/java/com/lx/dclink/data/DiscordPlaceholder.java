package com.lx.dclink.data;

import com.lx.dclink.util.StringHelper;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildChannel;

public class DiscordPlaceholder extends Placeholder {

    public DiscordPlaceholder(Message message, StandardGuildChannel channel, Member member, Message.Attachment attachment) {
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
        addPlaceholder(objName, "name", attachment.getFileName());
        addPlaceholder(objName, "ext", attachment.getFileExtension() == null ? "" : attachment.getFileExtension());
        addPlaceholder(objName, "url", attachment.getUrl());
        addPlaceholder(objName, "size", StringHelper.formatFileSize(attachment.getSize()));
    }

    public void setData(String objName, Member member) {
        addPlaceholder(objName, "nickOrUsername", member.getEffectiveName());
        setData(objName + "." + "user", member.getUser());
    }

    public void setData(String objName, User user) {
        addPlaceholder(objName, "username", user.getName());
        addPlaceholder(objName, "tag", user.getAsTag());
        addPlaceholder(objName, "id", user.getId());
        addPlaceholder(objName, "avatarURL", user.getAvatarUrl() == null ? "" : user.getAvatarUrl());
    }

    public void setData(String objName, StandardGuildChannel channel) {
        addPlaceholder(objName, "name", channel.getName());
        addPlaceholder(objName, "id", channel.getId());

        if(channel instanceof TextChannel) {
           addPlaceholder(objName, "mention", channel.getAsMention());
           addPlaceholder(objName, "topic", ((TextChannel)channel).getTopic());
        }
    }

    public void setData(String objName, Guild guild) {
        addPlaceholder(objName, "memberCount", String.valueOf(guild.getMemberCount()));
        addPlaceholder(objName, "iconURL", guild.getIconUrl());
        addPlaceholder(objName, "name", guild.getName());

        if(guild.getOwner() != null) {
            setData(objName + ".owner", guild.getOwner());
        }

        if(guild.getBannerUrl() != null) {
            addPlaceholder(objName, "bannerURL", guild.getBannerUrl());
        }
    }

    public void setData(String objName, Message message) {
        addPlaceholder(objName, "content", message.getContentDisplay());
        addPlaceholder(objName, "jumpURL", message.getJumpUrl());
        if(message.getMember() != null) {
            setData(objName + "." + "author", message.getMember());
        } else {
            setData(objName + "." + "author" + "." + "user", message.getAuthor());
        }
    }
}
