package com.lx862.dclink.minecraft.events;

import com.lx862.dclink.bridges.BridgeManager;
import com.lx862.dclink.data.BridgeContext;
import com.lx862.dclink.data.MinecraftPlaceholder;
import com.lx862.dclink.data.Placeholder;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class PlayerManager {

    public static void playerJoin(ClientConnection handler, ServerPlayerEntity player) {
        ServerWorld world = player.getWorld();
        String worldId = world.getRegistryKey().getValue().toString();
        BridgeManager.forEach(bridge -> {
            for (BridgeContext entry : bridge.getContext()) {
                if (!entry.allowedDimension.isEmpty() && !entry.allowedDimension.contains(worldId)) continue;
                Placeholder placeholder = new MinecraftPlaceholder(player, player.getServer(), world, null);
                bridge.sendMessage(
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
        ServerWorld world = player.getWorld();
        String worldId = world.getRegistryKey().getValue().toString();

        BridgeManager.forEach(bridge -> {
            for(BridgeContext context : bridge.getContext()) {
                if(!context.allowedDimension.isEmpty() && !context.allowedDimension.contains(worldId)) continue;
                String disconnectReason = context.message.getPlayerDisconnectReason(disconnectReasonText.getString());
                Placeholder placeholder = new MinecraftPlaceholder(player, player.getServer(), world, null);
                placeholder.addPlaceholder("reason", disconnectReason);

                bridge.sendMessage(context.message.playerLeft, placeholder, context.channelID, context.allowMention, context.enableEmoji);
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
                bridge.sendMessage(context.message.playerDeath, placeholder, context.channelID, context.allowMention, context.enableEmoji);
            }
        });
    }

    public static void playerAdvancementGranted(ServerPlayerEntity player, AdvancementProgress advancementProgress, World world, Advancement advancement) {
        if (advancementProgress.isDone()) {
            if (advancement.getDisplay() != null && advancement.getDisplay().shouldAnnounceToChat()) {
                Placeholder placeholder = new MinecraftPlaceholder(player, player.getServer(), world, null);
                placeholder.addPlaceholder("advancement", advancement.getDisplay().getTitle().getString());
                placeholder.addPlaceholder("advancementDetails", advancement.getDisplay().getDescription().getString());

                BridgeManager.forEach(bridge -> {
                    for(BridgeContext entry : bridge.getContext()) {
                        bridge.sendMessage(entry.message.playerAdvancement, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
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

                bridge.sendMessage(entry.message.changeDimension, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
            }
        });
    }

    public static void sendMessage(String content, ServerPlayerEntity player) {
        ServerWorld world = player.getWorld();
        String worldId = world.getRegistryKey().getValue().toString();
        Placeholder placeholder = new MinecraftPlaceholder(player, player.server, player.getWorld(), content);

        BridgeManager.forEach(bridge -> {
            for(BridgeContext entry : bridge.getContext()) {
                if(!entry.allowedDimension.isEmpty() && !entry.allowedDimension.contains(worldId)) continue;
                bridge.sendMessage(entry.message.relay, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
            }
        });
    }

    public static void sendCommand(String content, ServerPlayerEntity player) {
        ServerWorld world = player.getWorld();
        String worldId = world.getRegistryKey().getValue().toString();
        Placeholder placeholder = new MinecraftPlaceholder(player, player.server, player.getWorld(), content);

        BridgeManager.forEach(bridge -> {
            for(BridgeContext entry : bridge.getContext()) {
                if(!entry.allowedDimension.isEmpty() && !entry.allowedDimension.contains(worldId)) continue;
                bridge.sendMessage(entry.message.relayCommand, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
            }
        });
    }
}
