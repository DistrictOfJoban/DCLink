package com.lx862.dclink.minecraft.events;

import com.lx862.dclink.bridges.BridgeManager;
import com.lx862.dclink.data.BridgeContext;
import com.lx862.dclink.data.MinecraftEntry;
import com.lx862.dclink.data.MinecraftPlaceholder;
import com.lx862.dclink.data.Placeholder;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.util.crash.CrashReport;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ServerManager {
    public static long serverStartingTimestamp;
    public static long serverStartedTimestamp;
    private static MinecraftServer serverInstance = null;

    public static @Nullable MinecraftServer getServer() {
        return serverInstance;
    }

    public static boolean serverAlive() {
        return serverInstance != null && serverInstance.isRunning();
    }

    public static void serverPrelaunch() {
        serverStartingTimestamp = System.currentTimeMillis();
    }

    public static void serverStarting(MinecraftServer server) {
        try {
            serverInstance = server;

            BridgeManager.clearBridges();
            BridgeManager.addDefaultBridges();
            BridgeManager.login();

            BridgeManager.forEach(bridge -> {
                for(BridgeContext entry : bridge.getContext()) {
                    BridgeManager.sendMessage(bridge, entry.message.serverStarting, null, entry.channelID, entry.allowMention, entry.enableEmoji);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void serverStarted(MinecraftServer server) {
        try {
            serverStartedTimestamp = System.currentTimeMillis();
            Placeholder placeholder = new MinecraftPlaceholder(null, server, null, null);
            placeholder.addTimePlaceholder("time", serverStartedTimestamp - serverStartingTimestamp);
            BridgeManager.forEach(bridge -> {
                for(BridgeContext entry : bridge.getContext()) {
                    BridgeManager.sendMessage(bridge, entry.message.serverStarted, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
                }
            });
            BridgeManager.startStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void serverStopping(MinecraftServer server) {
        try {
            long serverStoppingTimestamp = System.currentTimeMillis();
            Placeholder placeholder = new MinecraftPlaceholder(null, server, null, null);
            placeholder.addTimePlaceholder("time", serverStoppingTimestamp - serverStartedTimestamp);

            BridgeManager.forEach(bridge -> {
                for(BridgeContext entry : bridge.getContext()) {
                    BridgeManager.sendMessage(bridge, entry.message.serverStopping, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void serverStopped(MinecraftServer server) {
        try {
            BridgeManager.forEach(bridge -> {
                for(BridgeContext entry : bridge.getContext()) {
                    BridgeManager.sendMessage(bridge, entry.message.serverStopped, null, entry.channelID, entry.allowMention, entry.enableEmoji);
                }
            });

            serverInstance = null;
            BridgeManager.logout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void serverCrashed(CrashReport crashReport) {
        try {
            Placeholder placeholder = new MinecraftPlaceholder();
            placeholder.addPlaceholder("reason", crashReport.getMessage());
            placeholder.addPlaceholder("stacktrace", crashReport.getCauseAsString());

            BridgeManager.forEach(bridge -> {
                for(BridgeContext entry : bridge.getContext()) {
                    BridgeManager.sendMessage(bridge, entry.message.serverCrashed, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(List<MutableText> textToBeSent, MinecraftEntry entry) {
        if(serverInstance == null) return;

        List<String> dimensions = new ArrayList<>(entry.sendDimension);

        if(dimensions.isEmpty()) {
            //Send to all
            broadcast(textToBeSent);
        } else {
            for(String worldKeyConfig : dimensions) {
                for(ServerWorld world : serverInstance.getWorlds()) {
                    String worldKey = world.getRegistryKey().getValue().toString();
                    if(worldKeyConfig.equals(worldKey)) {
                        sendMessage(world.getPlayers(), textToBeSent);
                    }
                }
            }
        }
    }

    public static void sendMessage(List<ServerPlayerEntity> players, List<MutableText> textToBeSent) {
        for(ServerPlayerEntity player : players) {
            for(MutableText text : textToBeSent) {
                player.sendMessage(text, false);
            }
        }
    }

    public static void broadcast(List<MutableText> textToBeSent) {
        for(MutableText text : textToBeSent) {
            serverInstance.sendMessage(text);
        }
    }

    public static void registerCommand(Consumer<CommandDispatcher<ServerCommandSource>> callback) {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            callback.accept(dispatcher);
        }));
    }
}
