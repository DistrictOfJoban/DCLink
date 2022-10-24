package com.lx.dclink.Data;

import com.lx.dclink.Utils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildChannel;

public class DiscordPlaceholder extends Placeholder {

    public DiscordPlaceholder(Message message, StandardGuildChannel channel, Member member, Message.Attachment attachment) {
        if(channel != null) {
            setChannel("channel", channel);
            setGuild("guild", channel.getGuild());
        }

        if(member != null) {
            setMember("member", member);
        }

        if(attachment != null) {
            setAttachment("attachment", attachment);
        }

        if(message != null) {
            setMessage("message", message);
        }
    }

    public void setAttachment(String objName, Message.Attachment attachment) {
        addPlaceholder(objName + "." + "name", attachment.getFileName());
        addPlaceholder(objName + "." + "ext", attachment.getFileExtension() == null ? "" : attachment.getFileExtension());
        addPlaceholder(objName + "." + "url", attachment.getUrl());
        addPlaceholder(objName + "." + "size", Utils.formatFileSize(attachment.getSize()));
    }

    public void setMember(String objName, Member member) {
        addPlaceholder(objName + "." + "nickOrUsername", member.getEffectiveName());
        setUser(objName + "." + "user", member.getUser());
    }

    public void setUser(String objName, User user) {
        addPlaceholder(objName + "." + "username", user.getName());
        addPlaceholder(objName + "." + "tag", user.getAsTag());
        addPlaceholder(objName + "." + "id", user.getId());
        addPlaceholder(objName + "." + "avatarURL", user.getAvatarUrl() == null ? "" : user.getAvatarUrl());
    }

    public void setChannel(String objName, StandardGuildChannel channel) {
        addPlaceholder(objName + "." + "name", channel.getName());
        addPlaceholder(objName + "." + "id", channel.getId());

        if(channel instanceof TextChannel) {
           addPlaceholder(objName + "." + "mention", channel.getAsMention());
           addPlaceholder(objName + "." + "topic", ((TextChannel)channel).getTopic());
        }
    }

    public void setGuild(String objName, Guild guild) {
        addPlaceholder(objName + "." + "memberCount", guild.getMemberCount());
        addPlaceholder(objName + "." + "iconURL", guild.getIconUrl());
        addPlaceholder(objName + "." + "name", guild.getName());

        if(guild.getOwner() != null) {
            setMember(objName + "." + "owner", guild.getOwner());
        }

        if(guild.getBannerUrl() != null) {
            addPlaceholder(objName + "." + "bannerURL", guild.getBannerUrl());
        }
    }

    public void setMessage(String objName, Message message) {
        addPlaceholder(objName + "." + "content", message.getContentDisplay());
        addPlaceholder(objName + "." + "jumpURL", message.getJumpUrl());
        if(message.getMember() != null) {
            setMember(objName + "." + "author", message.getMember());
        } else {
            setUser(objName + "." + "author" + "." + "user", message.getAuthor());
        }
    }
}
