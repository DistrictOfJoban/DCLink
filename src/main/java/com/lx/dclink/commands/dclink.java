package com.lx.dclink.commands;

import com.lx.dclink.bridges.Bridge;
import com.lx.dclink.bridges.BridgeManager;
import com.lx.dclink.config.BotConfig;
import com.lx.dclink.DCLink;
import com.lx.dclink.Mappings;
import com.lx.dclink.config.DiscordConfig;
import com.lx.dclink.config.MinecraftConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

public class dclink {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("dclink")
                .requires(ctx -> ctx.hasPermissionLevel(2))
                .then(CommandManager.literal("reload")
                        .executes(dclink::reloadConfig)
                )
                .then(CommandManager.literal("status")
                        .executes(dclink::status)
                )
                .then(CommandManager.literal("save")
                        .executes(dclink::saveConfig)
                )
                .then(CommandManager.literal("enable")
                        .then(CommandManager.literal("outbound")
                                .executes(dclink::enableOutbound)
                        )
                        .then(CommandManager.literal("inbound")
                                .executes(dclink::enableInbound)
                        )
                )
                .then(CommandManager.literal("disable")
                        .then(CommandManager.literal("outbound")
                                .executes(dclink::disableOutbound)
                        )
                        .then(CommandManager.literal("inbound")
                                .executes(dclink::disableInbound)
                        )
                )
        );
    }

    private static int reloadConfig(CommandContext<ServerCommandSource> context) {
        boolean success = DCLink.loadAllConfig();
        MutableText successMessage = Mappings.literalText("DCLink Config Reloaded, re-logging in...").formatted(Formatting.GREEN);
        MutableText failedMessage = Mappings.literalText("DCLink Config Reload Failed, please check console for detail.").formatted(Formatting.RED);
        context.getSource().sendFeedback(success ? successMessage : failedMessage, false);

        /* Re-login if successful */
        if(success) {
            BridgeManager.logout();
            BridgeManager.clearBridges();
            BridgeManager.addDefaultBridges();
            BridgeManager.login();
            BridgeManager.forEach(Bridge::startStatus);
        }

        return 1;
    }

    private static int saveConfig(CommandContext<ServerCommandSource> context) {
        BotConfig.getInstance().save();
        DiscordConfig.getInstance().save();
        MinecraftConfig.getInstance().save();
        context.getSource().sendFeedback(Mappings.literalText("Config saved.").formatted(Formatting.GREEN), false);
        return 1;
    }

    private static int status(CommandContext<ServerCommandSource> context) {
        MutableText onlineText = Mappings.literalText("Online").formatted(Formatting.GREEN);
        MutableText offlineText = Mappings.literalText("Offline").formatted(Formatting.RED);
        MutableText enabledText = Mappings.literalText("Enabled").formatted(Formatting.GREEN);
        MutableText disabledText = Mappings.literalText("Disabled").formatted(Formatting.RED);

        BridgeManager.forEach(bridge -> {
            MutableText title = Mappings.literalText("===== " + bridge.getType().toString() + " =====").formatted(Formatting.GOLD);
            MutableText clientAccount = bridge.isReady() ? Mappings.literalText(bridge.getUserInfo().getAccountName() + " (" + bridge.getUserInfo().getId() + ")").formatted(Formatting.GREEN) : null;
            MutableText clientStatus = getPair("Client Status", bridge.isReady() ? onlineText : offlineText);
            MutableText loggedInAccount = getPair("Logged in as", clientAccount);
            MutableText outBound = getPair("Outbound Messages", BotConfig.getInstance().outboundEnabled ? enabledText : disabledText);
            MutableText inBound = getPair("Inbound Messages", BotConfig.getInstance().inboundEnabled ? enabledText : disabledText);

            context.getSource().sendFeedback(title, false);
            context.getSource().sendFeedback(clientStatus, false);
            if(bridge.isReady()) context.getSource().sendFeedback(loggedInAccount, false);
            context.getSource().sendFeedback(outBound, false);
            context.getSource().sendFeedback(inBound, false);
        });

        return 1;
    }

    public static MutableText getPair(String key, MutableText value) {
        if(value == null) return null;
        MutableText keyText = Mappings.literalText(key + ": ").formatted(Formatting.GOLD);
        return keyText.append(value);
    }

    public static int enableOutbound(CommandContext<ServerCommandSource> context) {
        BotConfig.getInstance().outboundEnabled = true;
        context.getSource().sendFeedback(Mappings.literalText("Outbound messages (MC -> DC) Enabled").formatted(Formatting.GREEN), false);
        return 1;
    }

    public static int enableInbound(CommandContext<ServerCommandSource> context) {
        BotConfig.getInstance().inboundEnabled = true;
        context.getSource().sendFeedback(Mappings.literalText("Inbound messages (DC -> MC) Enabled").formatted(Formatting.GREEN), false);
        return 1;
    }

    public static int disableOutbound(CommandContext<ServerCommandSource> context) {
        BotConfig.getInstance().outboundEnabled = false;
        context.getSource().sendFeedback(Mappings.literalText("Outbound messages (MC -> DC) Disabled").formatted(Formatting.GOLD), false);
        return 1;
    }

    public static int disableInbound(CommandContext<ServerCommandSource> context) {
        BotConfig.getInstance().inboundEnabled = false;
        context.getSource().sendFeedback(Mappings.literalText("Inbound messages (DC -> MC) Disabled").formatted(Formatting.GOLD), false);
        return 1;
    }
}
