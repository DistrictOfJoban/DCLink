package com.lx.dclink.mixin;

import com.lx.dclink.events.PlayerEvent;
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

    @Shadow public abstract ServerWorld getWorld();

    @Inject(method = "worldChanged", at = @At("TAIL"))
    public void worldChanged(ServerWorld origin, CallbackInfo ci) {
        PlayerEvent.worldChanged(origin, getWorld(), ((ServerPlayerEntity)(Object)this));
    }

    @Inject(method = "onDeath", at = @At("TAIL"))
    public void onDeath(DamageSource source, CallbackInfo ci) {
        PlayerEvent.playerDied(((ServerPlayerEntity)(Object)this), source, getWorld());
    }
}