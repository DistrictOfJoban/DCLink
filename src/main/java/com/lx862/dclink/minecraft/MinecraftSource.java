package com.lx862.dclink.minecraft;

import com.lx862.dclink.DCLink;
import com.lx862.dclink.bridges.BridgeManager;
import com.lx862.dclink.data.*;
import com.lx862.dclink.minecraft.commands.DCLinkCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.util.crash.CrashReport;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MinecraftSource {
    public boolean crashServerOnTick = false;
    private long serverStartingTimestamp = -1;
    private long serverStartedTimestamp = -1;
    private static MinecraftServer serverInstance = null;

    public void initialize() {
        try {
            ServerLifecycleEvents.SERVER_STARTING.register(this::serverStarting);
            ServerLifecycleEvents.SERVER_STARTED.register(this::serverStarted);
            ServerLifecycleEvents.SERVER_STOPPING.register(this::serverStopping);
            ServerLifecycleEvents.SERVER_STOPPED.register(this::serverStopped);
            ServerTickEvents.START_SERVER_TICK.register(e -> {
                if(crashServerOnTick) {
                    throw new RuntimeException("Manually triggered DCLink Debug crash.");
                }
            });
            CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
                DCLinkCommand.register(dispatcher);
            }));
        } catch (Exception e) {
            DCLink.LOGGER.error(e);
        }
    }

    public boolean alive() {
        return serverInstance != null && serverInstance.isRunning();
    }

    public @Nullable MinecraftServer getServer() {
        return serverInstance;
    }

    public void sourceStarted() {
        this.serverStartingTimestamp = System.currentTimeMillis();
    }

    public void serverStarting(MinecraftServer server) {
        BridgeManager.startup();
        serverInstance = server;

        BridgeManager.login();
        BridgeManager.forEach(bridge -> {
            for(BridgeContext entry : bridge.getContext()) {
                BridgeManager.sendMessage(bridge, entry.message.serverStarting, null, entry.channelID, entry.allowMention, entry.enableEmoji);
            }
        });
    }

    public void serverStarted(MinecraftServer server) {
        serverStartedTimestamp = System.currentTimeMillis();
        Placeholder placeholder = new MinecraftPlaceholder(null, server, null, null);
        placeholder.addTimePlaceholder("time", serverStartedTimestamp - serverStartingTimestamp);
        BridgeManager.forEach(bridge -> {
            for(BridgeContext entry : bridge.getContext()) {
                BridgeManager.sendMessage(bridge, entry.message.serverStarted, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
            }
        });
        BridgeManager.startStatus();
    }

    public void serverStopping(MinecraftServer server) {
        long serverStoppingTimestamp = System.currentTimeMillis();
        Placeholder placeholder = new MinecraftPlaceholder(null, server, null, null);
        placeholder.addTimePlaceholder("time", serverStoppingTimestamp - serverStartedTimestamp);

        BridgeManager.forEach(bridge -> {
            for(BridgeContext entry : bridge.getContext()) {
                BridgeManager.sendMessage(bridge, entry.message.serverStopping, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
            }
        });
    }

    public void serverStopped(MinecraftServer server) {
        BridgeManager.forEach(bridge -> {
            for(BridgeContext entry : bridge.getContext()) {
                BridgeManager.sendMessage(bridge, entry.message.serverStopped, null, entry.channelID, entry.allowMention, entry.enableEmoji);
            }
        });

        serverInstance = null;
        BridgeManager.shutdown();
    }

    public void serverCrashed(CrashReport crashReport) {
        Placeholder placeholder = new MinecraftPlaceholder();
        placeholder.addPlaceholder("reason", crashReport.getMessage());
        placeholder.addPlaceholder("stacktrace", crashReport.getCauseAsString());

        BridgeManager.forEach(bridge -> {
            for(BridgeContext entry : bridge.getContext()) {
                BridgeManager.sendMessage(bridge, entry.message.serverCrashed, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
            }
        });
    }

    public void sendMessage(List<MutableText> textToBeSent, MinecraftEntry entry) {
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

    public void sendMessage(List<ServerPlayerEntity> players, List<MutableText> textToBeSent) {
        for(ServerPlayerEntity player : players) {
            for(MutableText text : textToBeSent) {
                player.sendMessage(text, false);
            }
        }
    }

    public void broadcast(List<MutableText> textToBeSent) {
        for(MutableText text : textToBeSent) {
            serverInstance.sendMessage(text);
        }
    }
}
