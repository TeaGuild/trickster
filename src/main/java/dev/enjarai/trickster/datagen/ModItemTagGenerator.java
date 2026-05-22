package dev.enjarai.trickster.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

import dev.enjarai.trickster.item.ModItems;

public class ModItemTagGenerator extends FabricTagProvider.ItemTagProvider {
    public ModItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        //        for (ModItems.DyedVariant variant : ModItems.DYED_VARIANTS) {
        //            getOrCreateTagBuilder(TagKey.of(registryRef, reverseLookup(variant.original()).getValue().withPrefixedPath("dyed_")))
        //                    .add(variant.variant());
        //        }

        getOrCreateTagBuilder(ConventionalItemTags.DRINK_CONTAINING_BOTTLE)
            .add(ModItems.LUMINOUS_BOTTLE, ModItems.ESOTERIC_MANA);
        getOrCreateTagBuilder(ConventionalItemTags.MAGIC_DRINKS)
            .add(ModItems.LUMINOUS_BOTTLE, ModItems.ESOTERIC_MANA);
    }
}
