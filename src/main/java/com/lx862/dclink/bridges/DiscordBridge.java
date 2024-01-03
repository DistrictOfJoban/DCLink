package com.lx862.dclink.bridges;

import com.google.gson.JsonArray;
import com.lx862.dclink.config.BotConfig;
import com.lx862.dclink.config.DiscordConfig;
import com.lx862.dclink.config.MinecraftConfig;
import com.lx862.dclink.data.*;
import com.lx862.dclink.data.bridge.User;
import com.lx862.dclink.minecraft.events.ServerManager;
import com.lx862.dclink.util.EmbedParser;
import com.lx862.dclink.util.StringHelper;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordBridge extends ListenerAdapter implements Bridge {
    private static final Pattern EMBED_PATTERN = Pattern.compile("(<<<.+>>>)");
    private static final Logger LOGGER = LogManager.getLogger("DCLinkDiscord");
    private final DiscordConfig config;
    private final Collection<GatewayIntent> intents;
    private Timer timer;
    private final Collection<Runnable> queuedAction;
    private boolean isReady = false;
    private int currentStatus;
    public JDA client;
    public Map<String, List<RichCustomEmoji>> emojiMap = new HashMap<>();
    public Map<Long, Message> messageCache = new HashMap<>();

    public DiscordBridge(DiscordConfig config) {
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
        if(client != null) {
            isReady = false;
            client.shutdown();
        }
    }

    public void executeWhenReady(Runnable callback) {
        if(!isReady) {
            queuedAction.add(callback);
        } else {
            callback.run();
        }
    }

    public void startStatus() {
        if(!BotConfig.getInstance().statuses.isEmpty() && client != null) {
            executeWhenReady(() -> {
                stopStatus();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(!ServerManager.serverAlive()) return;

                        Placeholder placeholder = new MinecraftPlaceholder(null, ServerManager.getServer(), null, null);
                        String status = BotConfig.getInstance().statuses.get(currentStatus++ % BotConfig.getInstance().statuses.size());
                        String formattedStatus = placeholder.parse(status);
                        client.getPresence().setActivity(Activity.playing(formattedStatus));
                    }
                }, 0, BotConfig.getInstance().getStatusRefreshInterval() * 1000L);
            });
        }
    }

    public void stopStatus() {
        if(timer != null) {
            timer.cancel();
            timer.purge();
        }

        if(isReady && client != null) {
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
        if (!isReady || event.isFromType(ChannelType.PRIVATE) || event.getMember() == null || !BotConfig.getInstance().inboundEnabled) return;

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
            ServerManager.sendMessage(textToBeSent, entry);
        }
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event)
    {
        if (!isReady || event.isFromType(ChannelType.PRIVATE) || !BotConfig.getInstance().inboundEnabled) return;

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
            ServerManager.sendMessage(textToBeSent, entry);
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
            ServerManager.sendMessage(textToBeSent, entry);
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event)
    {
        if (!isReady || event.isFromType(ChannelType.PRIVATE) || !BotConfig.getInstance().inboundEnabled) return;

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

            ServerManager.sendMessage(textToBeSent, entry);
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event)
    {
        if (!isReady || event.isFromType(ChannelType.PRIVATE) || !BotConfig.getInstance().inboundEnabled) return;

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

            ServerManager.sendMessage(textToBeSent, entry);
        }
    }

    public boolean isReady() {
        return isReady;
    }

    public BridgeType getType() {
        return BridgeType.DISCORD;
    }

    public User getUserInfo() {
        return User.fromDiscord(client.getSelfUser());
    }

    public void sendMessage(String template, Placeholder placeholder, List<String> channelList, boolean allowMention, boolean enableEmoji) {
        if(!isReady || !BotConfig.getInstance().outboundEnabled || StringHelper.notValidString(template) || client == null) return;

        ArrayList<MessageEmbed> embedToBeSent = new ArrayList<>();
        Matcher matcher = EMBED_PATTERN.matcher(template);

        if(matcher.find()) {
            template = template.replace(matcher.group(0), "");
            String embedName = matcher.group(0).replace("<<<", "").replace(">>>", "");
            JsonArray embedJson = config.getEmbedJson(embedName);
            if(embedJson != null) {
                embedToBeSent.addAll(EmbedParser.fromJson(placeholder, embedJson));
            }
        }

        String finalMessage = placeholder == null ? template : placeholder.parse(template);

        for(String channelId : channelList) {
            try {
                Long.parseLong(channelId);
            } catch (Exception ignored) {
                continue;
            }
            TextChannel channel = client.getChannelById(TextChannel.class, channelId);
            if(channel == null) continue;

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

            try {
                // Send Embed Only
                if(finalMessage.isEmpty() && !embedToBeSent.isEmpty()) {
                    channel.sendMessageEmbeds(embedToBeSent).queue();
                } else {
                    // Send message with embed attached
                    MessageCreateAction action = channel.sendMessage(finalMessage);
                    action.setEmbeds(embedToBeSent);
                    action.queue();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
