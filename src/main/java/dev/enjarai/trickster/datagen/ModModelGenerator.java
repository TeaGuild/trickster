package dev.enjarai.trickster.datagen;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.block.ModBlocks;
import dev.enjarai.trickster.block.SpellResonatorBlock;
import dev.enjarai.trickster.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.List;

import static dev.enjarai.trickster.item.ModItems.*;
import static dev.enjarai.trickster.item.ModItems.GILDED_TUFF_BRICK_SLAB;
import static dev.enjarai.trickster.item.ModItems.GILDED_TUFF_BRICK_WALL;
import static dev.enjarai.trickster.item.ModItems.GILDED_TUFF_SLAB;
import static dev.enjarai.trickster.item.ModItems.GILDED_TUFF_WALL;

public class ModModelGenerator extends FabricModelProvider {

    public static final List<Pair<BlockItem, BlockItem>> STAIRS = List.of(
        new Pair<>(GILDED_TUFF, GILDED_TUFF_STAIRS),
        new Pair<>(GILDED_TUFF_BRICKS, GILDED_TUFF_BRICK_STAIRS)
    );

    public static final List<Pair<BlockItem, BlockItem>> SLABS = List.of(
        new Pair<>(GILDED_TUFF, GILDED_TUFF_SLAB),
        new Pair<>(GILDED_TUFF_BRICKS, GILDED_TUFF_BRICK_SLAB)
    );

    public static final List<Pair<BlockItem, BlockItem>> WALLS = List.of(
        new Pair<>(GILDED_TUFF, GILDED_TUFF_WALL),
        new Pair<>(GILDED_TUFF_BRICKS, GILDED_TUFF_BRICK_WALL)
    );

    public ModModelGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(ModBlocks.SPELL_RESONATOR)
            .coordinate(BlockStateVariantMap.create(SpellResonatorBlock.FACING, SpellResonatorBlock.POWER)
                .register((direction, i) -> BlockStateVariant.create()
                    .put(VariantSettings.MODEL, i != 0 ? Trickster.id("block/spell_resonator_enabled") : Trickster.id("block/spell_resonator"))
                    .put(VariantSettings.Y, switch (direction) {
                        case UP, DOWN, NORTH -> VariantSettings.Rotation.R0;
                        case SOUTH -> VariantSettings.Rotation.R180;
                        case EAST -> VariantSettings.Rotation.R90;
                        case WEST -> VariantSettings.Rotation.R270;
                    })
                    .put(VariantSettings.X, switch (direction) {
                        case UP -> VariantSettings.Rotation.R0;
                        case DOWN -> VariantSettings.Rotation.R180;
                        case NORTH, EAST, WEST, SOUTH -> VariantSettings.Rotation.R90;
                    })
                )
            )
        );
        blockStateModelGenerator.registerNorthDefaultHorizontalRotated(ModBlocks.SCROLL_SHELF, TexturedModel.ORIENTABLE_WITH_BOTTOM);
        for (var block : List.of(ModBlocks.MODULAR_SPELL_CONSTRUCT, ModBlocks.SPELL_CONSTRUCT, ModBlocks.CHARGING_ARRAY, ModBlocks.LIGHT_SCONCE)) {
            blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block)
                .coordinate(BlockStateVariantMap.create(Properties.FACING)
                    .register(direction -> BlockStateVariant.create()
                        .put(VariantSettings.MODEL, Registries.BLOCK.getId(block).withPrefixedPath("block/"))
                        .put(VariantSettings.Y, switch (direction) {
                            case UP, DOWN, NORTH -> VariantSettings.Rotation.R0;
                            case SOUTH -> VariantSettings.Rotation.R180;
                            case EAST -> VariantSettings.Rotation.R90;
                            case WEST -> VariantSettings.Rotation.R270;
                        })
                        .put(VariantSettings.X, switch (direction) {
                            case UP -> VariantSettings.Rotation.R0;
                            case DOWN -> VariantSettings.Rotation.R180;
                            case NORTH, EAST, WEST, SOUTH -> VariantSettings.Rotation.R90;
                        })
                    )
                )
            );
        }

        for (Pair<BlockItem, BlockItem> entry : STAIRS) {
            var block = entry.getRight().getBlock();
            var texture = TextureMap.all(Trickster.id("block/" + Registries.BLOCK.getId(entry.getLeft().getBlock()).getPath()));
            final Identifier stairsModelId = Models.STAIRS.upload(block, texture, blockStateModelGenerator.modelCollector);
            final Identifier innerStairsModelId = Models.INNER_STAIRS.upload(block, texture, blockStateModelGenerator.modelCollector);
            final Identifier outerStairsModelId = Models.OUTER_STAIRS.upload(block, texture, blockStateModelGenerator.modelCollector);
            blockStateModelGenerator.blockStateCollector.accept(
                BlockStateModelGenerator.createStairsBlockState(block,
                    (innerStairsModelId),
                    (stairsModelId),
                    (outerStairsModelId)));
            blockStateModelGenerator.registerParentedItemModel(block, stairsModelId);
        }

        for (Pair<BlockItem, BlockItem> entry : SLABS) {
            var block = entry.getRight().getBlock();
            var id = Trickster.id("block/" + Registries.BLOCK.getId(entry.getLeft().getBlock()).getPath());
            var texture = TextureMap.all(id);
            final Identifier slabBottomModelId = Models.SLAB.upload(block, texture, blockStateModelGenerator.modelCollector);
            final Identifier slabTopModelId = Models.SLAB_TOP.upload(block, texture, blockStateModelGenerator.modelCollector);
            blockStateModelGenerator.blockStateCollector.accept(
                BlockStateModelGenerator.createSlabBlockState(block,
                    (slabBottomModelId),
                    (slabTopModelId),
                    (id)));
            blockStateModelGenerator.registerParentedItemModel(block, slabBottomModelId);
        }

        for (Pair<BlockItem, BlockItem> entry : WALLS) {
            var block = entry.getRight().getBlock();
            var id = Trickster.id("block/" + Registries.BLOCK.getId(entry.getLeft().getBlock()).getPath());
            var texture = TextureMap.all(id);
            final Identifier postModelId = Models.TEMPLATE_WALL_POST.upload(block, texture, blockStateModelGenerator.modelCollector);
            final Identifier shortSideModelId = Models.TEMPLATE_WALL_SIDE.upload(block, texture, blockStateModelGenerator.modelCollector);
            final Identifier tallSideModelId = Models.TEMPLATE_WALL_SIDE_TALL.upload(block, texture, blockStateModelGenerator.modelCollector);
            final Identifier wallModelId = Models.WALL_INVENTORY.upload(block, texture, blockStateModelGenerator.modelCollector);
            blockStateModelGenerator.blockStateCollector.accept(
                BlockStateModelGenerator.createWallBlockState(block,
                    (postModelId),
                    (shortSideModelId),
                    (tallSideModelId)));
            blockStateModelGenerator.registerParentedItemModel(block, wallModelId);
        }

        blockStateModelGenerator.blockStateCollector.accept(
            BlockStateModelGenerator.createSlabBlockState(ModBlocks.GILDED_POLISHED_TUFF_SLAB,
                Trickster.id("block/gilded_polished_tuff_slab"),
                Trickster.id("block/gilded_polished_tuff_slab_top"),
                Trickster.id("block/gilded_polished_tuff_slab_full")));

        blockStateModelGenerator.blockStateCollector.accept(
            BlockStateModelGenerator.createWallBlockState(ModBlocks.GILDED_POLISHED_TUFF_WALL,
                Trickster.id("block/gilded_polished_tuff_wall_post"),
                Trickster.id("block/gilded_polished_tuff_wall_side"),
                Trickster.id("block/gilded_polished_tuff_wall_side_tall")));

        blockStateModelGenerator.registerParentedItemModel(ModBlocks.GILDED_POLISHED_TUFF_STAIRS, Trickster.id("block/gilded_polished_tuff_stairs"));
        blockStateModelGenerator.registerParentedItemModel(ModBlocks.GILDED_POLISHED_TUFF_SLAB, Trickster.id("block/gilded_polished_tuff_slab"));
        blockStateModelGenerator.registerParentedItemModel(ModBlocks.GILDED_POLISHED_TUFF_WALL, Trickster.id("block/gilded_polished_tuff_wall"));

        blockStateModelGenerator.registerSimpleState(ModBlocks.LIGHT);
        blockStateModelGenerator.registerSingleton(ModBlocks.INERT_SPAWNER, TexturedModel.CUBE_ALL_INNER_FACES);
        blockStateModelGenerator.registerSingleton(ModBlocks.GILDED_TUFF, TexturedModel.CUBE_ALL);
        blockStateModelGenerator.registerSingleton(ModBlocks.GILDED_POLISHED_TUFF, TexturedModel.CUBE_ALL);
        blockStateModelGenerator.registerSingleton(ModBlocks.GILDED_TUFF_BRICKS, TexturedModel.CUBE_ALL);
        blockStateModelGenerator.registerSingleton(ModBlocks.GILDED_CHISELED_TUFF, TexturedModel.CUBE_ALL);
        blockStateModelGenerator.registerSingleton(ModBlocks.GILDED_CHISELED_TUFF_BRICKS, TexturedModel.CUBE_ALL);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.WAND, Models.HANDHELD);

        itemModelGenerator.register(ModItems.AMETHYST_KNOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.CRACKED_AMETHYST_KNOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.QUARTZ_KNOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.CRACKED_QUARTZ_KNOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.EMERALD_KNOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.CRACKED_EMERALD_KNOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.DIAMOND_KNOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.CRACKED_DIAMOND_KNOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.ECHO_KNOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.CRACKED_ECHO_KNOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.ASTRAL_KNOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.CRACKED_ASTRAL_KNOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.COMMAND_KNOT, Models.GENERATED);

        itemModelGenerator.register(ModItems.SPELL_CORE, Models.GENERATED);
        itemModelGenerator.register(ModItems.RUSTED_SPELL_CORE, Models.GENERATED);
        itemModelGenerator.register(ModItems.OMINOUS_SPELL_CORE, Models.GENERATED);
        itemModelGenerator.register(ModItems.INACTIVE_SPAWNER_SPELL_CORE, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPAWNER_SPELL_CORE, Models.GENERATED);

        itemModelGenerator.register(ModItems.SCROLL_AND_QUILL, Models.GENERATED);
        itemModelGenerator.register(ModItems.WRITTEN_SCROLL, Models.GENERATED);

        itemModelGenerator.register(ModItems.TOME_OF_TOMFOOLERY, Models.GENERATED);
        itemModelGenerator.register(ModItems.AMETHYST_WHORL, Models.GENERATED);
        itemModelGenerator.register(ModItems.TORMENT_ON_A_CHAIN, Models.GENERATED);
        itemModelGenerator.register(ModItems.COLLAR, Models.GENERATED);
        itemModelGenerator.register(ModItems.LEASH, Models.GENERATED);
        itemModelGenerator.register(ModItems.MACRO_RING, Models.GENERATED);
        itemModelGenerator.register(ModItems.ESOTERIC_MANA, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPELL_RESONATOR_ITEM, Models.GENERATED);

        // For the NaN advancement
        itemModelGenerator.register(ModItems.NAN, Models.GENERATED);
        itemModelGenerator.register(ModItems.NOTHING, Models.GENERATED);

        ModItems.DYED_VARIANTS.forEach(v -> {
            itemModelGenerator.register(v.variant(), Models.GENERATED);
        });
    }
}
