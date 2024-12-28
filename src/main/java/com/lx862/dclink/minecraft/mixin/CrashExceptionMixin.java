package com.lx862.dclink.minecraft.mixin;

import com.lx862.dclink.DCLink;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrashException.class)
public abstract class CrashExceptionMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onCrashCreate(CrashReport report, CallbackInfo ci) {
        DCLink.getMaster().serverCrashed(report);
    }
}