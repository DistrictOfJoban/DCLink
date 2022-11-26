package com.lx.dclink.mixin;

import com.lx.dclink.events.PlayerEvent;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.server.network.ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {

    @Shadow public ServerPlayerEntity player;

    @Inject(method = "onChatMessage", at = @At("TAIL"))
    public void sendMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        String message = StringUtils.normalizeSpace(packet.chatMessage());
        PlayerEvent.sendMessage(message, player);
    }
}