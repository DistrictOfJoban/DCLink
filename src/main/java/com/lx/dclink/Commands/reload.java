package com.lx.dclink.Commands;

import com.lx.dclink.Config.BotConfig;
import com.lx.dclink.DCLink;
import com.lx.dclink.DiscordBot;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

public class reload {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("dclink")
                .requires(ctx -> ctx.hasPermissionLevel(2))
                .then(CommandManager.literal("reload")
                        .executes(reload::reloadConfig)
                )
        );
    }

    public static int reloadConfig(CommandContext<ServerCommandSource> context) {
        boolean success = DCLink.loadAllConfig();
        MutableText successMessage = new LiteralText("DCLink Config Reloaded, re-logging in...").formatted(Formatting.GREEN);
        MutableText failedMessage = new LiteralText("DCLink Config Reload Failed, please check console for detail.").formatted(Formatting.RED);
        context.getSource().sendFeedback(success ? successMessage : failedMessage, false);

        /* Re-login if successful*/
        if(success) {
            DiscordBot.disconnect();
            DiscordBot.load(BotConfig.getToken(), BotConfig.getIntents());
        }

        return 1;
    }
}
