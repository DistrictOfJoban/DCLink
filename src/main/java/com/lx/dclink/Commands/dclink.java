package com.lx.dclink.Commands;

import com.lx.dclink.Config.BotConfig;
import com.lx.dclink.DCLink;
import com.lx.dclink.DiscordBot;
import com.lx.dclink.Mappings;
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

    public static int reloadConfig(CommandContext<ServerCommandSource> context) {
        boolean success = DCLink.loadAllConfig();
        MutableText successMessage = Mappings.literalText("DCLink Config Reloaded, re-logging in...").formatted(Formatting.GREEN);
        MutableText failedMessage = Mappings.literalText("DCLink Config Reload Failed, please check console for detail.").formatted(Formatting.RED);
        context.getSource().sendFeedback(success ? successMessage : failedMessage, false);

        /* Re-login if successful*/
        if(success) {
            DiscordBot.disconnect();
            DiscordBot.load(BotConfig.getToken(), BotConfig.getIntents());
        }

        return 1;
    }

    public static int status(CommandContext<ServerCommandSource> context) {
        MutableText onlineText = Mappings.literalText("Online").formatted(Formatting.GREEN);
        MutableText offlineText = Mappings.literalText("Offline").formatted(Formatting.RED);
        MutableText enabledText = Mappings.literalText("Enabled").formatted(Formatting.GREEN);
        MutableText disabledText = Mappings.literalText("Disabled").formatted(Formatting.RED);

        MutableText outBound = getPair("Outbound Messages", BotConfig.getOutboundEnabled() ? enabledText : disabledText);
        MutableText inBound = getPair("Inbound Messages", BotConfig.getInboundEnabled() ? enabledText : disabledText);

        context.getSource().sendFeedback(outBound, false);
        context.getSource().sendFeedback(inBound, false);
        return 1;
    }

    public static MutableText getPair(String key, MutableText value) {
        MutableText keyText = Mappings.literalText(key + ": ").formatted(Formatting.GOLD);
        return keyText.append(value);
    }

    public static int enableOutbound(CommandContext<ServerCommandSource> context) {
        BotConfig.setOutboundEnabled(true);
        context.getSource().sendFeedback(Mappings.literalText("Outbound messages (MC -> DC) Enabled").formatted(Formatting.GREEN), false);
        return 1;
    }

    public static int enableInbound(CommandContext<ServerCommandSource> context) {
        BotConfig.setInboundEnabled(true);
        context.getSource().sendFeedback(Mappings.literalText("Inbound messages (DC -> MC) Enabled").formatted(Formatting.GREEN), false);
        return 1;
    }

    public static int disableOutbound(CommandContext<ServerCommandSource> context) {
        BotConfig.setOutboundEnabled(false);
        context.getSource().sendFeedback(Mappings.literalText("Outbound messages (MC -> DC) Disabled").formatted(Formatting.GOLD), false);
        return 1;
    }

    public static int disableInbound(CommandContext<ServerCommandSource> context) {
        BotConfig.setInboundEnabled(false);
        context.getSource().sendFeedback(Mappings.literalText("Inbound messages (DC -> MC) Disabled").formatted(Formatting.GOLD), false);
        return 1;
    }
}