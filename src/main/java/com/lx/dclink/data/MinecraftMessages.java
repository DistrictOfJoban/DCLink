package com.lx.dclink.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.lx.dclink.Mappings;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildChannel;
import net.minecraft.text.*;

import java.util.ArrayList;
import java.util.List;

public class MinecraftMessages {
    public String attachments;
    public String relay;
    public String relayEdited;
    public String relayReplied;
    public String relayDeleted;
    public String reactionAdded;
    public String reactionRemoved;

    public MinecraftMessages() {
        /* Initialize default message */
        attachments = "{member.user.tag} sent a attachment. ({attachment.size})";
        relay = "{member.user.tag}: {message.content}";
        relayEdited = "{member.user.tag}: {oldMessage.content}\n{newMessage.content}";
        relayReplied = "<To: {repliedAuthor.user.tag} {repliedMessage.content}> {member.tag}: {message.content}";
        relayDeleted = "~~{member.user.tag}: {message.content}~~";
        reactionAdded = "{member.user.tag} reacted {emoji} to {message.content}";
        reactionRemoved = "{member.user.tag} removed reaction {emoji} to {message.content}";
    }

    public MutableText getDiscordRelayMessage(Message message, StandardGuildChannel channel, Member guildMember, Message repliedMessage, Member repliedAuthor)
    {
        DiscordPlaceholder placeholder = new DiscordPlaceholder(message, channel, guildMember, null);

        if(repliedMessage != null) {
            placeholder.setData("repliedMessage", repliedMessage);
        }

        String formatted = placeholder.parse(repliedMessage != null ? relayReplied : relay);
        return toText(formatted);
    }

    public MutableText getDiscordEditedMessage(Message oldMessage, Message newMessage, StandardGuildChannel channel, Member guildMember) {
        DiscordPlaceholder placeholder = new DiscordPlaceholder(null, channel, guildMember, null);
        placeholder.setData("oldMessage", oldMessage);
        placeholder.setData("newMessage", newMessage);
        String formatted = placeholder.parse(relayEdited);
        return toText(formatted);
    }

    public MutableText getDiscordDeletedMessage(Message message, StandardGuildChannel channel, Member guildMember) {
        DiscordPlaceholder placeholder = new DiscordPlaceholder(message, channel, guildMember, null);
        String formatted = placeholder.parse(relayDeleted);
        return toText(formatted);
    }

    public List<MutableText> getAttachmentText(List<Message.Attachment> attachmentList, StandardGuildChannel channel, Member guildMember) {
        List<MutableText> textList = new ArrayList<>();
        for(Message.Attachment attachment : attachmentList) {
            DiscordPlaceholder placeholder = new DiscordPlaceholder(null, channel, guildMember, attachment);
            String formatted = placeholder.parse(attachments);

            textList.add(toText(formatted));
        }
        return textList;
    }

    public MutableText getReactionAddMessage(String emoji, StandardGuildChannel channel, Member reactMember, Member messageMember, Message reactedMessage) {
        DiscordPlaceholder placeholder = new DiscordPlaceholder(reactedMessage, channel, reactMember, null);
        placeholder.addPlaceholder("emojiID", emoji);
        placeholder.addPlaceholder("emoji", emoji);
        placeholder.setData("author", messageMember);
        String formatted = placeholder.parse(reactionAdded);

        return toText(formatted);
    }

    public MutableText getReactionRemoveMessage(String emoji, StandardGuildChannel channel, Member reactMember, Member messageMember, Message reactedMessage) {
        DiscordPlaceholder placeholder = new DiscordPlaceholder(reactedMessage, channel, reactMember, null);
        placeholder.addPlaceholder("emojiID", emoji);
        placeholder.addPlaceholder("emoji", emoji);
        placeholder.setData("author", messageMember);
        String formatted = placeholder.parse(reactionRemoved);

        return toText(formatted);
    }

    private MutableText toText(String str) {
        try {
            return Text.Serializer.fromJson(str);
        } catch (Exception e) {
            return Mappings.literalText(str);
        }
    }

    public static MinecraftMessages fromJson(JsonElement jsonElement) {
        Gson g = new Gson();
        return g.fromJson(jsonElement, MinecraftMessages.class);
    }

    public static JsonElement toJson(MinecraftMessages minecraftMessages) {
        Gson g = new Gson();
        return g.toJsonTree(minecraftMessages, MinecraftMessages.class);
    }
}
