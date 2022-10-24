package com.lx.dclink;

import com.google.gson.JsonArray;
import com.lx.dclink.Config.BotConfig;
import com.lx.dclink.Config.MinecraftConfig;
import com.lx.dclink.Data.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.minecraft.text.MutableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordBot extends ListenerAdapter {
    public static final Logger LOGGER = (Logger) LogManager.getLogger("DCLinkClient");
    public static JDA client;
    public static Map<String, List<RichCustomEmoji>> emojiMap = new HashMap<>();
    public static Map<Long, Message> messageCache = new HashMap<>();
    private static Timer timer;
    private static int currentStatus;
    private static final Pattern embedPattern = Pattern.compile("(<<<.+>>>)");

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
                    .build()
                    .awaitReady();

            client.getGuildCache().forEach(guild -> {
                emojiMap.put(guild.getId(), guild.getEmojis());
            });

            LOGGER.info("[DCLink] Logged in as: " + client.getSelfUser().getAsTag());

            stopStatus();
            startCyclingStatus();
        } catch (InvalidTokenException | IllegalArgumentException ex) {
            LOGGER.error(ex.getStackTrace());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void disconnect() {
        client.shutdown();
    }

    public static void startCyclingStatus() {
        if(!BotConfig.statuses.isEmpty()) {
            Placeholder placeholder = new MinecraftPlaceholder(null, DCLink.server, null, null);

            timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    if(DCLink.server == null) return;
                    String status = BotConfig.statuses.get(currentStatus++ % BotConfig.statuses.size());
                    String formattedStatus = placeholder.parse(status);
                    client.getPresence().setActivity(Activity.playing(formattedStatus));
                }
            }, 0, BotConfig.getStatusRefreshInterval() * 1000L);
        }
    }

    public static void stopStatus() {
        if(timer != null) {
            timer.cancel();
            timer.purge();
        }
        client.getPresence().setActivity(null);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (event.isFromType(ChannelType.PRIVATE) || event.getMember() == null || !BotConfig.getInboundEnabled()) return;

        messageCache.put(event.getMessageIdLong(), event.getMessage());

        /* Don't send if coming from self */
        if(event.getMember().getId().equals(client.getSelfUser().getId())) return;
        String messageContent = event.getMessage().getContentDisplay();
        Message repliedMessage = event.getMessage().getReferencedMessage();
        Member repliedMessageAuthor = repliedMessage == null ? null : repliedMessage.getMember();
        List<Message.Attachment> attachments = event.getMessage().getAttachments();

        for(MCEntry entry : MinecraftConfig.entries) {
            if(!entry.channelID.contains(event.getGuildChannel().getId())) {
                continue;
            }

            List<MutableText> textToBeSent = new ArrayList<>();
            if(!messageContent.isEmpty()) {
                MutableText relayMessageText = entry.message.getDiscord2MCMessage(event.getMessage(), event.getGuildChannel().asTextChannel(), event.getMember(), repliedMessage, repliedMessageAuthor);
                textToBeSent.add(relayMessageText);
            }

            textToBeSent.addAll(entry.message.getAttachmentText(attachments, event.getGuildChannel().asTextChannel(), event.getMember()));
            DCLink.sendInGameMessage(textToBeSent, entry);
        }
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event)
    {
        if (event.isFromType(ChannelType.PRIVATE) || !BotConfig.getInboundEnabled()) return;

        Message message = messageCache.get(event.getMessageIdLong());
        if(message == null) return;

        Member member = message.getMember();
        if(member == null) return;
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
                MutableText formattedMessage = entry.message.getDiscordDeletedMessage(message, event.getGuildChannel().asTextChannel(), member);
                textToBeSent.add(formattedMessage);
            }

            textToBeSent.addAll(entry.message.getAttachmentText(attachments, event.getGuildChannel().asTextChannel(), member));
            DCLink.sendInGameMessage(textToBeSent, entry);
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event)
    {
        if (event.isFromType(ChannelType.PRIVATE) || !BotConfig.getInboundEnabled()) return;

        Message reactedMessage = messageCache.get(event.getMessageIdLong());
        if(reactedMessage == null) return;

        Member messageAuthor = reactedMessage.getMember();
        if(messageAuthor == null) return;

        String messageContent = reactedMessage.getContentDisplay();
        String emoji = event.getEmoji().getFormatted();

        for(MCEntry entry : MinecraftConfig.entries) {
            if(!entry.channelID.contains(event.getGuildChannel().getId())) continue;

            List<MutableText> textToBeSent = new ArrayList<>();
            if(!messageContent.isEmpty()) {
                MutableText formattedMessage = entry.message.getReactionRemoveMessage(emoji, event.getGuildChannel().asTextChannel(), event.getMember(), messageAuthor, reactedMessage);
                textToBeSent.add(formattedMessage);
            }

            DCLink.sendInGameMessage(textToBeSent, entry);
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event)
    {
        if (event.isFromType(ChannelType.PRIVATE) || !BotConfig.getInboundEnabled()) return;

        Message reactedMessage = messageCache.get(event.getMessageIdLong());
        if(reactedMessage == null) return;

        Member messageAuthor = reactedMessage.getMember();
        if(messageAuthor == null) return;

        String messageContent = reactedMessage.getContentDisplay();
        String emoji = event.getEmoji().getFormatted();

        for(MCEntry entry : MinecraftConfig.entries) {
            if(!entry.channelID.contains(event.getGuildChannel().getId())) continue;

            List<MutableText> textToBeSent = new ArrayList<>();
            if(!messageContent.isEmpty()) {
                MutableText formattedMessage = entry.message.getReactionAddMessage(emoji, event.getGuildChannel().asTextChannel(), event.getMember(), messageAuthor, reactedMessage);
                textToBeSent.add(formattedMessage);
            }

            DCLink.sendInGameMessage(textToBeSent, entry);
        }
    }

    public static void sendUniversalMessage(String template, Placeholder placeholder, List<String> channelList, boolean allowMention, boolean enableEmoji) {
        if(!BotConfig.getOutboundEnabled() || template == null) return;

        ArrayList<MessageEmbed> embedToBeSent = new ArrayList<>();
        Matcher matcher = embedPattern.matcher(template);

        if(matcher.find()) {
            template = template.replace(matcher.group(0), "");
            String embedName = matcher.group(0).replace("<<<", "").replace(">>>", "");
            JsonArray embedJson = BotConfig.getEmbedJson(embedName);
            if(embedJson != null) {
                embedToBeSent.addAll(EmbedGenerator.fromJson(placeholder, embedJson));
            }
        }

        String finalMessage = placeholder == null ? template : placeholder.parse(template);

        for(String channelId : channelList) {
            TextChannel channel = client.getChannelById(TextChannel.class, channelId);
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
            if(allowMention && finalMessage.contains("@")) {
                if(client.getGatewayIntents().contains(GatewayIntent.GUILD_MEMBERS)) {
                    for(Member member : channel.getGuild().getMembers()) {
                        finalMessage = finalMessage.replace("@" + member.getUser().getName(), String.format("<@%s>", member.getUser().getId()));
                    }
                }
            }

            /* Parse emoji map */
            if(enableEmoji) {
                for (RichCustomEmoji emoji : emojiMap) {
                    finalMessage = finalMessage.replace(":" + emoji.getName() + ":", "<:" + emoji.getName() + ":" + emoji.getId() + ">");
                }
            }

            if(finalMessage.isEmpty() && !embedToBeSent.isEmpty()) {
                channel.sendMessageEmbeds(embedToBeSent).queue();
            } else {
                MessageCreateAction action = channel.sendMessage(finalMessage);
                action.setEmbeds(embedToBeSent);
                action.queue();
            }
        }
    }
}
