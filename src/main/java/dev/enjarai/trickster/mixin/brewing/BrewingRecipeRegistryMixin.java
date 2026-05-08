package dev.enjarai.trickster.mixin.brewing;

import dev.enjarai.trickster.item.BottledItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BrewingRecipeRegistry.class)
public class BrewingRecipeRegistryMixin {
    @Shadow
    @Final
    private List<BrewingRecipeRegistry.Recipe<Item>> itemRecipes;

    @Inject(
        method = "craft", at = @At("HEAD"), cancellable = true
    )
    private void craftTricksterBottle(ItemStack ingredient, ItemStack input, CallbackInfoReturnable<ItemStack> cir) {
        for (BrewingRecipeRegistry.Recipe<Item> recipe : this.itemRecipes) {
            if (recipe.to().value() instanceof BottledItem && input.itemMatches(recipe.from()) && recipe.ingredient().test(ingredient)) {
                cir.setReturnValue(recipe.to().value().getDefaultStack());
                return;
            }
        }
    }
}
