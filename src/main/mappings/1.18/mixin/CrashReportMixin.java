package com.lx.dclink.mixin;

import com.lx.dclink.events.ServerEvent;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(CrashReport.class)
public class CrashReportMixin {
    @Inject(method = "writeToFile", at = @At("HEAD"))
    public void writeToFile(File file, CallbackInfoReturnable<Boolean> cir) {
        ServerEvent.serverCrashed((CrashReport)(Object)this);
    }
}
