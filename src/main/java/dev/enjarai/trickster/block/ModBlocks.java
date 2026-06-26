package dev.enjarai.trickster.block;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.block.cauldron.EraseSpellCauldronBehavior;
import dev.enjarai.trickster.item.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class ModBlocks {
    public static final SpellResonatorBlock SPELL_RESONATOR = register("spell_resonator", new SpellResonatorBlock());
    public static final SpellConstructBlock SPELL_CONSTRUCT = register("spell_construct", new SpellConstructBlock());
    public static final LightBlock LIGHT = register("light", new LightBlock());
    public static final LightSconceBlock LIGHT_SCONCE = register("light_sconce", new LightSconceBlock());
    public static final ColorBlock COLOR_BLOCK = register("color_block", new ColorBlock());
    public static final ModularSpellConstructBlock MODULAR_SPELL_CONSTRUCT = register("modular_spell_construct", new ModularSpellConstructBlock());
    public static final ScrollShelfBlock SCROLL_SHELF = register("scroll_shelf", new ScrollShelfBlock());
    public static final ChargingArrayBlock CHARGING_ARRAY = register("charging_array", new ChargingArrayBlock());
    public static final Block INERT_SPAWNER = register("inert_spawner", new Block(AbstractBlock.Settings.copy(Blocks.SPAWNER)));
    public static final Block TATTERED_WOOL = register("tattered_wool", new Block(AbstractBlock.Settings.copy(Blocks.WHITE_WOOL)));
    public static final CarpetBlock TATTERED_CARPET = register("tattered_carpet", new CarpetBlock(AbstractBlock.Settings.copy(Blocks.WHITE_CARPET)));
    public static final Block WORN_CONCRETE = register("worn_concrete", new Block(AbstractBlock.Settings.copy(Blocks.WHITE_CONCRETE)));

    public static final Block GILDED_TUFF = register("gilded_tuff", new Block(AbstractBlock.Settings.copy(Blocks.TUFF)));
    public static final StairsBlock GILDED_TUFF_STAIRS = register("gilded_tuff_stairs", createStairsBlock(GILDED_TUFF));
    public static final SlabBlock GILDED_TUFF_SLAB = register("gilded_tuff_slab", createSlabBlock(GILDED_TUFF));
    public static final WallBlock GILDED_TUFF_WALL = register("gilded_tuff_wall", createWallBlock(GILDED_TUFF));
    public static final Block GILDED_POLISHED_TUFF = register("gilded_polished_tuff", new Block(AbstractBlock.Settings.copy(Blocks.POLISHED_TUFF)));
    public static final StairsBlock GILDED_POLISHED_TUFF_STAIRS = register("gilded_polished_tuff_stairs", createStairsBlock(GILDED_POLISHED_TUFF));
    public static final SlabBlock GILDED_POLISHED_TUFF_SLAB = register("gilded_polished_tuff_slab", createSlabBlock(GILDED_POLISHED_TUFF));
    public static final WallBlock GILDED_POLISHED_TUFF_WALL = register("gilded_polished_tuff_wall", createWallBlock(GILDED_POLISHED_TUFF));
    public static final Block GILDED_TUFF_BRICKS = register("gilded_tuff_bricks", new Block(AbstractBlock.Settings.copy(Blocks.TUFF_BRICKS)));
    public static final StairsBlock GILDED_TUFF_BRICK_STAIRS = register("gilded_tuff_brick_stairs", createStairsBlock(GILDED_TUFF_BRICKS));
    public static final SlabBlock GILDED_TUFF_BRICK_SLAB = register("gilded_tuff_brick_slab", createSlabBlock(GILDED_TUFF_BRICKS));
    public static final WallBlock GILDED_TUFF_BRICK_WALL = register("gilded_tuff_brick_wall", createWallBlock(GILDED_TUFF_BRICKS));
    public static final Block GILDED_CHISELED_TUFF = register("gilded_chiseled_tuff", new Block(AbstractBlock.Settings.copy(Blocks.CHISELED_TUFF)));
    public static final Block GILDED_CHISELED_TUFF_BRICKS = register("gilded_chiseled_tuff_bricks", new Block(AbstractBlock.Settings.copy(Blocks.CHISELED_TUFF_BRICKS)));

    public static final BlockEntityType<SpellConstructBlockEntity> SPELL_CONSTRUCT_ENTITY = register("spell_construct",
        BlockEntityType.Builder.create(SpellConstructBlockEntity::new, SPELL_CONSTRUCT).build(null));
    public static final BlockEntityType<LightBlockEntity> LIGHT_ENTITY = register("light", BlockEntityType.Builder.create(LightBlockEntity::new, LIGHT).build(null));
    public static final BlockEntityType<ColorBlockEntity> COLOR_BLOCK_ENTITY = register("color_block", BlockEntityType.Builder.create(ColorBlockEntity::new, COLOR_BLOCK).build(null));
    public static final BlockEntityType<ModularSpellConstructBlockEntity> MODULAR_SPELL_CONSTRUCT_ENTITY = register("modular_spell_construct",
        BlockEntityType.Builder.create(ModularSpellConstructBlockEntity::new, MODULAR_SPELL_CONSTRUCT).build(null));
    public static final BlockEntityType<ScrollShelfBlockEntity> SCROLL_SHELF_ENTITY = register("scroll_shelf", BlockEntityType.Builder.create(ScrollShelfBlockEntity::new, SCROLL_SHELF).build(null));
    public static final BlockEntityType<ChargingArrayBlockEntity> CHARGING_ARRAY_ENTITY = register("charging_array",
        BlockEntityType.Builder.create(ChargingArrayBlockEntity::new, CHARGING_ARRAY).build(null));

    public static final TagKey<Block> CANNOT_BREAK = TagKey.of(RegistryKeys.BLOCK, Trickster.id("cannot_break"));
    public static final TagKey<Block> CANNOT_SWAP = TagKey.of(RegistryKeys.BLOCK, Trickster.id("cannot_swap"));
    public static final TagKey<Block> CANNOT_LEVITATE = TagKey.of(RegistryKeys.BLOCK, Trickster.id("cannot_levitate"));
    public static final TagKey<Block> CONJURABLE_FLOWERS = TagKey.of(RegistryKeys.BLOCK, Trickster.id("conjurable_flowers"));
    public static final TagKey<Block> LIGHT_HOLDERS = TagKey.of(RegistryKeys.BLOCK, Trickster.id("light_holders"));
    public static final TagKey<Block> GILDED_TUFF_TAG = TagKey.of(RegistryKeys.BLOCK, Trickster.id("gilded_tuff"));
    public static final TagKey<Block> EXPOSED_KNOT_HOLDERS = TagKey.of(RegistryKeys.BLOCK, Trickster.id("exposed_knot_holders"));

    private static <T extends Block> T register(String name, T block) {
        return Registry.register(Registries.BLOCK, Trickster.id(name), block);
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> blockEntityType) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Trickster.id(name), blockEntityType);
    }

    public static StairsBlock createStairsBlock(Block base) {
        return new StairsBlock(base.getDefaultState(), AbstractBlock.Settings.copy(base));
    }

    public static SlabBlock createSlabBlock(Block base) {
        return new SlabBlock(AbstractBlock.Settings.copy(base));
    }

    public static WallBlock createWallBlock(Block base) {
        return new WallBlock(AbstractBlock.Settings.copy(base));
    }

    public static void register() {
        var cauldronMap = CauldronBehavior.WATER_CAULDRON_BEHAVIOR.map();
        cauldronMap.put(ModItems.WRITTEN_SCROLL, new EraseSpellCauldronBehavior());

        for (var scroll : ModItems.COLORED_WRITTEN_SCROLLS) {
            cauldronMap.put(scroll, new EraseSpellCauldronBehavior());
        }

        for (var scroll : ModItems.COLORED_SCROLLS_AND_QUILLS) {
            cauldronMap.put(scroll, new EraseSpellCauldronBehavior());
        }
    }
}
