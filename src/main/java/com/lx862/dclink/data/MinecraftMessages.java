package com.lx862.dclink.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildChannel;
import net.minecraft.registry.RegistryWrapper;
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

    public MutableText getDiscordRelayMessage(Message message, StandardGuildChannel channel, Member guildMember, Message repliedMessage, Member repliedAuthor, RegistryWrapper.WrapperLookup registries) {
        BridgePlaceholder placeholder = new BridgePlaceholder(message, channel, guildMember, null);

        if (repliedMessage != null) {
            placeholder.setData("repliedMessage", repliedMessage);
        }

        String formatted = placeholder.parse(repliedMessage != null ? relayReplied : relay);
        return toText(formatted, registries);
    }

    public MutableText getDiscordEditedMessage(Message oldMessage, Message newMessage, StandardGuildChannel channel, Member guildMember, RegistryWrapper.WrapperLookup registries) {
        BridgePlaceholder placeholder = new BridgePlaceholder(null, channel, guildMember, null);
        placeholder.setData("oldMessage", oldMessage);
        placeholder.setData("newMessage", newMessage);
        String formatted = placeholder.parse(relayEdited);
        return toText(formatted, registries);
    }

    public MutableText getDiscordDeletedMessage(Message message, StandardGuildChannel channel, Member guildMember, RegistryWrapper.WrapperLookup registries) {
        BridgePlaceholder placeholder = new BridgePlaceholder(message, channel, guildMember, null);
        String formatted = placeholder.parse(relayDeleted);
        return toText(formatted, registries);
    }

    public List<MutableText> getAttachmentText(List<Message.Attachment> attachmentList, StandardGuildChannel channel, Member guildMember, RegistryWrapper.WrapperLookup registries) {
        List<MutableText> textList = new ArrayList<>();
        for(Message.Attachment attachment : attachmentList) {
            BridgePlaceholder placeholder = new BridgePlaceholder(null, channel, guildMember, attachment);
            String formatted = placeholder.parse(attachments);

            textList.add(toText(formatted, registries));
        }
        return textList;
    }

    public MutableText getReactionAddMessage(String emoji, StandardGuildChannel channel, Member reactMember, Member messageMember, Message reactedMessage, RegistryWrapper.WrapperLookup registries) {
        BridgePlaceholder placeholder = new BridgePlaceholder(reactedMessage, channel, reactMember, null);
        placeholder.addPlaceholder("emojiID", emoji);
        placeholder.addPlaceholder("emoji", emoji);
        placeholder.setData("author", messageMember);
        String formatted = placeholder.parse(reactionAdded);

        return toText(formatted, registries);
    }

    public MutableText getReactionRemoveMessage(String emoji, StandardGuildChannel channel, Member reactMember, Member messageMember, Message reactedMessage, RegistryWrapper.WrapperLookup registries) {
        BridgePlaceholder placeholder = new BridgePlaceholder(reactedMessage, channel, reactMember, null);
        placeholder.addPlaceholder("emojiID", emoji);
        placeholder.addPlaceholder("emoji", emoji);
        placeholder.setData("author", messageMember);
        String formatted = placeholder.parse(reactionRemoved);

        return toText(formatted, registries);
    }

    private MutableText toText(String str, RegistryWrapper.WrapperLookup registries) {
        try {
            return Text.Serialization.fromJson(str, registries);
        } catch (Exception e) {
            return Text.literal(str);
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
