package com.lx.dclink.Events;

import com.lx.dclink.Config.DiscordConfig;
import com.lx.dclink.DCLink;
import com.lx.dclink.Data.ContentType;
import com.lx.dclink.Data.DCEntry;
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
        for(DCEntry entry : DiscordConfig.entries) {
            if(!entry.contentType.contains(ContentType.PLAYER)) continue;
            if(!entry.allowedDimension.isEmpty() && !entry.allowedDimension.contains(worldId)) {
                continue;
            }

            DiscordBot.sendSimpleEmbed(
                    entry.message.getPlayerJoinMessage(player, server, world),
                    entry.channelID,
                    entry.getThumbnailURL(player, server, world)
            );
        }
    }

    public static void playerLeft(ServerPlayNetworkHandler handler, MinecraftServer server) {
        ServerPlayerEntity player = Mappings.getPlayer(handler);
        ServerWorld world = Mappings.getServerWorld(player);
        String worldId = world.getRegistryKey().getValue().toString();
        Text disconnectReasonText = handler.getConnection().getDisconnectReason();
        for(DCEntry entry : DiscordConfig.entries) {
            if(!entry.contentType.contains(ContentType.PLAYER)) continue;
            if(!entry.allowedDimension.isEmpty() && !entry.allowedDimension.contains(worldId)) {
                continue;
            }
            String disconnectReason = disconnectReasonText == null ? "" : entry.message.getPlayerDisconnectReason(disconnectReasonText.getString());
            String leftMessage = entry.message.getPlayerLeftMessage(player, server, world)
            .replace("{reason}", disconnectReason);

            DiscordBot.sendSimpleEmbed(
                    leftMessage,
                    entry.channelID,
                    entry.getThumbnailURL(player, server, world)
            );
        }
    }

    public static void playerDied(ServerPlayerEntity player, DamageSource source, World world) {
        String worldId = world.getRegistryKey().getValue().toString();
        for(DCEntry entry : DiscordConfig.entries) {
            if(!entry.contentType.contains(ContentType.PLAYER)) continue;
            if(!entry.allowedDimension.isEmpty() && !entry.allowedDimension.contains(worldId)) {
                continue;
            }

            DiscordBot.sendSimpleEmbed(
                    entry.message.getPlayerDeathMessage(player, source, world),
                    entry.channelID
            );
        }
    }

    public static void playerAdvancementGranted(ServerPlayerEntity player, AdvancementProgress advancementProgress, World world, Advancement advancement) {
        if (advancementProgress.isDone()) {
            if (advancement.getDisplay() != null && advancement.getDisplay().shouldAnnounceToChat()) {
                for(DCEntry entry : DiscordConfig.entries) {
                    if(!entry.contentType.contains(ContentType.PLAYER)) continue;

                    DiscordBot.sendSimpleEmbed(
                            entry.message.getPlayerAdvancementMessage(player, world, advancement),
                            entry.channelID
                    );
                }
            }
        }
    }

    public static void worldChanged(ServerWorld originalWorld, ServerWorld currentWorld, ServerPlayerEntity player) {
        String oldWorldId = originalWorld.getRegistryKey().getValue().toString();
        String newWorldId = currentWorld.getRegistryKey().getValue().toString();
        for(DCEntry entry : DiscordConfig.entries) {
            if(!entry.contentType.contains(ContentType.PLAYER)) continue;
            if(!entry.allowedDimension.isEmpty() && !entry.allowedDimension.contains(newWorldId) && !entry.allowedDimension.contains(oldWorldId)) {
                continue;
            }

            DiscordBot.sendSimpleEmbed(
                    entry.message.getDimensionChangeMessage(player, DCLink.server, currentWorld),
                    entry.channelID,
                    entry.getThumbnailURL(player, DCLink.server, currentWorld)
            );
        }
    }

    public static void sendMessage(String content, ServerPlayerEntity player) {
        ServerWorld world = Mappings.getServerWorld(player);
        String worldId = world.getRegistryKey().getValue().toString();

        for(DCEntry entry : DiscordConfig.entries) {
            if(!entry.allowedDimension.isEmpty() && !entry.allowedDimension.contains(worldId)) {
                continue;
            }

            if(content.startsWith("/")) {
                if(entry.contentType.contains(ContentType.COMMAND)) {
                    DiscordBot.sendMessage(
                            entry.message.getCommandMessage(content, player, DCLink.server, world),
                            entry
                    );
                }
                return;
            }

            if(entry.contentType.contains(ContentType.CHAT)) {
                DiscordBot.sendMessage(
                        entry.message.getPlayerMessage(content, player, DCLink.server, world),
                        entry
                );
            }
        }
    }
}
