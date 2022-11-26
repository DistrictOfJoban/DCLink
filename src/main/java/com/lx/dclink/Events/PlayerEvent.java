package com.lx.dclink.Events;

import com.lx.dclink.Config.DiscordConfig;
import com.lx.dclink.DCLink;
import com.lx.dclink.Data.DCEntry;
import com.lx.dclink.Data.MinecraftPlaceholder;
import com.lx.dclink.Data.Placeholder;
import com.lx.dclink.DiscordBot;
import com.lx.dclink.Mappings;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class PlayerEvent {

    public static void playerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        ServerPlayerEntity player = Mappings.getPlayer(handler);
        ServerWorld world = Mappings.getServerWorld(player);
        String worldId = world.getRegistryKey().getValue().toString();
        for (DCEntry entry : DiscordConfig.entries) {
            if (!entry.allowedDimension.isEmpty() && !entry.allowedDimension.contains(worldId)) continue;

            Placeholder placeholder = new MinecraftPlaceholder(player, server, world, null);

            DiscordBot.sendUniversalMessage(
                    entry.message.playerJoin,
                    placeholder,
                    entry.channelID,
                    entry.allowMention,
                    entry.enableEmoji
            );
        }
    }

    public static void playerLeft(ServerPlayNetworkHandler handler, MinecraftServer server) {
        ServerPlayerEntity player = Mappings.getPlayer(handler);
        ServerWorld world = Mappings.getServerWorld(player);
        String worldId = world.getRegistryKey().getValue().toString();
        Text disconnectReasonText = handler.getConnection().getDisconnectReason();
        for(DCEntry entry : DiscordConfig.entries) {
            if(!entry.allowedDimension.isEmpty() && !entry.allowedDimension.contains(worldId)) continue;
            String disconnectReason = disconnectReasonText == null ? "" : entry.message.getPlayerDisconnectReason(disconnectReasonText.getString());
            Placeholder placeholder = new MinecraftPlaceholder(player, server, world, null);
            placeholder.addPlaceholder("reason", disconnectReason);

            DiscordBot.sendUniversalMessage(entry.message.playerLeft, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
        }
    }

    public static void playerDied(ServerPlayerEntity player, DamageSource source, World world) {
        String worldId = world.getRegistryKey().getValue().toString();
        String deathCause = source.getDeathMessage(player).getString().replace(player.getGameProfile().getName(), "");
        for(DCEntry entry : DiscordConfig.entries) {
            if(!entry.allowedDimension.isEmpty() && !entry.allowedDimension.contains(worldId)) continue;

            Placeholder placeholder = new MinecraftPlaceholder(player, DCLink.server, world, null);
            placeholder.addPlaceholder("cause", deathCause);

            DiscordBot.sendUniversalMessage(entry.message.playerDeath, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
        }
    }

    public static void playerAdvancementGranted(ServerPlayerEntity player, AdvancementProgress advancementProgress, World world, Advancement advancement) {
        if (advancementProgress.isDone()) {
            if (advancement.getDisplay() != null && advancement.getDisplay().shouldAnnounceToChat()) {
                for(DCEntry entry : DiscordConfig.entries) {
                    Placeholder placeholder = new MinecraftPlaceholder(player, DCLink.server, world, null);
                    placeholder.addPlaceholder("advancement", advancement.getDisplay().getTitle().getString());
                    placeholder.addPlaceholder("advancementDetails", advancement.getDisplay().getDescription().getString());

                    DiscordBot.sendUniversalMessage(entry.message.playerAdvancement, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
                }
            }
        }
    }

    public static void worldChanged(ServerWorld originalWorld, ServerWorld currentWorld, ServerPlayerEntity player) {
        String oldWorldId = originalWorld.getRegistryKey().getValue().toString();
        String newWorldId = currentWorld.getRegistryKey().getValue().toString();
        for(DCEntry entry : DiscordConfig.entries) {
            if(!entry.allowedDimension.isEmpty() && !entry.allowedDimension.contains(newWorldId) && !entry.allowedDimension.contains(oldWorldId)) continue;
            Placeholder placeholder = new MinecraftPlaceholder(player, DCLink.server, currentWorld, null);

            DiscordBot.sendUniversalMessage(entry.message.changeDimension, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
        }
    }

    public static void sendMessage(String content, ServerPlayerEntity player) {
        ServerWorld world = Mappings.getServerWorld(player);
        String worldId = world.getRegistryKey().getValue().toString();
        Placeholder placeholder = new MinecraftPlaceholder(player, player.server, player.world, content);

        for(DCEntry entry : DiscordConfig.entries) {
            if(!entry.allowedDimension.isEmpty() && !entry.allowedDimension.contains(worldId)) {
                continue;
            }

            if(content.startsWith("/")) {
                DiscordBot.sendUniversalMessage(entry.message.relayCommand, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
            } else {
                DiscordBot.sendUniversalMessage(entry.message.relay, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
            }
        }
    }
}
