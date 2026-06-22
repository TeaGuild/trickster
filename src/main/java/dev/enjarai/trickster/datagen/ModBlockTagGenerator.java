package dev.enjarai.trickster.datagen;

import dev.enjarai.trickster.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Pair;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        for (Pair<Item, BlockItem> entry : ModRecipeGenerator.TUFF_TO_GILDED) {
            getOrCreateTagBuilder(ModBlocks.GILDED_TUFF_TAG)
                .add(entry.getRight().getBlock());
        }

        getOrCreateTagBuilder(BlockTags.WOOL).add(ModBlocks.TATTERED_WOOL);
        getOrCreateTagBuilder(BlockTags.WOOL_CARPETS).add(ModBlocks.TATTERED_CARPET);
    }
}
