package dev.enjarai.trickster.datagen;

import dev.enjarai.trickster.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.SlabBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Pair;

import java.util.concurrent.CompletableFuture;

public class ModLootTableGenerator extends FabricBlockLootTableProvider {
    protected ModLootTableGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        for (Pair<Item, BlockItem> entry : ModRecipeGenerator.TUFF_TO_GILDED) {
            var block = entry.getRight().getBlock();
            if (block instanceof SlabBlock) {
                addDrop(block, slabDrops(block));
            } else {
                addDrop(block);
            }
        }

        addDrop(ModBlocks.LIGHT_SCONCE);
    }
}
