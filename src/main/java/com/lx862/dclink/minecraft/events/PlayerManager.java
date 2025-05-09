package com.lx862.dclink.minecraft.events;

import java.util.Optional;

import com.lx862.dclink.bridges.BridgeManager;
import com.lx862.dclink.data.BridgeContext;
import com.lx862.dclink.data.MinecraftPlaceholder;
import com.lx862.dclink.data.Placeholder;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class PlayerManager {

    public static void playerJoin(ClientConnection handler, ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        String worldId = world.getRegistryKey().getValue().toString();
        BridgeManager.forEach(bridge -> {
            for (BridgeContext entry : bridge.getContext()) {
                if (!entry.allowedDimension.isEmpty() && !entry.allowedDimension.contains(worldId)) continue;
                Placeholder placeholder = new MinecraftPlaceholder(player, player.getServer(), world, null);
                BridgeManager.sendMessage(
                        bridge,
                        entry.message.playerJoin,
                        placeholder,
                        entry.channelID,
                        entry.allowMention,
                        entry.enableEmoji
                );
            }
        });
    }

    public static void playerLeft(Text disconnectReasonText, ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        String worldId = world.getRegistryKey().getValue().toString();

        BridgeManager.forEach(bridge -> {
            for(BridgeContext context : bridge.getContext()) {
                if(!context.allowedDimension.isEmpty() && !context.allowedDimension.contains(worldId)) continue;
                String disconnectReason = context.message.getPlayerDisconnectReason(disconnectReasonText.getString());
                Placeholder placeholder = new MinecraftPlaceholder(player, player.getServer(), world, null);
                placeholder.addPlaceholder("reason", disconnectReason);

                BridgeManager.sendMessage(bridge, context.message.playerLeft, placeholder, context.channelID, context.allowMention, context.enableEmoji);
            }
        });
    }

    public static void playerDied(ServerPlayerEntity player, DamageSource source, World world) {
        String worldId = world.getRegistryKey().getValue().toString();
        String deathCause = source.getDeathMessage(player).getString().replace(player.getDisplayName().toString(), "");
        Placeholder placeholder = new MinecraftPlaceholder(player, player.getServer(), world, null);
        placeholder.addPlaceholder("cause", deathCause);

        BridgeManager.forEach(bridge -> {
            for(BridgeContext context : bridge.getContext()) {
                if(!context.allowedDimension.isEmpty() && !context.allowedDimension.contains(worldId)) continue;
                BridgeManager.sendMessage(bridge, context.message.playerDeath, placeholder, context.channelID, context.allowMention, context.enableEmoji);
            }
        });
    }

    public static void playerAdvancementGranted(ServerPlayerEntity player, AdvancementProgress advancementProgress, World world, Advancement advancement) {
        if (advancementProgress.isDone()) {
            Optional<AdvancementDisplay> optDisplay = advancement.display();
            if (optDisplay.isPresent() && optDisplay.get().shouldAnnounceToChat()) {
                AdvancementDisplay display = optDisplay.get();

                Placeholder placeholder = new MinecraftPlaceholder(player, player.getServer(), world, null);
                placeholder.addPlaceholder("advancement", display.getTitle().getString());
                placeholder.addPlaceholder("advancementDetails", display.getDescription().getString());

                BridgeManager.forEach(bridge -> {
                    for(BridgeContext entry : bridge.getContext()) {
                        BridgeManager.sendMessage(bridge, entry.message.playerAdvancement, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
                    }
                });
            }
        }
    }

    public static void worldChanged(ServerWorld originalWorld, ServerWorld currentWorld, ServerPlayerEntity player) {
        String oldWorldId = originalWorld.getRegistryKey().getValue().toString();
        String newWorldId = currentWorld.getRegistryKey().getValue().toString();
        Placeholder placeholder = new MinecraftPlaceholder(player, player.getServer(), currentWorld, null);

        BridgeManager.forEach(bridge -> {
            for(BridgeContext entry : bridge.getContext()) {
                if(!entry.allowedDimension.isEmpty() && !entry.allowedDimension.contains(newWorldId) && !entry.allowedDimension.contains(oldWorldId)) continue;

                BridgeManager.sendMessage(bridge, entry.message.changeDimension, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
            }
        });
    }

    public static void sendMessage(String content, ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        String worldId = world.getRegistryKey().getValue().toString();
        Placeholder placeholder = new MinecraftPlaceholder(player, player.server, player.getWorld(), content);

        BridgeManager.forEach(bridge -> {
            for(BridgeContext entry : bridge.getContext()) {
                if(!entry.allowedDimension.isEmpty() && !entry.allowedDimension.contains(worldId)) continue;
                BridgeManager.sendMessage(bridge, entry.message.relay, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
            }
        });
    }

    public static void sendCommand(String content, ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        String worldId = world.getRegistryKey().getValue().toString();
        Placeholder placeholder = new MinecraftPlaceholder(player, player.server, player.getWorld(), content);

        BridgeManager.forEach(bridge -> {
            for(BridgeContext entry : bridge.getContext()) {
                if(!entry.allowedDimension.isEmpty() && !entry.allowedDimension.contains(worldId)) continue;
                BridgeManager.sendMessage(bridge, entry.message.relayCommand, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
            }
        });
    }
}
