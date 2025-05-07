package com.lx862.dclink.minecraft.mixin;

import com.lx862.dclink.minecraft.events.PlayerManager;

import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow public ServerPlayerEntity player;

    @Inject(method = "onDisconnected", at = @At("HEAD"))
    public void onDisconnected(DisconnectionInfo info, CallbackInfo ci) {
        PlayerManager.playerLeft(info.reason(), player);
    }

    @Inject(method = "onCommandExecution", at = @At("HEAD"))
    public void sendCommand(CommandExecutionC2SPacket packet, CallbackInfo ci) {
        PlayerManager.sendCommand(packet.command(), player);
    }
}