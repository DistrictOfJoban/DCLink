package com.lx.dclink.Data;

import com.lx.dclink.Mappings;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildChannel;
import net.minecraft.text.*;

import java.util.ArrayList;
import java.util.List;

public class MinecraftMessages {
    public String attachments = "{member.user.tag} sent a attachment. ({attachment.size})";
    public String relay = "{member.user.tag}: {message.content}";
    public String relayReplied = "<To: {repliedAuthor.user.tag} {repliedMessage.content}> {member.tag}: {message.content}";
    public String relayDeleted = "~~{member.user.tag}: {message.content}~~";
    public String reactionAdded = "{member.user.tag} reacted {emoji} to {message.content}";
    public String reactionRemoved = "{member.user.tag} removed reaction {emoji} to {message.content}";

    public MutableText getDiscord2MCMessage(Message message, StandardGuildChannel channel, Member guildMember, Message repliedMessage, Member repliedAuthor)
    {
        DiscordPlaceholder placeholder = new DiscordPlaceholder(message, channel, guildMember, null);

        if(repliedMessage != null) {
            placeholder.setMessage("repliedMessage", repliedMessage);
        }

        String formatted = placeholder.parse(repliedMessage != null ? relayReplied : relay);
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
        placeholder.setMember("author", messageMember);
        String formatted = placeholder.parse(reactionAdded);

        return toText(formatted);
    }

    public MutableText getReactionRemoveMessage(String emoji, StandardGuildChannel channel, Member reactMember, Member messageMember, Message reactedMessage) {
        DiscordPlaceholder placeholder = new DiscordPlaceholder(reactedMessage, channel, reactMember, null);
        placeholder.addPlaceholder("emojiID", emoji);
        placeholder.addPlaceholder("emoji", emoji);
        placeholder.setMember("author", messageMember);
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
}
