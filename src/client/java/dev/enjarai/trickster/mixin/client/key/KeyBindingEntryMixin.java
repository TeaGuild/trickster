package dev.enjarai.trickster.mixin.client.key;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.enjarai.trickster.pond.ContextualKeyBinding;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ControlsListWidget.KeyBindingEntry.class)
public abstract class KeyBindingEntryMixin {
    @Shadow
    @Final
    private KeyBinding binding;

    @ModifyExpressionValue(
        method = "update", at = @At(
            value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;equals(Lnet/minecraft/client/option/KeyBinding;)Z"
        )
    )
    private boolean ignoreCertainKeyBindingConflicts(boolean original, @Local KeyBinding otherBinding) {
        var firstContexts = ((ContextualKeyBinding) binding).trickster$getContexts();
        var secondContexts = ((ContextualKeyBinding) otherBinding).trickster$getContexts();

        // none + none -> original
        // none + has -> false
        // has + none -> false
        // has + has ->
        //   same context -> original
        //   different context -> false

        if (firstContexts.isEmpty() && secondContexts.isEmpty()) return original;
        if (firstContexts.isEmpty() || secondContexts.isEmpty()) return false;
        if (firstContexts.stream().anyMatch(secondContexts::contains)) return original;
        return false;
    }
}
