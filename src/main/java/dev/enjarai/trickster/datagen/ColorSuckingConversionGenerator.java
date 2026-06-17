package dev.enjarai.trickster.datagen;

import dev.enjarai.trickster.block.ModBlocks;
import dev.enjarai.trickster.data.conversion.ColorSuckingConversionLoader;
import dev.enjarai.trickster.datagen.provider.BlockConversionProvider;
import dev.enjarai.trickster.datagen.provider.ColorSuckingConversionBuilder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.DyeColor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ColorSuckingConversionGenerator extends BlockConversionProvider<ColorSuckingConversionLoader.ColoredValue, ColorSuckingConversionBuilder> {
    public ColorSuckingConversionGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        super(output, "color_sucking", ColorSuckingConversionLoader.ColoredValue.CODEC, ColorSuckingConversionBuilder::create, registryLookupFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        registerSuckingConversion(256, ModBlocks.TATTERED_WOOL, new HashMap<>() {
            {
                put(Blocks.BLACK_WOOL, DyeColor.BLACK);
                put(Blocks.WHITE_WOOL, DyeColor.WHITE);
                put(Blocks.ORANGE_WOOL, DyeColor.ORANGE);
                put(Blocks.MAGENTA_WOOL, DyeColor.MAGENTA);
                put(Blocks.LIGHT_BLUE_WOOL, DyeColor.LIGHT_BLUE);
                put(Blocks.YELLOW_WOOL, DyeColor.YELLOW);
                put(Blocks.LIME_WOOL, DyeColor.LIME);
                put(Blocks.PINK_WOOL, DyeColor.PINK);
                put(Blocks.GRAY_WOOL, DyeColor.GRAY);
                put(Blocks.LIGHT_GRAY_WOOL, DyeColor.LIGHT_GRAY);
                put(Blocks.CYAN_WOOL, DyeColor.CYAN);
                put(Blocks.PURPLE_WOOL, DyeColor.PURPLE);
                put(Blocks.BLUE_WOOL, DyeColor.BLUE);
                put(Blocks.BROWN_WOOL, DyeColor.BROWN);
                put(Blocks.GREEN_WOOL, DyeColor.GREEN);
                put(Blocks.RED_WOOL, DyeColor.RED);
            }
        });

        registerSuckingConversion(64, Blocks.TERRACOTTA, new HashMap<>() {
            {
                put(Blocks.BLACK_TERRACOTTA, DyeColor.BLACK);
                put(Blocks.WHITE_TERRACOTTA, DyeColor.WHITE);
                put(Blocks.ORANGE_TERRACOTTA, DyeColor.ORANGE);
                put(Blocks.MAGENTA_TERRACOTTA, DyeColor.MAGENTA);
                put(Blocks.LIGHT_BLUE_TERRACOTTA, DyeColor.LIGHT_BLUE);
                put(Blocks.YELLOW_TERRACOTTA, DyeColor.YELLOW);
                put(Blocks.LIME_TERRACOTTA, DyeColor.LIME);
                put(Blocks.PINK_TERRACOTTA, DyeColor.PINK);
                put(Blocks.GRAY_TERRACOTTA, DyeColor.GRAY);
                put(Blocks.LIGHT_GRAY_TERRACOTTA, DyeColor.LIGHT_GRAY);
                put(Blocks.CYAN_TERRACOTTA, DyeColor.CYAN);
                put(Blocks.PURPLE_TERRACOTTA, DyeColor.PURPLE);
                put(Blocks.BLUE_TERRACOTTA, DyeColor.BLUE);
                put(Blocks.BROWN_TERRACOTTA, DyeColor.BROWN);
                put(Blocks.GREEN_TERRACOTTA, DyeColor.GREEN);
                put(Blocks.RED_TERRACOTTA, DyeColor.RED);
            }
        });

        registerSuckingConversion(64, Blocks.GLASS, new HashMap<>() {
            {
                put(Blocks.BLACK_STAINED_GLASS, DyeColor.BLACK);
                put(Blocks.WHITE_STAINED_GLASS, DyeColor.WHITE);
                put(Blocks.ORANGE_STAINED_GLASS, DyeColor.ORANGE);
                put(Blocks.MAGENTA_STAINED_GLASS, DyeColor.MAGENTA);
                put(Blocks.LIGHT_BLUE_STAINED_GLASS, DyeColor.LIGHT_BLUE);
                put(Blocks.YELLOW_STAINED_GLASS, DyeColor.YELLOW);
                put(Blocks.LIME_STAINED_GLASS, DyeColor.LIME);
                put(Blocks.PINK_STAINED_GLASS, DyeColor.PINK);
                put(Blocks.GRAY_STAINED_GLASS, DyeColor.GRAY);
                put(Blocks.LIGHT_GRAY_STAINED_GLASS, DyeColor.LIGHT_GRAY);
                put(Blocks.CYAN_STAINED_GLASS, DyeColor.CYAN);
                put(Blocks.PURPLE_STAINED_GLASS, DyeColor.PURPLE);
                put(Blocks.BLUE_STAINED_GLASS, DyeColor.BLUE);
                put(Blocks.BROWN_STAINED_GLASS, DyeColor.BROWN);
                put(Blocks.GREEN_STAINED_GLASS, DyeColor.GREEN);
                put(Blocks.RED_STAINED_GLASS, DyeColor.RED);
            }
        });
        registerSuckingConversion(32, Blocks.GLASS_PANE, new HashMap<>() {
            {
                put(Blocks.BLACK_STAINED_GLASS_PANE, DyeColor.BLACK);
                put(Blocks.WHITE_STAINED_GLASS_PANE, DyeColor.WHITE);
                put(Blocks.ORANGE_STAINED_GLASS_PANE, DyeColor.ORANGE);
                put(Blocks.MAGENTA_STAINED_GLASS_PANE, DyeColor.MAGENTA);
                put(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, DyeColor.LIGHT_BLUE);
                put(Blocks.YELLOW_STAINED_GLASS_PANE, DyeColor.YELLOW);
                put(Blocks.LIME_STAINED_GLASS_PANE, DyeColor.LIME);
                put(Blocks.PINK_STAINED_GLASS_PANE, DyeColor.PINK);
                put(Blocks.GRAY_STAINED_GLASS_PANE, DyeColor.GRAY);
                put(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, DyeColor.LIGHT_GRAY);
                put(Blocks.CYAN_STAINED_GLASS_PANE, DyeColor.CYAN);
                put(Blocks.PURPLE_STAINED_GLASS_PANE, DyeColor.PURPLE);
                put(Blocks.BLUE_STAINED_GLASS_PANE, DyeColor.BLUE);
                put(Blocks.BROWN_STAINED_GLASS_PANE, DyeColor.BROWN);
                put(Blocks.GREEN_STAINED_GLASS_PANE, DyeColor.GREEN);
                put(Blocks.RED_STAINED_GLASS_PANE, DyeColor.RED);
            }
        });

        registerSuckingConversion(512, Blocks.SHULKER_BOX, new HashMap<>() {
            {
                put(Blocks.BLACK_SHULKER_BOX, DyeColor.BLACK);
                put(Blocks.WHITE_SHULKER_BOX, DyeColor.WHITE);
                put(Blocks.ORANGE_SHULKER_BOX, DyeColor.ORANGE);
                put(Blocks.MAGENTA_SHULKER_BOX, DyeColor.MAGENTA);
                put(Blocks.LIGHT_BLUE_SHULKER_BOX, DyeColor.LIGHT_BLUE);
                put(Blocks.YELLOW_SHULKER_BOX, DyeColor.YELLOW);
                put(Blocks.LIME_SHULKER_BOX, DyeColor.LIME);
                put(Blocks.PINK_SHULKER_BOX, DyeColor.PINK);
                put(Blocks.GRAY_SHULKER_BOX, DyeColor.GRAY);
                put(Blocks.LIGHT_GRAY_SHULKER_BOX, DyeColor.LIGHT_GRAY);
                put(Blocks.CYAN_SHULKER_BOX, DyeColor.CYAN);
                put(Blocks.PURPLE_SHULKER_BOX, DyeColor.PURPLE);
                put(Blocks.BLUE_SHULKER_BOX, DyeColor.BLUE);
                put(Blocks.BROWN_SHULKER_BOX, DyeColor.BROWN);
                put(Blocks.GREEN_SHULKER_BOX, DyeColor.GREEN);
                put(Blocks.RED_SHULKER_BOX, DyeColor.RED);
            }
        });

        registerSuckingConversion(32, Blocks.CANDLE, new HashMap<>() {
            {
                put(Blocks.BLACK_CANDLE, DyeColor.BLACK);
                put(Blocks.WHITE_CANDLE, DyeColor.WHITE);
                put(Blocks.ORANGE_CANDLE, DyeColor.ORANGE);
                put(Blocks.MAGENTA_CANDLE, DyeColor.MAGENTA);
                put(Blocks.LIGHT_BLUE_CANDLE, DyeColor.LIGHT_BLUE);
                put(Blocks.YELLOW_CANDLE, DyeColor.YELLOW);
                put(Blocks.LIME_CANDLE, DyeColor.LIME);
                put(Blocks.PINK_CANDLE, DyeColor.PINK);
                put(Blocks.GRAY_CANDLE, DyeColor.GRAY);
                put(Blocks.LIGHT_GRAY_CANDLE, DyeColor.LIGHT_GRAY);
                put(Blocks.CYAN_CANDLE, DyeColor.CYAN);
                put(Blocks.PURPLE_CANDLE, DyeColor.PURPLE);
                put(Blocks.BLUE_CANDLE, DyeColor.BLUE);
                put(Blocks.BROWN_CANDLE, DyeColor.BROWN);
                put(Blocks.GREEN_CANDLE, DyeColor.GREEN);
                put(Blocks.RED_CANDLE, DyeColor.RED);
            }
        });
        registerSuckingConversion(32, Blocks.CANDLE_CAKE, new HashMap<>() {
            {
                put(Blocks.BLACK_CANDLE_CAKE, DyeColor.BLACK);
                put(Blocks.WHITE_CANDLE_CAKE, DyeColor.WHITE);
                put(Blocks.ORANGE_CANDLE_CAKE, DyeColor.ORANGE);
                put(Blocks.MAGENTA_CANDLE_CAKE, DyeColor.MAGENTA);
                put(Blocks.LIGHT_BLUE_CANDLE_CAKE, DyeColor.LIGHT_BLUE);
                put(Blocks.YELLOW_CANDLE_CAKE, DyeColor.YELLOW);
                put(Blocks.LIME_CANDLE_CAKE, DyeColor.LIME);
                put(Blocks.PINK_CANDLE_CAKE, DyeColor.PINK);
                put(Blocks.GRAY_CANDLE_CAKE, DyeColor.GRAY);
                put(Blocks.LIGHT_GRAY_CANDLE_CAKE, DyeColor.LIGHT_GRAY);
                put(Blocks.CYAN_CANDLE_CAKE, DyeColor.CYAN);
                put(Blocks.PURPLE_CANDLE_CAKE, DyeColor.PURPLE);
                put(Blocks.BLUE_CANDLE_CAKE, DyeColor.BLUE);
                put(Blocks.BROWN_CANDLE_CAKE, DyeColor.BROWN);
                put(Blocks.GREEN_CANDLE_CAKE, DyeColor.GREEN);
                put(Blocks.RED_CANDLE_CAKE, DyeColor.RED);
            }
        });

        registerSuckingConversion(64, Blocks.SAND, new HashMap<>() {
            {
                put(Blocks.BLACK_CONCRETE_POWDER, DyeColor.BLACK);
                put(Blocks.WHITE_CONCRETE_POWDER, DyeColor.WHITE);
                put(Blocks.ORANGE_CONCRETE_POWDER, DyeColor.ORANGE);
                put(Blocks.MAGENTA_CONCRETE_POWDER, DyeColor.MAGENTA);
                put(Blocks.LIGHT_BLUE_CONCRETE_POWDER, DyeColor.LIGHT_BLUE);
                put(Blocks.YELLOW_CONCRETE_POWDER, DyeColor.YELLOW);
                put(Blocks.LIME_CONCRETE_POWDER, DyeColor.LIME);
                put(Blocks.PINK_CONCRETE_POWDER, DyeColor.PINK);
                put(Blocks.GRAY_CONCRETE_POWDER, DyeColor.GRAY);
                put(Blocks.LIGHT_GRAY_CONCRETE_POWDER, DyeColor.LIGHT_GRAY);
                put(Blocks.CYAN_CONCRETE_POWDER, DyeColor.CYAN);
                put(Blocks.PURPLE_CONCRETE_POWDER, DyeColor.PURPLE);
                put(Blocks.BLUE_CONCRETE_POWDER, DyeColor.BLUE);
                put(Blocks.BROWN_CONCRETE_POWDER, DyeColor.BROWN);
                put(Blocks.GREEN_CONCRETE_POWDER, DyeColor.GREEN);
                put(Blocks.RED_CONCRETE_POWDER, DyeColor.RED);
            }
        });
        registerSuckingConversion(64, Blocks.GRAVEL, new HashMap<>() {
            {
                put(Blocks.BLACK_CONCRETE_POWDER, DyeColor.BLACK);
                put(Blocks.WHITE_CONCRETE_POWDER, DyeColor.WHITE);
                put(Blocks.ORANGE_CONCRETE_POWDER, DyeColor.ORANGE);
                put(Blocks.MAGENTA_CONCRETE_POWDER, DyeColor.MAGENTA);
                put(Blocks.LIGHT_BLUE_CONCRETE_POWDER, DyeColor.LIGHT_BLUE);
                put(Blocks.YELLOW_CONCRETE_POWDER, DyeColor.YELLOW);
                put(Blocks.LIME_CONCRETE_POWDER, DyeColor.LIME);
                put(Blocks.PINK_CONCRETE_POWDER, DyeColor.PINK);
                put(Blocks.GRAY_CONCRETE_POWDER, DyeColor.GRAY);
                put(Blocks.LIGHT_GRAY_CONCRETE_POWDER, DyeColor.LIGHT_GRAY);
                put(Blocks.CYAN_CONCRETE_POWDER, DyeColor.CYAN);
                put(Blocks.PURPLE_CONCRETE_POWDER, DyeColor.PURPLE);
                put(Blocks.BLUE_CONCRETE_POWDER, DyeColor.BLUE);
                put(Blocks.BROWN_CONCRETE_POWDER, DyeColor.BROWN);
                put(Blocks.GREEN_CONCRETE_POWDER, DyeColor.GREEN);
                put(Blocks.RED_CONCRETE_POWDER, DyeColor.RED);
            }
        });

        registerSuckingConversion(64, ModBlocks.WORN_CONCRETE, new HashMap<>() {
            {
                put(Blocks.BLACK_CONCRETE, DyeColor.BLACK);
                put(Blocks.WHITE_CONCRETE, DyeColor.WHITE);
                put(Blocks.ORANGE_CONCRETE, DyeColor.ORANGE);
                put(Blocks.MAGENTA_CONCRETE, DyeColor.MAGENTA);
                put(Blocks.LIGHT_BLUE_CONCRETE, DyeColor.LIGHT_BLUE);
                put(Blocks.YELLOW_CONCRETE, DyeColor.YELLOW);
                put(Blocks.LIME_CONCRETE, DyeColor.LIME);
                put(Blocks.PINK_CONCRETE, DyeColor.PINK);
                put(Blocks.GRAY_CONCRETE, DyeColor.GRAY);
                put(Blocks.LIGHT_GRAY_CONCRETE, DyeColor.LIGHT_GRAY);
                put(Blocks.CYAN_CONCRETE, DyeColor.CYAN);
                put(Blocks.PURPLE_CONCRETE, DyeColor.PURPLE);
                put(Blocks.BLUE_CONCRETE, DyeColor.BLUE);
                put(Blocks.BROWN_CONCRETE, DyeColor.BROWN);
                put(Blocks.GREEN_CONCRETE, DyeColor.GREEN);
                put(Blocks.RED_CONCRETE, DyeColor.RED);
            }
        });

        registerSuckingConversion(32, Blocks.DEAD_BUSH, new HashMap<>() {
            {
                put(Blocks.POPPY, DyeColor.RED);
                put(Blocks.DANDELION, DyeColor.YELLOW);
                put(Blocks.ALLIUM, DyeColor.MAGENTA);
                put(Blocks.AZURE_BLUET, DyeColor.LIGHT_GRAY);
                put(Blocks.RED_TULIP, DyeColor.RED);
                put(Blocks.ORANGE_TULIP, DyeColor.ORANGE);
                put(Blocks.WHITE_TULIP, DyeColor.LIGHT_GRAY);
                put(Blocks.PINK_TULIP, DyeColor.PINK);
                put(Blocks.OXEYE_DAISY, DyeColor.LIGHT_GRAY);
                put(Blocks.CORNFLOWER, DyeColor.BLUE);
                put(Blocks.LILY_OF_THE_VALLEY, DyeColor.WHITE);
                put(Blocks.TORCHFLOWER, DyeColor.ORANGE);
                put(Blocks.WITHER_ROSE, DyeColor.BLACK);
            }
        });
        registerSuckingConversion(128, Blocks.DEAD_BUSH, Map.of(Blocks.BLUE_ORCHID, DyeColor.LIGHT_BLUE));
        registerSuckingConversion(32, Blocks.POTTED_DEAD_BUSH, new HashMap<>() {
            {
                put(Blocks.POTTED_POPPY, DyeColor.RED);
                put(Blocks.POTTED_DANDELION, DyeColor.YELLOW);
                put(Blocks.POTTED_ALLIUM, DyeColor.MAGENTA);
                put(Blocks.POTTED_AZURE_BLUET, DyeColor.LIGHT_GRAY);
                put(Blocks.POTTED_RED_TULIP, DyeColor.RED);
                put(Blocks.POTTED_ORANGE_TULIP, DyeColor.ORANGE);
                put(Blocks.POTTED_WHITE_TULIP, DyeColor.LIGHT_GRAY);
                put(Blocks.POTTED_PINK_TULIP, DyeColor.PINK);
                put(Blocks.POTTED_OXEYE_DAISY, DyeColor.LIGHT_GRAY);
                put(Blocks.POTTED_CORNFLOWER, DyeColor.BLUE);
                put(Blocks.POTTED_LILY_OF_THE_VALLEY, DyeColor.WHITE);
                put(Blocks.POTTED_TORCHFLOWER, DyeColor.ORANGE);
                put(Blocks.POTTED_WITHER_ROSE, DyeColor.BLACK);
            }
        });
        registerSuckingConversion(128, Blocks.POTTED_DEAD_BUSH, Map.of(Blocks.POTTED_BLUE_ORCHID, DyeColor.LIGHT_BLUE));

        registerSuckingConversion(512, Blocks.DEAD_TUBE_CORAL_BLOCK, Map.of(Blocks.TUBE_CORAL_BLOCK, DyeColor.PINK));
        registerSuckingConversion(512, Blocks.DEAD_BRAIN_CORAL_BLOCK, Map.of(Blocks.BRAIN_CORAL_BLOCK, DyeColor.PINK));
        registerSuckingConversion(512, Blocks.DEAD_BUBBLE_CORAL_BLOCK, Map.of(Blocks.BUBBLE_CORAL_BLOCK, DyeColor.PINK));
        registerSuckingConversion(512, Blocks.DEAD_FIRE_CORAL_BLOCK, Map.of(Blocks.FIRE_CORAL_BLOCK, DyeColor.PINK));
        registerSuckingConversion(512, Blocks.DEAD_HORN_CORAL_BLOCK, Map.of(Blocks.HORN_CORAL_BLOCK, DyeColor.PINK));
        registerSuckingConversion(128, Blocks.DEAD_TUBE_CORAL, Map.of(Blocks.TUBE_CORAL, DyeColor.PINK));
        registerSuckingConversion(128, Blocks.DEAD_BRAIN_CORAL, Map.of(Blocks.BRAIN_CORAL, DyeColor.PINK));
        registerSuckingConversion(128, Blocks.DEAD_BUBBLE_CORAL, Map.of(Blocks.BUBBLE_CORAL, DyeColor.PINK));
        registerSuckingConversion(128, Blocks.DEAD_FIRE_CORAL, Map.of(Blocks.FIRE_CORAL, DyeColor.PINK));
        registerSuckingConversion(128, Blocks.DEAD_HORN_CORAL, Map.of(Blocks.HORN_CORAL, DyeColor.PINK));
        registerSuckingConversion(128, Blocks.DEAD_TUBE_CORAL_FAN, Map.of(Blocks.TUBE_CORAL_FAN, DyeColor.PINK));
        registerSuckingConversion(128, Blocks.DEAD_BRAIN_CORAL_FAN, Map.of(Blocks.BRAIN_CORAL_FAN, DyeColor.PINK));
        registerSuckingConversion(128, Blocks.DEAD_BUBBLE_CORAL_FAN, Map.of(Blocks.BUBBLE_CORAL_FAN, DyeColor.PINK));
        registerSuckingConversion(128, Blocks.DEAD_FIRE_CORAL_FAN, Map.of(Blocks.FIRE_CORAL_FAN, DyeColor.PINK));
        registerSuckingConversion(128, Blocks.DEAD_HORN_CORAL_FAN, Map.of(Blocks.HORN_CORAL_FAN, DyeColor.PINK));

        registerSuckingConversion(512, Blocks.PACKED_ICE, Map.of(Blocks.BLUE_ICE, DyeColor.BLUE));
        registerSuckingConversion(512, Blocks.STONE, Map.of(Blocks.REDSTONE_BLOCK, DyeColor.RED));

        registerSuckingConversion(512, Blocks.SAND, Map.of(Blocks.RED_SAND, DyeColor.RED));
        registerSuckingConversion(512, Blocks.SANDSTONE, Map.of(Blocks.RED_SANDSTONE, DyeColor.RED));
        registerSuckingConversion(384, Blocks.SANDSTONE_STAIRS, Map.of(Blocks.RED_SANDSTONE_STAIRS, DyeColor.RED));
        registerSuckingConversion(256, Blocks.SANDSTONE_SLAB, Map.of(Blocks.RED_SANDSTONE_SLAB, DyeColor.RED));
        registerSuckingConversion(256, Blocks.SANDSTONE_WALL, Map.of(Blocks.RED_SANDSTONE_WALL, DyeColor.RED));
        registerSuckingConversion(512, Blocks.CHISELED_SANDSTONE, Map.of(Blocks.CHISELED_RED_SANDSTONE, DyeColor.RED));
        registerSuckingConversion(512, Blocks.SMOOTH_SANDSTONE, Map.of(Blocks.SMOOTH_RED_SANDSTONE, DyeColor.RED));
        registerSuckingConversion(384, Blocks.SMOOTH_SANDSTONE_STAIRS, Map.of(Blocks.SMOOTH_RED_SANDSTONE_STAIRS, DyeColor.RED));
        registerSuckingConversion(256, Blocks.SMOOTH_SANDSTONE_SLAB, Map.of(Blocks.SMOOTH_RED_SANDSTONE_SLAB, DyeColor.RED));
        registerSuckingConversion(512, Blocks.CUT_SANDSTONE, Map.of(Blocks.CUT_RED_SANDSTONE, DyeColor.RED));
        registerSuckingConversion(256, Blocks.CUT_SANDSTONE_SLAB, Map.of(Blocks.CUT_RED_SANDSTONE_SLAB, DyeColor.RED));

        registerSuckingConversion(512, Blocks.NETHER_BRICKS, Map.of(Blocks.RED_NETHER_BRICKS, DyeColor.RED));
        registerSuckingConversion(384, Blocks.NETHER_BRICK_STAIRS, Map.of(Blocks.RED_NETHER_BRICK_STAIRS, DyeColor.RED));
        registerSuckingConversion(256, Blocks.NETHER_BRICK_SLAB, Map.of(Blocks.RED_NETHER_BRICK_SLAB, DyeColor.RED));
        registerSuckingConversion(256, Blocks.NETHER_BRICK_WALL, Map.of(Blocks.RED_NETHER_BRICK_WALL, DyeColor.RED));
    }

    public void registerSuckingConversion(float manaAmount, Block to, Map<Block, DyeColor> from) {
        for (var conversion : from.entrySet()) {
            var builder = getOrCreateConversion(conversion.getKey());
            builder.add(to, true, 1, conversion.getValue().getEntityColor(), manaAmount);
        }
    }
}
