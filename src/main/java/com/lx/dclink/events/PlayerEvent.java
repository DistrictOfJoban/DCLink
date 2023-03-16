package com.lx.dclink.events;

import com.lx.dclink.config.DiscordConfig;
import com.lx.dclink.DCLink;
import com.lx.dclink.data.DiscordEntry;
import com.lx.dclink.data.MinecraftPlaceholder;
import com.lx.dclink.data.Placeholder;
import com.lx.dclink.Mappings;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class PlayerEvent {

    public static void playerJoin(ClientConnection handler, ServerPlayerEntity player) {
        ServerWorld world = Mappings.getServerWorld(player);
        String worldId = world.getRegistryKey().getValue().toString();
        for (DiscordEntry entry : DiscordConfig.getInstance().entries) {
            if (!entry.allowedDimension.isEmpty() && !entry.allowedDimension.contains(worldId)) continue;

            Placeholder placeholder = new MinecraftPlaceholder(player, DCLink.server, world, null);

            DCLink.bot.sendMessage(
                    entry.message.playerJoin,
                    placeholder,
                    entry.channelID,
                    entry.allowMention,
                    entry.enableEmoji
            );
        }
    }

    public static void playerLeft(Text disconnectReasonText, ServerPlayerEntity player) {
        ServerWorld world = Mappings.getServerWorld(player);
        String worldId = world.getRegistryKey().getValue().toString();
        for(DiscordEntry entry : DiscordConfig.getInstance().entries) {
            if(!entry.allowedDimension.isEmpty() && !entry.allowedDimension.contains(worldId)) continue;
            String disconnectReason = entry.message.getPlayerDisconnectReason(disconnectReasonText.getString());
            Placeholder placeholder = new MinecraftPlaceholder(player, DCLink.server, world, null);
            placeholder.addPlaceholder("reason", disconnectReason);

            DCLink.bot.sendMessage(entry.message.playerLeft, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
        }
    }

    public static void playerDied(ServerPlayerEntity player, DamageSource source, World world) {
        String worldId = world.getRegistryKey().getValue().toString();
        String deathCause = source.getDeathMessage(player).getString().replace(player.getGameProfile().getName(), "");
        for(DiscordEntry entry : DiscordConfig.getInstance().entries) {
            if(!entry.allowedDimension.isEmpty() && !entry.allowedDimension.contains(worldId)) continue;

            Placeholder placeholder = new MinecraftPlaceholder(player, DCLink.server, world, null);
            placeholder.addPlaceholder("cause", deathCause);

            DCLink.bot.sendMessage(entry.message.playerDeath, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
        }
    }

    public static void playerAdvancementGranted(ServerPlayerEntity player, AdvancementProgress advancementProgress, World world, Advancement advancement) {
        if (advancementProgress.isDone()) {
            if (advancement.getDisplay() != null && advancement.getDisplay().shouldAnnounceToChat()) {
                Placeholder placeholder = new MinecraftPlaceholder(player, DCLink.server, world, null);
                placeholder.addPlaceholder("advancement", advancement.getDisplay().getTitle().getString());
                placeholder.addPlaceholder("advancementDetails", advancement.getDisplay().getDescription().getString());

                for(DiscordEntry entry : DiscordConfig.getInstance().entries) {
                    DCLink.bot.sendMessage(entry.message.playerAdvancement, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
                }
            }
        }
    }

    public static void worldChanged(ServerWorld originalWorld, ServerWorld currentWorld, ServerPlayerEntity player) {
        String oldWorldId = originalWorld.getRegistryKey().getValue().toString();
        String newWorldId = currentWorld.getRegistryKey().getValue().toString();
        for(DiscordEntry entry : DiscordConfig.getInstance().entries) {
            if(!entry.allowedDimension.isEmpty() && !entry.allowedDimension.contains(newWorldId) && !entry.allowedDimension.contains(oldWorldId)) continue;
            Placeholder placeholder = new MinecraftPlaceholder(player, DCLink.server, currentWorld, null);

            DCLink.bot.sendMessage(entry.message.changeDimension, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
        }
    }

    public static void sendMessage(String content, ServerPlayerEntity player) {
        ServerWorld world = Mappings.getServerWorld(player);
        String worldId = world.getRegistryKey().getValue().toString();
        Placeholder placeholder = new MinecraftPlaceholder(player, player.server, player.world, content);

        for(DiscordEntry entry : DiscordConfig.getInstance().entries) {
            if(!entry.allowedDimension.isEmpty() && !entry.allowedDimension.contains(worldId)) {
                continue;
            }

            if(content.startsWith("/")) {
                DCLink.bot.sendMessage(entry.message.relayCommand, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
            } else {
                DCLink.bot.sendMessage(entry.message.relay, placeholder, entry.channelID, entry.allowMention, entry.enableEmoji);
            }
        }
    }
}
