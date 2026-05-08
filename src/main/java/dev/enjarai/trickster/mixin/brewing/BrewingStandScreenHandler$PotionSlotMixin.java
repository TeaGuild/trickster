package dev.enjarai.trickster.mixin.brewing;

import dev.enjarai.trickster.item.BottledItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/screen/BrewingStandScreenHandler$PotionSlot")
public class BrewingStandScreenHandler$PotionSlotMixin {
    @Inject(
        method = "matches", at = @At("HEAD"), cancellable = true
    )
    private static void makeTricksterItemsValid(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof BottledItem) {
            cir.setReturnValue(true);
        }
    }
}
