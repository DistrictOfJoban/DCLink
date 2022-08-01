package com.lx.dclink;

import com.lx.dclink.Config.MinecraftConfig;
import com.lx.dclink.Config.BotConfig;
import com.lx.dclink.Data.DCEntry;
import com.lx.dclink.Data.MCEntry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.minecraft.text.MutableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import javax.security.auth.login.LoginException;
import java.util.*;

public class DiscordBot extends ListenerAdapter {
    public static final Logger LOGGER = (Logger) LogManager.getLogger("DCLinkClient");
    public static JDA client;
    public static Map<String, List<RichCustomEmoji>> emojiMap = new HashMap<>();
    public static Map<Long, Message> messageCache = new HashMap<>();

    public static void load(String token, Collection<GatewayIntent> intents) {
        if(token == null) {
            LOGGER.warn("[DCLink] Cannot log in to Discord: No token provided!");
            return;
        }

        try {
            ChunkingFilter chunkingFilter = BotConfig.getCacheMember() ? ChunkingFilter.ALL : ChunkingFilter.NONE;
            MemberCachePolicy memberCachePolicy = BotConfig.getCacheMember() ? MemberCachePolicy.ALL : MemberCachePolicy.NONE;
            client = JDABuilder.createDefault(token)
                    .addEventListeners(new DiscordBot())
                    .setAutoReconnect(true)
                    .setMemberCachePolicy(memberCachePolicy)
                    .enableIntents(intents)
                    .setChunkingFilter(chunkingFilter)
                    .build();

            client.getGuildCache().forEach(g -> {
                System.out.println(g.getId());
                emojiMap.put(g.getId(), g.getEmojis());
            });

            LOGGER.info("[DCLink] Logged in as: " + client.getSelfUser().getAsTag());
        } catch (LoginException | IllegalArgumentException ex) {
            LOGGER.error(ex.getStackTrace());
        }
    }

    public static void disconnect() {
        client.shutdown();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (event.isFromType(ChannelType.PRIVATE) || event.getMember() == null) {
            return;
        }

        messageCache.put(event.getMessageIdLong(), event.getMessage());

        /* Don't send if coming from self */
        if(event.getMember().getId().equals(client.getSelfUser().getId())) return;
        String messageContent = event.getMessage().getContentDisplay();
        List<Message.Attachment> attachments = event.getMessage().getAttachments();

        for(MCEntry entry : MinecraftConfig.entries) {
            if(!entry.channelID.contains(event.getGuildChannel().getId())) {
                continue;
            }

            List<MutableText> textToBeSent = new ArrayList<>();
            if(!messageContent.isEmpty()) {
                MutableText formattedMessage = entry.message.getDiscordMessage(event.getMessage().getContentDisplay(), event.getGuildChannel(), event.getMember());
                textToBeSent.add(formattedMessage);
            }

            textToBeSent.addAll(entry.message.getAttachmentText(attachments, event.getGuildChannel(), event.getMember()));
            DCLink.sendInGameMessage(textToBeSent, entry);
        }
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event)
    {
        if (event.isFromType(ChannelType.PRIVATE)) {
            return;
        }

        Message message = messageCache.get(event.getMessageIdLong());
        if(message == null) {
            return;
        }

        Member member = message.getMember();
        if(member == null) {
            return;
        }

        /* Don't send if coming from self */
        if(member.getId().equals(client.getSelfUser().getId())) return;
        String messageContent = message.getContentDisplay();
        List<Message.Attachment> attachments = message.getAttachments();

        for(MCEntry entry : MinecraftConfig.entries) {
            if(!entry.channelID.contains(event.getGuildChannel().getId())) {
                continue;
            }

            List<MutableText> textToBeSent = new ArrayList<>();
            if(!messageContent.isEmpty()) {
                MutableText formattedMessage = entry.message.getDiscordDeletedMessage(message.getContentDisplay(), event.getGuildChannel(), member);
                textToBeSent.add(formattedMessage);
            }

            textToBeSent.addAll(entry.message.getAttachmentText(attachments, event.getGuildChannel(), member));
            DCLink.sendInGameMessage(textToBeSent, entry);
        }
    }

    public static void sendMessage(String message, DCEntry entry) {
        if(message == null) return;

        for(String channelId : entry.channelID) {
            String finalMessage = message;
            GuildMessageChannel channel = client.getChannelById(GuildMessageChannel.class, channelId);
            if(channel == null) {
                LOGGER.warn("Cannot find text channel: " + channelId);
                continue;
            }

            if(!channel.canTalk()) {
                LOGGER.warn("No permission to send message in Text Channel #" + channel.getName());
                continue;
            }

            List<RichCustomEmoji> emojiMap = channel.getGuild().getEmojis();

            /* Parse mention */
            if(entry.allowMention && message.contains("@")) {
                if(client.getGatewayIntents().contains(GatewayIntent.GUILD_MEMBERS)) {
                    for(Member member : channel.getGuild().getMembers()) {
                        finalMessage = finalMessage.replace("@" + member.getUser().getName(), String.format("<@%s>", member.getUser().getId()));
                    }
                }
            }

            /* Parse emoji map */
            if(entry.enableEmoji) {
                for (RichCustomEmoji emoji : emojiMap) {
                    finalMessage = finalMessage.replace(":" + emoji.getName() + ":", "<:" + emoji.getName() + ":" + emoji.getId() + ">");
                }
            }

            channel.sendMessage(finalMessage).queue();
        }
    }

    public static void sendSimpleEmbed(String description, List<String> channelList) {
        sendSimpleEmbed(description, channelList,null);
    }

    public static void sendSimpleEmbed(String description, List<String> channelList, String thumbnail) {
        if(description == null) return;

        for(String channelId : channelList) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setDescription(description);
            if(thumbnail != null) {
                embedBuilder.setThumbnail(thumbnail);
            }
            MessageEmbed embed = embedBuilder.build();
            GuildMessageChannel channel = client.getChannelById(GuildMessageChannel.class, channelId);
            if(channel == null) {
                LOGGER.warn("Cannot find text channel: " + channelId);
                continue;
            }

            if(!channel.canTalk()) {
                LOGGER.warn("No permission to send message in Text Channel #" + channel.getName());
                continue;
            }

            channel.sendMessageEmbeds(embed).queue();
        }
    }
}
