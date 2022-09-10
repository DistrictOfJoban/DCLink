package com.lx.dclink.Data;

import com.lx.dclink.Mappings;
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
    public String relayReplied = "<To: {repliedAuthorTag} {repliedMessage}> {memberTag}: {message}";
    public String relayDeleted = "~~{memberTag}: {message}~~";

    public MutableText getDiscord2MCMessage(String content, GuildMessageChannel channel, Member guildMember, String repliedMessage, Member repliedAuthor)
    {
        String formatted = format(repliedMessage != null ? relayReplied : relay, content, channel, guildMember, null);
        if(repliedMessage != null) {
            formatted = formatted
                    .replace("{repliedAuthorName}", repliedAuthor.getEffectiveName())
                    .replace("{repliedAuthorTag}", repliedAuthor.getUser().getAsTag())
                    .replace("{repliedMessage}", repliedMessage);
        }

        try {
            return Text.Serializer.fromJson(formatted);
        } catch (Exception e) {
            return Mappings.literalText(formatted);
        }
    }

    public MutableText getDiscordDeletedMessage(String content, GuildMessageChannel channel, Member guildMember) {
        String formatted = format(relayDeleted, content, channel, guildMember, null);

        try {
            return Text.Serializer.fromJson(formatted);
        } catch (Exception e) {
            return Mappings.literalText(formatted);
        }
    }

    public List<MutableText> getAttachmentText(List<Message.Attachment> attachmentList, GuildMessageChannel channel, Member guildMember) {
        List<MutableText> textList = new ArrayList<>();
        for(Message.Attachment attachment : attachmentList) {
            String formatted = format(attachments, null, channel, guildMember, attachment);

            try {
                textList.add(Text.Serializer.fromJson(formatted));
            } catch (Exception e) {
                textList.add(Mappings.literalText(formatted));
            }
        }
        return textList;
    }
}
