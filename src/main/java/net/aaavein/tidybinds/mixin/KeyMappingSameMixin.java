package net.aaavein.tidybinds.mixin;

import net.aaavein.tidybinds.config.TidyBindsConfig;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyMapping.class)
public abstract class KeyMappingSameMixin {

    @Inject(method = "same", at = @At("HEAD"), cancellable = true)
    private void tidybinds$disableConflictCheck(KeyMapping other, CallbackInfoReturnable<Boolean> cir) {
        if (TidyBindsConfig.DISABLE_CONFLICTS.get()) {
            cir.setReturnValue(false);
        }
    }
}