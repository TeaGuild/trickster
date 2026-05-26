package dev.enjarai.trickster.mixin.brewing;

import dev.enjarai.trickster.item.BottledItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingRecipeRegistry.Builder.class)
public class BrewingRecipeRegistry$BuilderMixin {
    @Inject(
        method = "assertPotion", at = @At("HEAD"), cancellable = true
    )
    private static void allowTricksterItems(Item potionType, CallbackInfo ci) {
        if (potionType instanceof BottledItem || potionType == Items.GLASS_BOTTLE) {
            ci.cancel();
        }
    }
}
