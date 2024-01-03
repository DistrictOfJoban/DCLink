package com.lx862.dclink.minecraft.mixin;

import com.lx862.dclink.minecraft.events.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow public ServerPlayerEntity player;

    @Inject(method = "onDisconnected", at = @At("HEAD"))
    public void onDisconnected(Text reason, CallbackInfo ci) {
        PlayerManager.playerLeft(reason, player);
    }

    // FIXME: this prob doesn't work idk
    @Inject(method = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;decorateCommand(Ljava/lang/String;)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"))
    public void sendCommand(String query, CallbackInfoReturnable<CompletableFuture<Text>> cir) {
        PlayerManager.sendCommand(query, player);
    }
}