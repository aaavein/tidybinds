package net.aaavein.tidybinds.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.aaavein.tidybinds.config.TidyBindsConfig;
import net.aaavein.tidybinds.core.KeyBindManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Arrays;
import java.util.Set;

@Mixin(KeyBindsList.class)
public abstract class KeyBindsListMixin {

    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/apache/commons/lang3/ArrayUtils;clone([Ljava/lang/Object;)[Ljava/lang/Object;"
            )
    )
    private Object[] tidybinds$filterHiddenKeys(Object[] array, Operation<Object[]> original) {
        if (TidyBindsConfig.DISPLAY_HIDDEN_KEYS.get()) {
            return original.call((Object) array);
        }

        Set<String> hiddenKeys = KeyBindManager.getHiddenKeyNames();
        if (hiddenKeys.isEmpty()) {
            return original.call((Object) array);
        }

        return Arrays.stream(array)
                .map(obj -> (KeyMapping) obj)
                .filter(mapping -> !hiddenKeys.contains(mapping.getName()))
                .toArray(KeyMapping[]::new);
    }

    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/ContainerObjectSelectionList;<init>(Lnet/minecraft/client/Minecraft;IIII)V"
            ),
            index = 4
    )
    private static int tidybinds$addEntrySpacing(int originalHeight) {
        return originalHeight + TidyBindsConfig.ENTRY_SPACING.get();
    }
}