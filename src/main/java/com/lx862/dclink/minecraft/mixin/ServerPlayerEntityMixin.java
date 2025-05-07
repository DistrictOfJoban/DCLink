package com.lx862.dclink.minecraft.mixin;

import com.lx862.dclink.minecraft.events.PlayerManager;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Shadow public abstract ServerWorld getServerWorld();

    @Inject(method = "worldChanged", at = @At("TAIL"))
    public void worldChanged(ServerWorld origin, CallbackInfo ci) {
        PlayerManager.worldChanged(origin, getServerWorld(), ((ServerPlayerEntity)(Object)this));
    }

    @Inject(method = "onDeath", at = @At("TAIL"))
    public void onDeath(DamageSource source, CallbackInfo ci) {
        PlayerManager.playerDied(((ServerPlayerEntity)(Object)this), source, getServerWorld());
    }
}