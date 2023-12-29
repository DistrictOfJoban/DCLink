package com.lx862.dclink.mixin;

import com.lx862.dclink.events.PlayerEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "onPlayerConnect", at=@At("TAIL"))
    private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        PlayerEvent.playerJoin(connection, player);
    }

    @Inject(method = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/network/message/SignedMessage;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/message/MessageType$Parameters;)V", at = @At("TAIL"))
    public void broadcast(SignedMessage message, ServerPlayerEntity sender, MessageType.Parameters params, CallbackInfo ci) {
        PlayerEvent.sendMessage(StringUtils.normalizeSpace(message.getContent().getString()), sender);
    }
}
