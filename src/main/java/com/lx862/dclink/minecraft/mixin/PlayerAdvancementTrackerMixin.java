package com.lx862.dclink.minecraft.mixin;

import com.lx862.dclink.minecraft.events.PlayerManager;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancementTracker.class)
public abstract class PlayerAdvancementTrackerMixin {

    @Shadow public abstract AdvancementProgress getProgress(AdvancementEntry advancement);

    @Shadow @Final private ServerPlayerEntity owner;

    @Inject(method = "grantCriterion", at = @At("RETURN"))
    public void grantCriterionReturn(AdvancementEntry advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if(cir.getReturnValue()) {
            PlayerManager.playerAdvancementGranted(this.owner, getProgress(advancement), this.owner.getWorld(), advancement.value());
        }
    }
}