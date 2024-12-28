package com.lx862.dclink.minecraft.mixin;

import com.lx862.dclink.DCLink;
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
        DCLink.getMaster().serverCrashed((CrashReport)(Object)this);
    }
}
