package com.lx.dclink.Commands;

import com.lx.dclink.DCLink;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class reload {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("dclinkr")
                .requires(ctx -> ctx.hasPermissionLevel(2))
                .executes(context -> {
                    DCLink.loadAllConfig();
                    return 1;
                })
        );
    }
}
