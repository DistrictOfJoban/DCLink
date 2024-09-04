package com.lx862.dclink.minecraft.commands;

import com.lx862.dclink.DCLink;
import com.lx862.dclink.bridges.BridgeManager;
import com.lx862.dclink.config.BotConfig;
import com.lx862.dclink.minecraft.MinecraftSource;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
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

        if(FabricLoader.getInstance().isDevelopmentEnvironment()) {
            dispatcher.register(CommandManager.literal("dclinkDebug")
                    .requires(ctx -> ctx.hasPermissionLevel(2))
                    .then(CommandManager.literal("crashServer")
                            .executes(context -> {
                                DCLink.getMcSource().crashServerOnTick = true;
                                return 1;
                            })
                    ));
        }
    }

    private static int reloadConfig(CommandContext<ServerCommandSource> context) {
        boolean success = DCLink.loadAllConfig();
        MutableText successMessage = Text.literal("DCLink Config Reloaded, re-logging in...").formatted(Formatting.GREEN);
        MutableText failedMessage = Text.literal("DCLink Config Reload Failed, please check console for detail.").formatted(Formatting.RED);
        context.getSource().sendFeedback(success ? successMessage : failedMessage, false);

        /* Re-login if successful */
        if(success) {
            BridgeManager.logout();
            BridgeManager.clearBridges();
            BridgeManager.addDefaultBridges();
            BridgeManager.login();
            BridgeManager.startStatus();
        }

        return 1;
    }

    private static int status(CommandContext<ServerCommandSource> context) {
        MutableText onlineText = Text.literal("Online").formatted(Formatting.GREEN);
        MutableText offlineText = Text.literal("Offline").formatted(Formatting.RED);
        MutableText enabledText = Text.literal("Enabled").formatted(Formatting.GREEN);
        MutableText disabledText = Text.literal("Disabled").formatted(Formatting.RED);

        BridgeManager.forEach(bridge -> {
            MutableText title = Text.literal("===== " + bridge.getType().toString() + " =====").formatted(Formatting.GOLD);
            MutableText clientAccount = bridge.isReady() ? Text.literal(bridge.getUserInfo().getName() + " (" + bridge.getUserInfo().getId() + ")").formatted(Formatting.GREEN) : null;
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
        MutableText keyText = Text.literal(key + ": ").formatted(Formatting.GOLD);
        return keyText.append(value);
    }

    public static int enableOutbound(CommandContext<ServerCommandSource> context) {
        BotConfig.getInstance().outboundEnabled = true;
        context.getSource().sendFeedback(Text.literal("Outbound messages (MC -> DC) Enabled").formatted(Formatting.GREEN), false);
        return 1;
    }

    public static int enableInbound(CommandContext<ServerCommandSource> context) {
        BotConfig.getInstance().inboundEnabled = true;
        context.getSource().sendFeedback(Text.literal("Inbound messages (DC -> MC) Enabled").formatted(Formatting.GREEN), false);
        return 1;
    }

    public static int disableOutbound(CommandContext<ServerCommandSource> context) {
        BotConfig.getInstance().outboundEnabled = false;
        context.getSource().sendFeedback(Text.literal("Outbound messages (MC -> DC) Disabled").formatted(Formatting.GOLD), false);
        return 1;
    }

    public static int disableInbound(CommandContext<ServerCommandSource> context) {
        BotConfig.getInstance().inboundEnabled = false;
        context.getSource().sendFeedback(Text.literal("Inbound messages (DC -> MC) Disabled").formatted(Formatting.GOLD), false);
        return 1;
    }
}
