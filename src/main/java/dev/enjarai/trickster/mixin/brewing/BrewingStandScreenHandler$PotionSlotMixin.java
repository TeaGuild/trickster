package dev.enjarai.trickster.mixin.brewing;

import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.item.BottledItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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

    @Inject(
        method = "onTakeItem", at = @At(value = "HEAD")
    )
    private static void brewManaAdvancement(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            ModCriteria.BREW_ITEM.trigger(serverPlayer, stack.getItem());
        }
    }
}
