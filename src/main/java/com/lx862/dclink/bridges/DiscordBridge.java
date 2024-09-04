package com.lx862.dclink.bridges;

import com.lx862.dclink.config.BotConfig;
import com.lx862.dclink.config.DiscordConfig;
import com.lx862.dclink.config.MinecraftConfig;
import com.lx862.dclink.data.*;
import com.lx862.dclink.minecraft.MinecraftSource;
import com.lx862.vendorneutral.usermember.User;
import com.lx862.dclink.util.StringHelper;
import com.lx862.vendorneutral.texts.embed.TextEmbed;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.minecraft.text.MutableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.regex.Pattern;

public class DiscordBridge extends ListenerAdapter implements Bridge {
    private static final Pattern EMBED_PATTERN = Pattern.compile("(<<<.+>>>)");
    private static final Logger LOGGER = LogManager.getLogger("DCLinkDiscord");
    private final MinecraftSource source;
    private final DiscordConfig config;
    private final Collection<GatewayIntent> intents;
    private final Collection<Runnable> queuedAction;
    private boolean isReady = false;
    public JDA client;
    public Map<String, List<RichCustomEmoji>> emojiMap = new HashMap<>();
    public Map<Long, Message> messageCache = new HashMap<>();

    public DiscordBridge(DiscordConfig config, MinecraftSource source) {
        this.source = source;
        this.config = config;
        this.intents = config.getIntents();
        this.queuedAction = new ArrayList<>();
    }

    public void login() {
        isReady = false;

        try {
            ChunkingFilter chunkingFilter = BotConfig.getInstance().getCacheMember() ? ChunkingFilter.ALL : ChunkingFilter.NONE;
            MemberCachePolicy memberCachePolicy = BotConfig.getInstance().getCacheMember() ? MemberCachePolicy.ALL : MemberCachePolicy.NONE;
            client = JDABuilder.createDefault(config.getToken())
                    .disableCache(CacheFlag.VOICE_STATE, CacheFlag.FORUM_TAGS)
                    .addEventListeners(this)
                    .setAutoReconnect(true)
                    .setMemberCachePolicy(memberCachePolicy)
                    .enableIntents(intents)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .setChunkingFilter(chunkingFilter)
                    .build();

            client.awaitReady();

        } catch (InvalidTokenException | IllegalArgumentException ex) {
            LOGGER.error("[DCLink] An invalid token has been provided! Please ensure the token is valid.");
        } catch (InterruptedException e) {
            LOGGER.error("[DCLink] Thread interrupted.");
        }
    }

    public void disconnect() {
        if(isReady()) {
            isReady = false;
            client.shutdown();
        }
    }

    public void updateStatus(String status) {
        client.getPresence().setActivity(Activity.playing(status));
    }

    public void stopStatus() {
        if(isReady()) {
            client.getPresence().setActivity(null);
        }
    }

    public Collection<BridgeContext> getContext() {
        return config.entries;
    }

    public boolean isValid() {
        return !StringHelper.notValidString(config.getToken());
    }

    @Override
    public void onReady(ReadyEvent event) {
        client.getGuildCache().forEach(guild -> {
            emojiMap.put(guild.getId(), guild.getEmojis());
        });

        LOGGER.info("[DCLink] Logged in as: " + client.getSelfUser().getAsTag());
        isReady = true;

        for(Runnable callback : queuedAction) {
            callback.run();
        }
        queuedAction.clear();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (!isReady() || event.isFromType(ChannelType.PRIVATE) || event.getMember() == null || !BotConfig.getInstance().inboundEnabled) return;

        messageCache.put(event.getMessageIdLong(), event.getMessage());

        /* Ignore if coming from self */
        if(event.getMember().getId().equals(client.getSelfUser().getId())) return;
        String messageContent = event.getMessage().getContentDisplay();
        Message repliedMessage = event.getMessage().getReferencedMessage();
        Member repliedMessageAuthor = repliedMessage == null ? null : repliedMessage.getMember();
        List<Message.Attachment> attachments = event.getMessage().getAttachments();

        for(MinecraftEntry entry : MinecraftConfig.entries) {
            if(!entry.channelID.isEmpty() && !entry.channelID.contains(event.getGuildChannel().getId())) {
                continue;
            }

            List<MutableText> textToBeSent = new ArrayList<>();
            if(!messageContent.isEmpty()) {
                MutableText relayMessageText = entry.message.getDiscordRelayMessage(event.getMessage(), event.getGuildChannel().asTextChannel(), event.getMember(), repliedMessage, repliedMessageAuthor);
                textToBeSent.add(relayMessageText);
            }

            textToBeSent.addAll(entry.message.getAttachmentText(attachments, event.getGuildChannel().asTextChannel(), event.getMember()));
            source.sendMessage(textToBeSent, entry);
        }
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event)
    {
        if (!isReady() || event.isFromType(ChannelType.PRIVATE) || !BotConfig.getInstance().inboundEnabled) return;

        Message newMessage = event.getMessage();
        Message oldMessage = messageCache.get(event.getMessageIdLong());

        if(oldMessage == null) return;

        Member member = newMessage.getMember();
        if(member == null) return;
        /* Don't send if coming from self */
        if(member.getId().equals(client.getSelfUser().getId())) return;

        for(MinecraftEntry entry : MinecraftConfig.entries) {
            if(!entry.channelID.contains(event.getGuildChannel().getId())) {
                continue;
            }

            List<MutableText> textToBeSent = new ArrayList<>();
            MutableText formattedMessage = entry.message.getDiscordEditedMessage(oldMessage, newMessage, event.getGuildChannel().asTextChannel(), member);
            textToBeSent.add(formattedMessage);
            source.sendMessage(textToBeSent, entry);
        }
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event)
    {
        if (!isReady || event.isFromType(ChannelType.PRIVATE) || !BotConfig.getInstance().inboundEnabled) return;

        Message message = messageCache.get(event.getMessageIdLong());
        if(message == null) return;

        Member member = message.getMember();
        if(member == null) return;
        /* Don't send if coming from self */
        if(member.getId().equals(client.getSelfUser().getId())) return;
        String messageContent = message.getContentDisplay();
        List<Message.Attachment> attachments = message.getAttachments();

        for(MinecraftEntry entry : MinecraftConfig.entries) {
            if(!entry.channelID.contains(event.getGuildChannel().getId())) {
                continue;
            }

            List<MutableText> textToBeSent = new ArrayList<>();
            if(!messageContent.isEmpty()) {
                MutableText formattedMessage = entry.message.getDiscordDeletedMessage(message, event.getGuildChannel().asTextChannel(), member);
                textToBeSent.add(formattedMessage);
            }

            textToBeSent.addAll(entry.message.getAttachmentText(attachments, event.getGuildChannel().asTextChannel(), member));
            source.sendMessage(textToBeSent, entry);
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event)
    {
        if (!isReady() || event.isFromType(ChannelType.PRIVATE) || !BotConfig.getInstance().inboundEnabled) return;

        Message reactedMessage = messageCache.get(event.getMessageIdLong());
        if(reactedMessage == null) return;

        Member messageAuthor = reactedMessage.getMember();
        if(messageAuthor == null) return;

        String messageContent = reactedMessage.getContentDisplay();
        String emoji = event.getEmoji().getFormatted();

        for(MinecraftEntry entry : MinecraftConfig.entries) {
            if(!entry.channelID.contains(event.getGuildChannel().getId())) continue;

            List<MutableText> textToBeSent = new ArrayList<>();
            if(!messageContent.isEmpty()) {
                MutableText formattedMessage = entry.message.getReactionRemoveMessage(emoji, event.getGuildChannel().asTextChannel(), event.getMember(), messageAuthor, reactedMessage);
                textToBeSent.add(formattedMessage);
            }

            source.sendMessage(textToBeSent, entry);
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event)
    {
        if (!isReady() || event.isFromType(ChannelType.PRIVATE) || !BotConfig.getInstance().inboundEnabled) return;

        Message reactedMessage = messageCache.get(event.getMessageIdLong());
        if(reactedMessage == null) return;

        Member messageAuthor = reactedMessage.getMember();
        if(messageAuthor == null) return;

        String messageContent = reactedMessage.getContentDisplay();
        String emoji = event.getEmoji().getFormatted();

        for(MinecraftEntry entry : MinecraftConfig.entries) {
            if(!entry.channelID.contains(event.getGuildChannel().getId())) continue;

            List<MutableText> textToBeSent = new ArrayList<>();
            if(!messageContent.isEmpty()) {
                MutableText formattedMessage = entry.message.getReactionAddMessage(emoji, event.getGuildChannel().asTextChannel(), event.getMember(), messageAuthor, reactedMessage);
                textToBeSent.add(formattedMessage);
            }

            source.sendMessage(textToBeSent, entry);
        }
    }

    public boolean isReady() {
        return isReady && client != null;
    }

    public BridgeType getType() {
        return BridgeType.DISCORD;
    }

    public User getUserInfo() {
        return User.fromDiscord(client.getSelfUser());
    }

    public void handleMessage(String finalMessage, String channelId, List<TextEmbed> embeds, boolean enableEmoji, boolean allowMention) {
        List<MessageEmbed> ourEmbeds = embeds.stream().map(TextEmbed::toDiscord).toList();

        TextChannel channel = client.getChannelById(TextChannel.class, channelId);
        if(channel == null) {
            LOGGER.warn("[DCLink] [Discord] Cannot find channel with id " + channelId);
            return;
        }

        if(!channel.canTalk()) {
            LOGGER.warn("[DCLink] [Discord] No permission to send message in Text Channel #" + channel.getName());
            return;
        }

        if(enableEmoji) {
            for (RichCustomEmoji emoji : channel.getGuild().getEmojis()) {
                finalMessage = finalMessage.replace(":" + emoji.getName() + ":", "<:" + emoji.getName() + ":" + emoji.getId() + ">");
            }
        }

        if(allowMention && finalMessage.contains("@")) {
            if(client.getGatewayIntents().contains(GatewayIntent.GUILD_MEMBERS)) {
                for(Member member : channel.getGuild().getMembers()) {
                    finalMessage = finalMessage.replace("@" + member.getUser().getName(), String.format("<@%s>", member.getUser().getId()));
                }
            }
        }

        try {
            MessageCreateAction action;

            if(finalMessage.isEmpty() && !embeds.isEmpty()) {
                // Send Embed Only
                action = channel.sendMessageEmbeds(ourEmbeds);
            } else {
                // Send message with embed attached
                action = channel.sendMessage(finalMessage);
                action.setEmbeds(ourEmbeds);
            }
            action.queue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
