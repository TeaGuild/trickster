package dev.enjarai.trickster.mixin.brewing;

import dev.enjarai.trickster.item.BottledItem;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandBlockEntityMixin {
    @Inject(
        method = "isValid", at = @At("HEAD"), cancellable = true
    )
    private void makeTricksterItemsValid(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof BottledItem) {
            cir.setReturnValue(true);
        }
    }
}
