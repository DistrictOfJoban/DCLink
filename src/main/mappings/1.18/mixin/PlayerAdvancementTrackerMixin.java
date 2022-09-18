package com.lx.dclink.mixin;

import com.lx.dclink.Events.PlayerEvent;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancementTracker.class)
public abstract class PlayerAdvancementTrackerMixin {

    @Shadow public abstract AdvancementProgress getProgress(Advancement advancement);

    @Shadow private ServerPlayerEntity owner;

    @Inject(method = "grantCriterion", at = @At("RETURN"))
    public void grantCriterionReturn(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if(cir.getReturnValue()) {
            PlayerEvent.playerAdvancementGranted(this.owner, getProgress(advancement), this.owner.world, advancement);
        }
    }
}