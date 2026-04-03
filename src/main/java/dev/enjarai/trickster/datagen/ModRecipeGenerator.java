package dev.enjarai.trickster.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.StonecuttingRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.item.Items.*;
import static dev.enjarai.trickster.item.ModItems.*;

public class ModRecipeGenerator extends FabricRecipeProvider {

    public static final List<Pair<Item, Item>> TUFF_TO_GILDED = List.of(
        new Pair<>(TUFF, GILDED_TUFF),
        new Pair<>(TUFF_STAIRS, GILDED_TUFF_STAIRS),
        new Pair<>(TUFF_SLAB, GILDED_TUFF_SLAB),
        new Pair<>(TUFF_WALL, GILDED_TUFF_WALL),
        new Pair<>(POLISHED_TUFF, GILDED_POLISHED_TUFF),
        new Pair<>(POLISHED_TUFF_STAIRS, GILDED_POLISHED_TUFF_STAIRS),
        new Pair<>(POLISHED_TUFF_SLAB, GILDED_POLISHED_TUFF_SLAB),
        new Pair<>(POLISHED_TUFF_WALL, GILDED_POLISHED_TUFF_WALL),
        new Pair<>(TUFF_BRICKS, GILDED_TUFF_BRICKS),
        new Pair<>(TUFF_BRICK_STAIRS, GILDED_TUFF_BRICK_STAIRS),
        new Pair<>(TUFF_BRICK_SLAB, GILDED_TUFF_BRICK_SLAB),
        new Pair<>(TUFF_BRICK_WALL, GILDED_TUFF_BRICK_WALL),
        new Pair<>(CHISELED_TUFF, GILDED_CHISELED_TUFF),
        new Pair<>(CHISELED_TUFF_BRICKS, GILDED_CHISELED_TUFF_BRICKS)
    );

    public static final List<Item> FULL_BLOCKS = List.of(
        GILDED_TUFF,
        GILDED_POLISHED_TUFF,
        GILDED_TUFF_BRICKS,
        GILDED_CHISELED_TUFF,
        GILDED_CHISELED_TUFF_BRICKS
    );

    public static final Map<Item, Item> STAIRS = Map.of(
        GILDED_TUFF, GILDED_TUFF_STAIRS,
        GILDED_POLISHED_TUFF, GILDED_POLISHED_TUFF_STAIRS,
        GILDED_TUFF_BRICKS, GILDED_TUFF_BRICK_STAIRS
    );

    public static final Map<Item, Item> SLABS = Map.of(
        GILDED_TUFF, GILDED_TUFF_SLAB,
        GILDED_POLISHED_TUFF, GILDED_POLISHED_TUFF_SLAB,
        GILDED_TUFF_BRICKS, GILDED_TUFF_BRICK_SLAB
    );

    public static final Map<Item, Item> WALLS = Map.of(
        GILDED_TUFF, GILDED_TUFF_WALL,
        GILDED_POLISHED_TUFF, GILDED_POLISHED_TUFF_WALL,
        GILDED_TUFF_BRICKS, GILDED_TUFF_BRICK_WALL
    );

    public ModRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    public static Identifier toStonecuttingId(Item item) {
        return Registries.ITEM.getId(item).withSuffixedPath("_stonecutting");
    }

    public static Identifier toStonecuttingId(Item item, String suff) {
        return Registries.ITEM.getId(item).withSuffixedPath("_stonecutting_" + suff);
    }

    public static Identifier toGildingId(Item item) {
        return Registries.ITEM.getId(item).withSuffixedPath("_gilding");
    }

    @Override
    public void generate(RecipeExporter exporter) {
        for (Pair<Item, Item> entry : TUFF_TO_GILDED) {
            ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, entry.getRight(), 8)
                .pattern("bbb")
                .pattern("bgb")
                .pattern("bbb")
                .input('b', entry.getLeft())
                .input('g', GOLD_INGOT)
                .group("gilded_tuff")
                .criterion(FabricRecipeProvider.hasItem(entry.getLeft()), FabricRecipeProvider.conditionsFromItem(entry.getLeft()))
                .offerTo(exporter, toGildingId(entry.getRight()));
        }
        for (Item entry : FULL_BLOCKS) {
            StonecuttingRecipeJsonBuilder.createStonecutting(Ingredient.fromTag(GILDED_TUFF_FULL_BLOCKS), RecipeCategory.BUILDING_BLOCKS, entry)
                .criterion(GILDED_TUFF_FULL_BLOCKS.id().toString(), FabricRecipeProvider.conditionsFromTag(GILDED_TUFF_STAIR_BLOCKS))
                .offerTo(exporter, toStonecuttingId(entry));
        }
        for (Map.Entry<Item, Item> entry : STAIRS.entrySet()) {
            StonecuttingRecipeJsonBuilder.createStonecutting(Ingredient.fromTag(GILDED_TUFF_FULL_BLOCKS), RecipeCategory.BUILDING_BLOCKS, entry.getValue())
                .criterion(GILDED_TUFF_FULL_BLOCKS.id().toString(), FabricRecipeProvider.conditionsFromTag(GILDED_TUFF_STAIR_BLOCKS))
                .offerTo(exporter, toStonecuttingId(entry.getValue()));
            StonecuttingRecipeJsonBuilder.createStonecutting(Ingredient.fromTag(GILDED_TUFF_STAIR_BLOCKS), RecipeCategory.BUILDING_BLOCKS, entry.getValue())
                .criterion(GILDED_TUFF_STAIR_BLOCKS.id().toString(), FabricRecipeProvider.conditionsFromTag(GILDED_TUFF_STAIR_BLOCKS))
                .offerTo(exporter, toStonecuttingId(entry.getValue(), "stairs"));

            ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, entry.getValue(), 4)
                .pattern("b  ")
                .pattern("bb ")
                .pattern("bbb")
                .input('b', entry.getKey())
                .group("gilded_tuff_stairs")
                .criterion(FabricRecipeProvider.hasItem(entry.getKey()), FabricRecipeProvider.conditionsFromItem(entry.getKey()))
                .offerTo(exporter);
        }
        for (Map.Entry<Item, Item> entry : SLABS.entrySet()) {
            StonecuttingRecipeJsonBuilder.createStonecutting(Ingredient.fromTag(GILDED_TUFF_FULL_BLOCKS), RecipeCategory.BUILDING_BLOCKS, entry.getValue(), 2)
                .criterion(GILDED_TUFF_FULL_BLOCKS.id().toString(), FabricRecipeProvider.conditionsFromTag(GILDED_TUFF_STAIR_BLOCKS))
                .offerTo(exporter, toStonecuttingId(entry.getValue()));
            StonecuttingRecipeJsonBuilder.createStonecutting(Ingredient.fromTag(GILDED_TUFF_SLABS), RecipeCategory.BUILDING_BLOCKS, entry.getValue())
                .criterion(GILDED_TUFF_SLABS.id().toString(), FabricRecipeProvider.conditionsFromTag(GILDED_TUFF_SLABS))
                .offerTo(exporter, toStonecuttingId(entry.getValue(), "slabs"));
            ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, entry.getValue(), 6)
                .pattern("bbb")
                .input('b', entry.getKey())
                .group("gilded_tuff_slabss")
                .criterion(FabricRecipeProvider.hasItem(entry.getKey()), FabricRecipeProvider.conditionsFromItem(entry.getKey()))
                .offerTo(exporter);
        }
        for (Map.Entry<Item, Item> entry : WALLS.entrySet()) {
            StonecuttingRecipeJsonBuilder.createStonecutting(Ingredient.fromTag(GILDED_TUFF_FULL_BLOCKS), RecipeCategory.BUILDING_BLOCKS, entry.getValue())
                .criterion(GILDED_TUFF_FULL_BLOCKS.id().toString(), FabricRecipeProvider.conditionsFromTag(GILDED_TUFF_STAIR_BLOCKS))
                .offerTo(exporter, toStonecuttingId(entry.getValue()));
            StonecuttingRecipeJsonBuilder.createStonecutting(Ingredient.fromTag(GILDED_TUFF_WALLS), RecipeCategory.BUILDING_BLOCKS, entry.getValue())
                .criterion(GILDED_TUFF_WALLS.id().toString(), FabricRecipeProvider.conditionsFromTag(GILDED_TUFF_WALLS))
                .offerTo(exporter, toStonecuttingId(entry.getValue(), "walls"));
            ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, entry.getValue(), 6)
                .pattern("   ")
                .pattern("bbb")
                .pattern("bbb")
                .input('b', entry.getKey())
                .group("gilded_tuff_walls")
                .criterion(FabricRecipeProvider.hasItem(entry.getKey()), FabricRecipeProvider.conditionsFromItem(entry.getKey()))
                .offerTo(exporter);
        }
    }

}
