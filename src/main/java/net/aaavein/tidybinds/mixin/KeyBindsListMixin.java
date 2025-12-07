package net.aaavein.tidybinds.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.aaavein.tidybinds.config.ClientConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Mixin(KeyBindsList.class)
public class KeyBindsListMixin {

    @WrapOperation(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lorg/apache/commons/lang3/ArrayUtils;clone([Ljava/lang/Object;)[Ljava/lang/Object;")
    )
    private Object[] filterKeysToHide(Object[] array, Operation<Object[]> original) {
        // if debug mode is enabled, run the original method and return early
        if (ClientConfig.SHOW_HIDDEN_KEYS.get()) {
            return original.call((Object) array);
        }

        // prepare the list of keys to hide
        Set<String> keysToHide = new HashSet<>(ClientConfig.HIDE_KEYS.get());

        // add linked keys from combinations to the hide list
        for (String combo : ClientConfig.KEY_COMBINATIONS.get()) {
            String[] parts = combo.split(";");
            if (parts.length == 2) {
                Collections.addAll(keysToHide, parts[1].split(","));
            }
        }

        // filter the array manually
        // we do not call original.call() here because we are replacing the logic completely
        return Arrays.stream(array)
                .map(obj -> (KeyMapping) obj)
                .filter(mapping -> !keysToHide.contains(mapping.getName()))
                .toArray(KeyMapping[]::new);
    }
}