package dev.enjarai.trickster.mixin.brewing;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.item.BottledItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/screen/BrewingStandScreenHandler$PotionSlot")
public class BrewingStandScreenHandler$PotionSlotMixin {
    @WrapMethod(
        method = "matches"
    )
    private static boolean makeTricksterItemsValid(ItemStack stack, Operation<Boolean> original) {
        if (stack.getItem() instanceof BottledItem) {
            return true;
        } else {
            return original.call(stack);
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
