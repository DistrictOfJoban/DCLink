package com.lx.dclink.Data;

import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.text.DecimalFormat;

public class MinecraftFormatter {
    public static String format(String baseContent, String message, GuildMessageChannel channel, Member member, Message.Attachment attachment) {
        String modifiedContent = baseContent;

        if(channel != null) {
            modifiedContent = modifiedContent
                .replace("{channelName}", channel.getName())
                .replace("{channelId}", channel.getId())
                .replace("{guildName}", channel.getGuild().getName());
            }

        if(member != null) {
            modifiedContent = modifiedContent
                .replace("{memberNickOrUsername}", member.getEffectiveName())
                .replace("{memberUsername}", member.getUser().getName())
                .replace("{memberTag}", member.getUser().getAsTag())
                .replace("{memberId}", member.getId())
                .replace("{avatarURL}", member.getAvatarUrl() == null ? "" : member.getAvatarUrl());
        }

        if(attachment != null) {
            modifiedContent = modifiedContent
                    .replace("{attachmentFileName}", attachment.getFileName())
                    .replace("{attachmentFileExtension}", attachment.getFileExtension() == null ? "" : attachment.getFileExtension())
                    .replace("{attachmentURL}", attachment.getUrl())
                    .replace("{attachmentFileSize}", readableFileSize(attachment.getSize()));
        }

        if(message != null) {
            modifiedContent = modifiedContent.replace("{message}", message);
        }

        return modifiedContent;
    }

    public static String readableFileSize(long size) {
        if(size <= 0) return "0B";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups))  + units[digitGroups];
    }
}
