package com.lx.dclink.bridges;

import com.google.gson.JsonArray;
import com.lx.RevoltAPI.RevoltListener;
import com.lx.RevoltAPI.data.TextEmbed;
import com.lx.dclink.DCLink;
import com.lx.RevoltAPI.RevoltClient;
import com.lx.RevoltAPI.data.Channel;
import com.lx.RevoltAPI.data.UserInfo;
import com.lx.RevoltAPI.data.type.StatusPresence;
import com.lx.dclink.config.BotConfig;
import com.lx.dclink.config.DiscordConfig;
import com.lx.dclink.config.RevoltConfig;
import com.lx.dclink.data.BridgeEntry;
import com.lx.dclink.data.MinecraftPlaceholder;
import com.lx.dclink.data.Placeholder;
import com.lx.dclink.util.EmbedParser;
import com.lx.dclink.util.StringHelper;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RevoltBridge implements Bridge, RevoltListener {
    private static final Pattern EMBED_PATTERN = Pattern.compile("(<<<.+>>>)");
    private static final Logger LOGGER = LogManager.getLogger("DCLinkRevolt");
    private final Collection<Runnable> queuedAction;
    public RevoltClient client;
    public Map<String, List<RichCustomEmoji>> emojiMap = new HashMap<>();
    public Map<Long, Message> messageCache = new HashMap<>();
    private boolean isReady = false;
    private Timer timer;
    private int currentStatus;
    private final String token;
    private final RevoltConfig config;

    public RevoltBridge(RevoltConfig config) {
        this.config = config;
        this.client = new RevoltClient(config.getToken());
        this.client.addListener(this);
        this.token = config.getToken();
        this.queuedAction = new ArrayList<>();
    }

    @Override
    public void sendMessage(String template, Placeholder placeholder, List<String> channelList, boolean allowMention, boolean enableEmoji) {
        if(!isReady || !BotConfig.getInstance().outboundEnabled || StringHelper.notValidString(template) || client == null) return;
        ArrayList<TextEmbed> embedToBeSent = new ArrayList<>();
        Matcher matcher = EMBED_PATTERN.matcher(template);

        if(matcher.find()) {
            template = template.replace(matcher.group(0), "");
            String embedName = matcher.group(0).replace("<<<", "").replace(">>>", "");
            JsonArray embedJson = DiscordConfig.getInstance().getEmbedJson(embedName);
            if(embedJson != null) {
                embedToBeSent.addAll(EmbedParser.fromJsonToRevolt(placeholder, embedJson));
            }
        }

        String finalMessage = placeholder == null ? template : placeholder.parse(template);

        for(String channelId : channelList) {
            Channel channel = client.getChannel(channelId);
            if(channel == null) continue;
            channel.sendMessage(finalMessage, embedToBeSent);
        }
    }

    public void login() {
        isReady = false;
        if(token != null && StringHelper.notValidString(token)) {
            LOGGER.warn("[RevoltLink] Cannot log in to Revolt: No token provided/Token is empty!");
            return;
        }

        client.login();
    }

    public void disconnect() {
        if(client != null) {
            client.disconnect();
            isReady = false;
        }
    }

    @Override
    public boolean isReady() {
        return isReady;
    }

    public BridgeType getType() {
        return BridgeType.REVOLT;
    }

    public UserInfo getUserInfo() {
        return client.getSelf();
    }

    public void startStatus() {
        if(!BotConfig.getInstance().statuses.isEmpty() && client != null) {
            Placeholder placeholder = new MinecraftPlaceholder(null, DCLink.server, null, null);
            executeWhenReady(() -> {
                timer = new Timer();
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        if(DCLink.server == null) return;
                        String status = BotConfig.getInstance().statuses.get(currentStatus++ % BotConfig.getInstance().statuses.size());
                        String formattedStatus = placeholder.parse(status);
                        client.setStatus(StatusPresence.ONLINE, "Playing " + formattedStatus);
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
            client.setStatus(null, null);
        }
    }

    public void executeWhenReady(Runnable callback) {
        if(!isReady) {
            queuedAction.add(callback);
        } else {
            callback.run();
        }
    }

    public Collection<BridgeEntry> getEntries() {
        return config.entries;
    }

    public boolean isValid() {
        return !StringHelper.notValidString(config.getToken());
    }

    @Override
    public void onReady(UserInfo info) {
        //TODO: Implement guild and stuff so I can fetch emojis

        //        client.getGuildCache().forEach(guild -> {
        //            emojiMap.put(guild.getId(), guild.getEmojis());
        //        });
        LOGGER.info("[RevoltLink] Logged in as: " + info.getAccountName());
        isReady = true;

        for(Runnable callback : queuedAction) {
            callback.run();
        }
        queuedAction.clear();
    }

    @Override
    public void onMessage() {
        //TODO: Receive incoming message from Revolt
    }
}
