package com.lx.dclink.Data;

import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.minecraft.text.*;

import java.util.ArrayList;
import java.util.List;

import static com.lx.dclink.Data.MinecraftFormatter.format;

public class MinecraftMessages {
    public String attachments = "Attachments";
    public String relay = "{memberTag}: {message}";
    public String relayDeleted = "~~{memberTag}: {message}~~";
    public String replyText = null;

    public MutableText getDiscord2MCMessage(String content, GuildMessageChannel channel, Member guildMember, String repliedMessage, Member repliedMember) {
        MutableText repliedText;

        try {
            repliedText = repliedMessage == null && repliedMember == null ? null : Text.Serializer.fromJson(format(replyText, repliedMessage, null, repliedMember, null));
        } catch (Exception e) {
            repliedText = new LiteralText("");
        }

        String formatted = format(relay, content, channel, guildMember, null);

        try {
            MutableText parsedText = Text.Serializer.fromJson(formatted);

            if(repliedText != null) {
                return repliedText.append(parsedText);
            } else {
                return parsedText;
            }
        } catch (Exception e) {
            if(repliedText != null) {
                return repliedText.append(new LiteralText(formatted));
            } else {
                return new LiteralText(formatted);
            }
        }
    }

    public MutableText getDiscordDeletedMessage(String content, GuildMessageChannel channel, Member guildMember) {
        String formatted = format(relayDeleted, content, channel, guildMember, null);

        try {
            return Text.Serializer.fromJson(formatted);
        } catch (Exception e) {
            return new LiteralText(formatted);
        }
    }

    public List<MutableText> getAttachmentText(List<Message.Attachment> attachmentList, GuildMessageChannel channel, Member guildMember) {
        List<MutableText> textList = new ArrayList<>();
        for(Message.Attachment attachment : attachmentList) {
            String formatted = format(attachments, null, channel, guildMember, attachment);

            try {
                textList.add(Text.Serializer.fromJson(formatted));
            } catch (Exception e) {
                textList.add(new LiteralText(formatted));
            }
        }
        return textList;
    }
}
