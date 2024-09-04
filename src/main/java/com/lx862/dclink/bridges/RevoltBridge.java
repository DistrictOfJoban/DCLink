package com.lx862.dclink.bridges;

import com.lx862.dclink.config.RevoltConfig;
import com.lx862.dclink.data.BridgeContext;
import com.lx862.dclink.minecraft.MinecraftSource;
import com.lx862.vendorneutral.usermember.User;
import com.lx862.dclink.util.StringHelper;
import com.lx862.revoltimpl.RevoltClient;
import com.lx862.revoltimpl.RevoltListener;
import com.lx862.revoltimpl.data.Channel;
import com.lx862.revoltimpl.data.Message;
import com.lx862.revoltimpl.data.StatusPresence;
import com.lx862.revoltimpl.data.text.embed.TextEmbed;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.regex.Pattern;

public class RevoltBridge implements Bridge, RevoltListener {
    private static final Pattern EMBED_PATTERN = Pattern.compile("(<<<.+>>>)");
    private static final Logger LOGGER = LogManager.getLogger("DCLinkRevolt");
    private final Collection<Runnable> queuedAction;
    public RevoltClient client;
    public Map<String, List<RichCustomEmoji>> emojiMap = new HashMap<>();
    private boolean isReady = false;
    private final String token;
    private final RevoltConfig config;
    private final MinecraftSource source;

    public RevoltBridge(RevoltConfig config, MinecraftSource source) {
        this.source = source;
        this.config = config;
        this.client = new RevoltClient(config.getToken());
        this.client.addListener(this);
        this.token = config.getToken();
        this.queuedAction = new ArrayList<>();
    }

    @Override
    public void handleMessage(String finalMessage, String channelId, List<com.lx862.vendorneutral.texts.embed.TextEmbed> embeds, boolean allowEmoji, boolean allowMention) {
        Channel channel = client.getChannel(channelId);
        if(channel == null) return;

        List<TextEmbed> rvEmbeds = embeds.stream().map(com.lx862.vendorneutral.texts.embed.TextEmbed::toRevolt).toList();
        channel.sendMessage(client, finalMessage, rvEmbeds);
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

    public User getUserInfo() {
        return client.getSelf();
    }

    public void updateStatus(String status) {
        client.setStatus(StatusPresence.ONLINE, "Playing " + status);
    }

    public void stopStatus() {
        if(isReady()) client.setStatus(null, null);
    }

    public Collection<BridgeContext> getContext() {
        return config.entries;
    }

    public boolean isValid() {
        return !StringHelper.notValidString(config.getToken());
    }

    @Override
    public void onReady(User self) {
        //TODO: Implement server and stuff so I can fetch emojis

        //        client.getGuildCache().forEach(guild -> {
        //            emojiMap.put(guild.getId(), guild.getEmojis());
        //        });
        LOGGER.info("[RevoltLink] Logged in as: @" + self.getName());
        isReady = true;

        for(Runnable callback : queuedAction) {
            callback.run();
        }
        queuedAction.clear();
    }

    @Override
    public void onMessage(Message message) {
//        if (!isReady || message.getAuthor(client) == null || !BotConfig.getInstance().inboundEnabled) return;
//        if(message.getAuthor(client).getId().equals(client.getSelf().getId())) return;

        System.out.println(message.getContent());
        //TODO: Receive incoming message from Revolt
    }
}
